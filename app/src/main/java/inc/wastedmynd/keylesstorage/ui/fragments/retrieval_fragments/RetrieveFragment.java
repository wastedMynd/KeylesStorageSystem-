package inc.wastedmynd.keylesstorage.ui.fragments.retrieval_fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.things.contrib.driver.button.Button;
import com.nilhcem.androidthings.driver.keypad.Keypad;

import java.io.IOException;

import inc.wastedmynd.keylesstorage.R;
import inc.wastedmynd.keylesstorage.data.Token;
import inc.wastedmynd.keylesstorage.database.TokenDatabase;
import inc.wastedmynd.keylesstorage.ui.MainActivity;
import inc.wastedmynd.keylesstorage.ui.MainActivityListener;
import inc.wastedmynd.keylesstorage.ui.adapters.RetrievedTokenAdapter;
import inc.wastedmynd.keylesstorage.ui.adapters.StoredTokenAdapter;
import inc.wastedmynd.keylesstorage.ui.adapters.ViewHolder.ContentViewHolder;
import inc.wastedmynd.keylesstorage.ui.fragments.MainMenuFragment;
import inc.wastedmynd.keylesstorage.ui.fragments.storage_fragments.StorageUnitInfoFragment;
import inc.wastedmynd.keylesstorage.ui.utils.CrossFadeUtils;
import inc.wastedmynd.keylesstorage.ui.utils.LogInfo;

public class RetrieveFragment extends Fragment {

    //region ui view and resources
    private ContentViewHolder contentViewHolder;
    private MainActivityListener mainActivityListener;
    private RetrievedTokenAdapter adapter;
    //endregion

    //region Token database resources
    private TokenDatabase database;
    private Token shopperToken;
    //endregion

    //region io
    private Button nextBtn,backBtn;
    private Keypad keypad;
    private Handler ioSetupHandle= new Handler();
    private final Runnable ioSetup = new Runnable() {
        String[] rowPins = new String[]{"BCM12", "BCM16", "BCM20", "BCM21"};
        String[] colPins = new String[]{"BCM25", "BCM24", "BCM23", "BCM5"};

        void initializeComponents() throws IOException{

                nextBtn = new Button("BCM25", com.google.android.things.contrib.driver.button.Button.LogicState.PRESSED_WHEN_LOW);
                backBtn = new Button("BCM4", Button.LogicState.PRESSED_WHEN_LOW);
                keypad = new Keypad(rowPins, colPins, Keypad.KEYS_4x4);
        }

        @Override
        public void run() {

            try { initializeComponents(); } catch (IOException e) { new LogInfo("io_init").error(e.getMessage()); }

            //region next button events
            nextBtn.setOnButtonEventListener(new com.google.android.things.contrib.driver.button.Button.OnButtonEventListener() {
                @Override
                public void onButtonEvent(com.google.android.things.contrib.driver.button.Button button, boolean pressed) {

                    if (adapter.getSelectedToken() == null) return;

                    RetrievalProcessFragment fragment = new RetrievalProcessFragment();
                    Bundle args = new Bundle();
                    args.putInt("storage_unit",adapter.getSelectedToken().getStorageUnit());
                    args.putString("password",adapter.getSelectedToken().getPassPhrase());
                    fragment.setArguments(args);
                    mainActivityListener.onChangeFragment(fragment);
                }
            });
            //endregion

            //region back button events
            backBtn.setOnButtonEventListener(new Button.OnButtonEventListener() {
                @Override
                public void onButtonEvent(Button button, boolean pressed) {
                    MainMenuFragment fragment = new MainMenuFragment();
                    mainActivityListener.onChangeFragment(fragment);
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
                                //invalid keys
                                case "A":
                                case "B":
                                case "C":
                                case "D":
                                case "*":
                                case "#":
                                    StringBuilder text = new StringBuilder("You Pressed an invalid!");
                                    text.append("\n");
                                    text.append("Only Numeric keys are supported...");
                                    mainActivityListener.onChangeInstructText(text.toString());
                                    break;

                                default:
                                    shopperToken = adapter.selectToken(convertNumericKeyPressToInt(keyPressed));
                                    text = new StringBuilder("You have selected Storage Unit : ");
                                    text.append(shopperToken.getStorageUnit());
                                    text.append("\n");
                                    text.append("Please Press \'<b>Next</b>\' to Confirm your selected unit...");
                                    mainActivityListener.onChangeInstructText(text.toString());
                                    break;
                            }
                            break;

                        case "ACTION_UP":
                            break;
                    }

                    Log.i("Keypad", "onKeyEvent: (" + action + "): " + keyEvent.getDisplayLabel());
                }

                int convertNumericKeyPressToInt(String keyPressed)
                {
                    int numericKeyPressed = 0;
                    for(int i=0;i<10;i++) {
                        if (keyPressed.equals(String.valueOf(i)))
                        {
                            numericKeyPressed = i ;
                            break;
                        }
                    }

                    return numericKeyPressed;

                }

            });
            //endregion

        }
    };
    //endregion

    //region fragment life cycle
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_retrieve,container,false);
        contentViewHolder = new ContentViewHolder(root);
        return root;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        contentViewHolder.crossFadeUtils = new CrossFadeUtils(contentViewHolder.contentRecycler, contentViewHolder.contentLoader);
        contentViewHolder.contentLoaderInfo.setText(R.string.content_loader_processing);

        ioSetupHandle.post(ioSetup);
        database = new TokenDatabase(getContext());
        adapter = new RetrievedTokenAdapter(getContext());
        contentViewHolder.contentRecycler.setAdapter(adapter);
        contentViewHolder.contentRecycler.setLayoutManager(new GridLayoutManager(getContext(),3));

        contentViewHolder.contentLoaderInfo.setText(R.string.content_loader_done);
        contentViewHolder.crossFadeUtils.crossfade();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivityListener = (MainActivity)getActivity();
        mainActivityListener.onDefaultBackNext();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            nextBtn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //endregion

}
