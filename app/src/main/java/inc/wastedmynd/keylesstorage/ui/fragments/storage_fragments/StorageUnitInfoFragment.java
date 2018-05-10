package inc.wastedmynd.keylesstorage.ui.fragments.storage_fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.things.contrib.driver.button.Button;
import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.UartDevice;

import java.io.IOException;
import java.util.List;

import inc.wastedmynd.keylesstorage.R;
import inc.wastedmynd.keylesstorage.SerialCommunication.DoorStatus;
import inc.wastedmynd.keylesstorage.SerialCommunication.LockAccess.LockStatus;
import inc.wastedmynd.keylesstorage.SerialCommunication.ParcelStatus;
import inc.wastedmynd.keylesstorage.data.StorageUnit;
import inc.wastedmynd.keylesstorage.data.Token;
import inc.wastedmynd.keylesstorage.database.TokenDatabase;
import inc.wastedmynd.keylesstorage.ui.MainActivityListener;
import inc.wastedmynd.keylesstorage.ui.MainActivity;
import inc.wastedmynd.keylesstorage.ui.fragments.MainMenuFragment;
import inc.wastedmynd.keylesstorage.ui.utils.LogInfo;

public class StorageUnitInfoFragment extends Fragment {

    //region variables
    private TextView passPhrase,unit;
    private MainActivityListener mainActivityListener;

    private TokenDatabase tokenDatabase;
    private Token shopperToken;

    //--Buttons
    private Button backBtn, doneBtn;
    private void initializeComponents() throws IOException{
        backBtn = new Button("BCM4", Button.LogicState.PRESSED_WHEN_LOW);
        doneBtn = new Button("BCM17", Button.LogicState.PRESSED_WHEN_LOW);
        Button btn1 = new Button("BCM27", Button.LogicState.PRESSED_WHEN_LOW);
        Button btn2 = new Button("BCM22", Button.LogicState.PRESSED_WHEN_LOW);
        Button btn3 = new Button("BCM23", Button.LogicState.PRESSED_WHEN_LOW);

        btn1.close();
        btn2.close();
        btn3.close();
    }
    private void closeComponents()throws  IOException{
        backBtn.close();
        doneBtn.close();
    }
    private Handler asyncHandler = new Handler();
    private Handler uiHandler = new Handler();
    private Runnable processShopperToken = new Runnable() {

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

            //region process shopper Token

            //insert shop token to tokenDatabase
            tokenDatabase.addItem(shopperToken);

            //waiting for shopper to reach their storage unit
            try { Thread.sleep(getResources().getInteger(R.integer.shopperLocateUnitDelay)); } catch (InterruptedException e) { e.printStackTrace(); }
            // TODO: 05/05/2018 tell the shopper, that their storage unit is about to be unlocked

            //region start serial communication

                    //region Attempt to access the UART device
                    UartDevice mDevice;
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
                        unit.getLockAccess().setLockStatus(LockStatus.unlock);
                        writeUartData(mDevice,  unit.getLockAccess().getLockStatus().getCommand());//unlock command sent
                        // TODO: 05/05/2018 tell the shopper, that their storage unit is unlocked
                        //endregion

                        //region shopper behavior monitored
                        boolean stored = false;

                        //region monitor storage unit
                        long elapsedTime;
                        int setTimer = getResources().getInteger(R.integer.storageMonitoringDuration); //5min
                        long startTime = System.currentTimeMillis();
                        final long discardTime = startTime + setTimer;

                        boolean monitorStorage = true;
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


                                unit.getDoorStatus().setDoorResponses(
                                        (msg == DoorStatus.DoorResponses.closed.getDoorResponse())?
                                                DoorStatus.DoorResponses.closed: DoorStatus.DoorResponses.open);

                                Log.d("UART_READING", "Read " + count + " bytes from peripheral");
                            }
                            //endregion

                            //region monitor storage unit parcel
                            writeUartData(mDevice, unit.getParcelStatus().getCommand());//Door State Monitor sent
                            //receiving data from micro controller
                            while ((count = mDevice.read(buffer, buffer.length)) > 0) {

                                char msg = Byte.toString(buffer[0]).toCharArray()[0];

                                unit.getParcelStatus()
                                        .setParcelResponse((msg == ParcelStatus.ParcelResponse.containing.getParcelResponse())?
                                        ParcelStatus.ParcelResponse.containing:ParcelStatus.ParcelResponse.empty);

                                Log.d("UART_READING", "Read " + count + " bytes from peripheral");
                            }
                            //endregion

                            //region evaluating storage unit state
                            stored = !(unit.getParcelStatus().getParcelResponse().isStorageEmpty()) &&
                                       unit.getDoorStatus().getDoorResponses().doorClosed();

                            monitorStorage = !stored;

                            elapsedTime =  System.currentTimeMillis() - startTime;
                            if(elapsedTime>=discardTime)
                            {
                                stored = false;
                                monitorStorage =false;
                            }
                            //endregion
                        }
                        //endregion

                        //remove shopper Token if parcel not stored
                        if(!stored) tokenDatabase.removeItem(shopperToken);

                        if(unit.getDoorStatus().getDoorResponses().doorClosed())
                        {
                            //region Lock storage unit
                            writeUartData(mDevice,unit.getLockAccess().getCommand());//Lock Access sent
                            unit.getLockAccess().setLockStatus(LockStatus.lock);
                            writeUartData(mDevice,  unit.getLockAccess().getLockStatus().getCommand());//unlock command sent
                            //endregion
                        }
                        // TODO: 05/05/2018 else report to shopper to closed the storage unit the door

                        //endregion

                        try
                        {
                            mDevice.close();
                        }
                        catch (IOException e)
                        {
                            new LogInfo("DCE").error(e.getMessage());
                        }

                    }
                    catch (IOException e)
                    {
                        Log.w("UART", "Unable to access UART device", e);
                    }
                    //endregion

            //endregion

            //endregion
        }
    };
    private  final  Runnable setupIO = new Runnable() {
        @Override
        public void run() {
            //region setup IO
            try
            {

                initializeComponents();

                backBtn.setOnButtonEventListener(new Button.OnButtonEventListener() {
                    @Override
                    public void onButtonEvent(Button button, boolean pressed) {
                        StoreFragment fragment = new StoreFragment();
                        mainActivityListener.onChangeFragment(fragment);
                    }
                });


                doneBtn.setOnButtonEventListener(new Button.OnButtonEventListener() {
                    @Override
                    public void onButtonEvent(Button button, boolean pressed) {

                        //process shopper token asynchronously
                        asyncHandler.post(processShopperToken);

                        //then redirect to main menu
                        mainActivityListener.onChangeFragment(new MainMenuFragment());
                    }
                });

            }
            catch (IOException e) {
                e.printStackTrace();
            }
            //endregion
        }
    };

    //endregion

    //region Fragment life cycle
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_display_pass_phrase,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //initializing
        android.widget.Button mDoneBtn = view.findViewById(R.id.store_ui_btn);
        mDoneBtn.setCompoundDrawables(getResources().getDrawable(R.drawable.ic_lock_outline_black_24dp,null),null,null,null);
        unit = view.findViewById(R.id.token_item_text);
        passPhrase = view.findViewById(R.id.passPhrase);

        tokenDatabase = new TokenDatabase(getContext());
        uiHandler.post(setupIO);

        //showing shopper token info
        unit.setText(shopperToken.toString());
        passPhrase.setText(shopperToken.getPassPhrase());

        //hiding shopper password
        mainActivityListener.onChangeInstructText(getResources().getString(R.string.storage_unit_info));
        new Handler().postDelayed(new Runnable() {@Override public void run() { unit.setText("");passPhrase.setText(""); }},getResources().getInteger(R.integer.hideShopperPassphrase));

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setMainActivityListener((MainActivity)getActivity());
        mainActivityListener.onDefaultBackDone();

        //region get selected shopper token
        shopperToken =new Token();
        shopperToken.setStorageUnit( getArguments().getInt("storage_unit"));
        shopperToken.setPassPhrase(getArguments().getString("passPhrase"));
        //endregion
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try { closeComponents(); } catch (IOException e) { e.printStackTrace(); }
    }
    //endregion

    //region getters and setters
    private void setMainActivityListener(MainActivityListener mainActivityListener) {
        this.mainActivityListener = mainActivityListener;
    }
    //endregion
}
