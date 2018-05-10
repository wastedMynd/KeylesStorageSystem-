package inc.wastedmynd.keylesstorage.ui.fragments.retrieval_fragments;

import android.app.Fragment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.things.contrib.driver.button.Button;
import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.UartDevice;
import com.nilhcem.androidthings.driver.keypad.Keypad;

import java.io.IOException;
import java.util.List;

import inc.wastedmynd.keylesstorage.R;
import inc.wastedmynd.keylesstorage.SerialCommunication.DoorStatus;
import inc.wastedmynd.keylesstorage.SerialCommunication.LockAccess;
import inc.wastedmynd.keylesstorage.SerialCommunication.ParcelStatus;
import inc.wastedmynd.keylesstorage.data.StorageUnit;
import inc.wastedmynd.keylesstorage.data.Token;
import inc.wastedmynd.keylesstorage.database.TokenDatabase;
import inc.wastedmynd.keylesstorage.ui.MainActivity;
import inc.wastedmynd.keylesstorage.ui.MainActivityListener;
import inc.wastedmynd.keylesstorage.ui.fragments.MainMenuFragment;
import inc.wastedmynd.keylesstorage.ui.utils.LogInfo;

public class RetrievalProcessFragment extends Fragment {

    //region ui views
    private MainActivityListener mainActivityListener;
    private EditText passphrase;
    private TextView unit;
    //endregion

    //region Token Database resources
    //Token Database
    private TokenDatabase database;
    //shopperToken
    private Token shopperToken;
    //Database Token
    private Token refToken;
    //endregion

    //region io components
    private Keypad keypad;
    private Button backBtn, doneBtn;
    private Handler uiHandler = new Handler();
    private Handler processHandler = new Handler();
    private final Runnable setupUi = new Runnable() {
        //region setup io
        String[] rowPins = new String[]{"BCM12", "BCM16", "BCM20", "BCM21"};
        String[] colPins = new String[]{"BCM25", "BCM24", "BCM23", "BCM5"};

        void initializeComponents() {
            try {
                backBtn = new Button("BCM4", Button.LogicState.PRESSED_WHEN_LOW);
                doneBtn = new Button("BCM27", Button.LogicState.PRESSED_WHEN_LOW);
                keypad = new Keypad(rowPins, colPins, Keypad.KEYS_4x4);
            }
            catch (IOException e)
            {
                new LogInfo("io_init").error(e.getMessage());
            }
        }

        @Override
        public void run() {

            //setup io
            initializeComponents();

            backBtn.setOnButtonEventListener(new Button.OnButtonEventListener() {
                @Override
                public void onButtonEvent(Button button, boolean pressed) {
                    mainActivityListener.onChangeFragment(new RetrieveFragment());
                }
            });

            //region doneBtn
            doneBtn.setOnButtonEventListener(new Button.OnButtonEventListener() {
                @Override
                public void onButtonEvent(Button button, boolean pressed) {

                    //authenticate shopper
                    if(!passphrase.getText().toString().equals(refToken.getPassPhrase()))
                    {
                        mainActivityListener.onChangeInstructText("password incorrect!");
                        passphrase.setText("");
                        return;
                    }

                    mainActivityListener.onChangeInstructText("password correct...\n Please goto your storage unit.");

                    processHandler.postDelayed(processAuthentication,2*(60*1000));
                    mainActivityListener.onChangeFragment(new MainMenuFragment());
                }
            });
            //endregion

            //region keypad events
            keypad.register(new Keypad.OnKeyEventListener() {
                @Override
                public void onKeyEvent(KeyEvent keyEvent) {
                    String action = keyEvent.getAction() == KeyEvent.ACTION_DOWN ? "ACTION_DOWN" : "ACTION_UP";

                    switch (action)
                    {
                        case "ACTION_DOWN" :
                            String keyPressed = keyEvent.getCharacters();

                            switch (keyPressed)
                            {
                                case "*"://backspace
                                    int l = passphrase.getText().length();
                                    passphrase.setText( passphrase.getText().replace(l-1,l,passphrase.getText()));
                                    break;

                                default:
                                    passphrase.setText(passphrase.getText().toString()+keyPressed);
                                    break;
                            }
                            break;

                        case "ACTION_UP":
                            break;
                    }

                    Log.i("Keypad", "onKeyEvent: (" + action + "): " + keyEvent.getDisplayLabel());
                }
            });
            //endregion

        }
        //endregion
    };
    private final Runnable processAuthentication = new Runnable() {

        void configureUartFrame(UartDevice uart) throws IOException {
            // Configure the UART port
            uart.setBaudrate(9600);//default(115200);
            uart.setDataSize(8);
            uart.setParity(UartDevice.PARITY_NONE);
            uart.setStopBits(1);
        }


        void writeUartData(UartDevice uart,char cmd) throws IOException {
            byte[] buffer =  String.valueOf(cmd).getBytes();
            int count = uart.write(buffer, buffer.length);
            Log.d("UART_SENDING", "Wrote " + count + " bytes to peripheral");
        }

        @Override
        public void run() {

            //region process Authentication and doneBtn storage unit
            //serial com
            UartDevice mDevice;
            // Attempt to access the UART device
            try
            {
                PeripheralManager manager = PeripheralManager.getInstance();
                List<String> deviceList = manager.getUartDeviceList();
                if (deviceList.isEmpty())return;

                mDevice = manager.openUartDevice(deviceList.get(0));
                configureUartFrame(mDevice);

                final StorageUnit unit = new StorageUnit(shopperToken);

                //region Unlock storage unit
                writeUartData(mDevice,unit.getLockAccess().getCommand());//Lock Access sent
                unit.getLockAccess().setLockStatus(LockAccess.LockStatus.unlock);
                writeUartData(mDevice,  unit.getLockAccess().getLockStatus().getCommand());//unlock command sent
                // TODO: 05/05/2018 tell the shopper, that their storage unit is unlocked
                //endregion

                boolean monitorStorage = true;
                //shopper behavior monitored
                boolean retrieved = true;
                boolean parcelPresent=true,doorClosed=true;

                long elapsedTime;
                int setTimer = getResources().getInteger(R.integer.retrievalMonitoringDuration); //10min
                long startTime = System.currentTimeMillis();
                final long discardTime = startTime + setTimer;


                while(monitorStorage)
                {

                    // Maximum amount of data to read at one time
                    final int maxCount = 1;
                    byte[] buffer = new byte[maxCount];
                    int count;


                    //region monitor storage unit door
                    writeUartData(mDevice, unit.getDoorStatus().getCommand());//Door State Monitor sent
                    //receiving data from micro controller
                    while ((count = mDevice.read(buffer, buffer.length)) > 0) {

                        char msg = Byte.toString(buffer[0]).toCharArray()[0];


                        doorClosed = (msg == DoorStatus.DoorResponses.closed.getDoorResponse());
                        unit.getDoorStatus().setDoorResponses(doorClosed?
                                        DoorStatus.DoorResponses.closed: DoorStatus.DoorResponses.open);

                        Log.d("UART_READING", "Read " + count + " bytes from peripheral");
                    }
                    //endregion

                    //region monitor storage unit parcel
                    writeUartData(mDevice, unit.getParcelStatus().getCommand());//Door State Monitor sent
                    //receiving data from micro controller
                    while ((count = mDevice.read(buffer, buffer.length)) > 0) {

                        char msg = Byte.toString(buffer[0]).toCharArray()[0];

                        parcelPresent = (msg == ParcelStatus.ParcelResponse.containing.getParcelResponse());
                        unit.getParcelStatus().setParcelResponse(parcelPresent?
                                        ParcelStatus.ParcelResponse.containing:ParcelStatus.ParcelResponse.empty);

                        Log.d("UART_READING", "Read " + count + " bytes from peripheral");
                    }
                    //endregion

                    //evaluating results
                    retrieved = !parcelPresent && doorClosed;
                    monitorStorage = !retrieved;

                    elapsedTime =  System.currentTimeMillis() - startTime;
                    if(elapsedTime>=discardTime)
                    {
                        retrieved = false;
                        monitorStorage =false;
                    }
                }

                if(retrieved) database.removeItem(refToken);

                if(unit.getDoorStatus().getDoorResponses().doorClosed())
                {
                    //region Lock storage unit
                    writeUartData(mDevice,unit.getLockAccess().getCommand());//Lock Access sent
                    unit.getLockAccess().setLockStatus(LockAccess.LockStatus.lock);
                    writeUartData(mDevice,  unit.getLockAccess().getLockStatus().getCommand());//unlock command sent
                    //endregion
                }
                // TODO: 05/05/2018 else report to shopper to closed the storage unit the door


                try
                {
                    mDevice.close();
                }
                catch (IOException e)
                {
                    new LogInfo("UART_CLOSE").error(e.getMessage());
                }

            }
            catch (IOException e)
            {
                Log.w("UART", "Unable to access UART device", e);
            }
            //endregion

        }
    };
    private void closeComponents() {
        try {
            backBtn.close();
            doneBtn.close();

            // Don't forget to:
            keypad.unregister();
            keypad.close();
        }
        catch (IOException e)
        {
            new LogInfo("compo_closed").error(e.getMessage());
        }
    }
    //endregion

    //region Fragment life cycle
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_retrival_process,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //setup ui views
        android.widget.Button mDoneBtn = view.findViewById(R.id.store_ui_btn);
        mDoneBtn.setCompoundDrawables(getResources().getDrawable(R.drawable.ic_lock_open_black_24dp,null),null,null,null);
        unit = view.findViewById(R.id.token_item_text);
        passphrase = view.findViewById(R.id.passPhrase);
        uiHandler.post(setupUi);

        //get shopper Token database and the shopper ref Token
        final TokenDatabase database = new TokenDatabase(getContext());

        //this ref Token will be used for comparison reason(s) by the authentication process
        refToken = (Token) database.getItems().get(database.getItems().indexOf(shopperToken));

        //database selected unit and pass phrase logged
        unit.setText(shopperToken.toString());
        passphrase.setText(shopperToken.getPassPhrase());

        //tell the shopper that their password will be hiden after 1 minute
        mainActivityListener.onChangeInstructText("Password will hide 1 min \n Please goto your Storage unit");

        //hide the shopper password after 1 minute
        new Handler().postDelayed(new Runnable() {@Override public void run() { unit.setText("");passphrase.setText(""); }},60*1000);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivityListener = (MainActivity) getActivity();

        //region get which token was selected
        shopperToken =new Token();
        shopperToken.setStorageUnit( getArguments().getInt("storage_unit",Token.INVALID_TOKEN));
        shopperToken.setPassPhrase(getArguments().getString("password"));
        //endregion
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeComponents();
    }
    //endregion
}
