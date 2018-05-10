package inc.wastedmynd.keylesstorage.ui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.things.contrib.driver.button.Button;

import java.io.IOException;

import inc.wastedmynd.keylesstorage.R;
import inc.wastedmynd.keylesstorage.ui.fragments.AboutUsFragment;
import inc.wastedmynd.keylesstorage.ui.fragments.HelpFragment;
import inc.wastedmynd.keylesstorage.ui.fragments.MainMenuFragment;
import inc.wastedmynd.keylesstorage.ui.fragments.retrieval_fragments.RetrievalProcessFragment;
import inc.wastedmynd.keylesstorage.ui.fragments.retrieval_fragments.RetrieveFragment;
import inc.wastedmynd.keylesstorage.ui.fragments.storage_fragments.StorageUnitInfoFragment;
import inc.wastedmynd.keylesstorage.ui.fragments.storage_fragments.StoreFragment;
import inc.wastedmynd.keylesstorage.ui.utils.LogInfo;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 * <p>
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
public class MainActivity extends Activity implements MainActivityListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private LogInfo log  = new LogInfo(TAG);

    //ui
    public MainNavigationHelper mainNavigationHelper;
    private static MainMenuFragment mainMenuFragment;
    private static AboutUsFragment aboutUsFragment;
    private static StoreFragment storeFragment;
    private static HelpFragment helpFragment;
    private static RetrieveFragment retrieveFragment;

    //--Buttons
    private Button  main_menu_io_btn, store_parcel_io_btn, retrieve_parcel_io_btn, help_io_btn, about_us_io_btn;
    private void initializeComponents() throws IOException{
        main_menu_io_btn = new Button("BCM4", Button.LogicState.PRESSED_WHEN_LOW);
        store_parcel_io_btn = new Button("BCM17", Button.LogicState.PRESSED_WHEN_LOW);
        retrieve_parcel_io_btn = new Button("BCM27", Button.LogicState.PRESSED_WHEN_LOW);
        help_io_btn = new Button("BCM22", Button.LogicState.PRESSED_WHEN_LOW);
        about_us_io_btn = new Button("BCM23", Button.LogicState.PRESSED_WHEN_LOW);
    }
    private void closeComponents()throws  IOException{
        main_menu_io_btn.close();
        retrieve_parcel_io_btn.close();
        help_io_btn.close();
        about_us_io_btn.close();
        store_parcel_io_btn.close();
    }
    private Handler asyncHandler = new Handler();

    //fragment change listener
    private static MainActivityListener fragmentChangeListener;


    private void removeUITitleBar()
    {
        //set up notitle
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //set up full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        removeUITitleBar();
        setContentView(R.layout.activity_main);
        log.display("Raspberry Pi Started");

        mainNavigationHelper = new MainNavigationHelper(findViewById(R.id.main));

        if (findViewById(R.id.fragment_container) != null) {

            if (savedInstanceState != null) {
                return;
            }

            mainMenuFragment = new MainMenuFragment();
            Fragment fragment = mainMenuFragment;
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container,fragment,"mainMenuFragment")
                    .commit();

            log.display("Re-entered onCreate");
        }



        fragmentChangeListener = this;
        asyncHandler.post(setupIO);
        log.display("Exited the onCreate");
    }


    //region io main items button clicks
    private  final  Runnable setupIO = new Runnable() {
        @Override
        public void run() {
            try
            {
                initializeComponents();

                about_us_io_btn.setOnButtonEventListener(new Button.OnButtonEventListener() {
                    @Override
                    public void onButtonEvent(Button button, boolean pressed) {
                        if( aboutUsFragment ==null) aboutUsFragment= new AboutUsFragment();
                        fragmentChangeListener.onChangeFragment(aboutUsFragment);
                    }
                });


                main_menu_io_btn.setOnButtonEventListener(new Button.OnButtonEventListener() {
                    @Override
                    public void onButtonEvent(Button button, boolean pressed) {
                        if( mainMenuFragment ==null) mainMenuFragment= new MainMenuFragment();
                        fragmentChangeListener.onChangeFragment(mainMenuFragment);
                    }
                });

                store_parcel_io_btn.setOnButtonEventListener(new Button.OnButtonEventListener() {
                    @Override
                    public void onButtonEvent(Button button, boolean pressed) {
                        if( storeFragment ==null) storeFragment= new StoreFragment();
                        fragmentChangeListener.onChangeFragment(storeFragment);
                    }
                });

                retrieve_parcel_io_btn.setOnButtonEventListener(new Button.OnButtonEventListener() {
                    @Override
                    public void onButtonEvent(Button button, boolean pressed) {
                        if( retrieveFragment ==null) retrieveFragment= new RetrieveFragment();
                        fragmentChangeListener.onChangeFragment(retrieveFragment);
                    }
                });

                help_io_btn.setOnButtonEventListener(new Button.OnButtonEventListener() {
                    @Override
                    public void onButtonEvent(Button button, boolean pressed) {
                        if( helpFragment ==null) helpFragment= new HelpFragment();
                        fragmentChangeListener.onChangeFragment(helpFragment);
                    }
                });

            }
            catch (IOException e) {
                e.printStackTrace();
            }


        }
    };
    //endregion

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        log.display("Exited the onDestroy");
        try
        {
           closeComponents();
        }
        catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onChangeFragment(Fragment fragment) {

        if(fragment instanceof MainMenuFragment){
            mainNavigationHelper.defaultNavigate();
        }
        else if(fragment instanceof StoreFragment){
            mainNavigationHelper.defaultToBackNext();

        }else if(fragment instanceof RetrieveFragment){
            mainNavigationHelper.defaultToBackNext();

        }else if(fragment instanceof StorageUnitInfoFragment){
            mainNavigationHelper.defaultToBackDone();

        }else if(fragment instanceof RetrievalProcessFragment){
            mainNavigationHelper.defaultToBackDone();
        }else if(fragment instanceof AboutUsFragment){
            mainNavigationHelper.defaultNavigate();
        }
        else if(fragment instanceof HelpFragment){
            mainNavigationHelper.defaultNavigate();
        }

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container,fragment,"fragment")
                .commit();
    }

    @Override
    public void onDefaultMain() {
        mainNavigationHelper.defaultNavigate();
    }

    @Override
    public void onDefaultBackNext() {
        mainNavigationHelper.defaultToBackNext();
    }


    @Override
    public void onDefaultBackDone() {
        mainNavigationHelper.defaultToBackDone();
    }

    @Override
    public void onChangeInstructText(String text) {
        mainNavigationHelper.changeDirectText(text);
    }
}
