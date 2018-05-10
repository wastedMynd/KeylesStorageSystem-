package inc.wastedmynd.keylesstorage.ui;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import inc.wastedmynd.keylesstorage.R;

public class MainNavigationHelper
{
    public TextView navigator,directText;
    public Button btn0,btn1,btn2,btn3,btn4;
    public ArrayList<Button> buttons;
    public Context context;

    private com.google.android.things.contrib.driver.button.Button main_menu_io_btn,
            store_parcel_io_btn,
            retrieve_parcel_io_btn,
            help_io_btn,
            about_us_io_btn;


    public MainNavigationHelper(View v)
    {
        context = v.getContext();
        navigator= v.findViewById(R.id.navigator);
        btn0 = v.findViewById(R.id.main_menu_ui_btn);

        btn1= v.findViewById(R.id.store_ui_btn);
        btn2 = v.findViewById(R.id.retrieve_ui_btn);
        btn3 = v.findViewById(R.id.help_ui_btn);
        btn4 = v.findViewById(R.id.about_us_ui_btn);
        directText = v.findViewById(R.id.app_status_msg);



        buttons.add(btn0);
        buttons.add(btn1);
        buttons.add(btn2);
        buttons.add(btn3);
        buttons.add(btn4);


    }

    //only for io navigation, button reference mapping help
    private void initailizeComponents() throws IOException {
        main_menu_io_btn = new com.google.android.things.contrib.driver.button.Button("BCM4", com.google.android.things.contrib.driver.button.Button.LogicState.PRESSED_WHEN_LOW);
        store_parcel_io_btn = new com.google.android.things.contrib.driver.button.Button("BCM17", com.google.android.things.contrib.driver.button.Button.LogicState.PRESSED_WHEN_LOW);
        retrieve_parcel_io_btn = new com.google.android.things.contrib.driver.button.Button("BCM27", com.google.android.things.contrib.driver.button.Button.LogicState.PRESSED_WHEN_LOW);
        help_io_btn = new com.google.android.things.contrib.driver.button.Button("BCM22", com.google.android.things.contrib.driver.button.Button.LogicState.PRESSED_WHEN_LOW);
        about_us_io_btn = new com.google.android.things.contrib.driver.button.Button("BCM23", com.google.android.things.contrib.driver.button.Button.LogicState.PRESSED_WHEN_LOW);
    }

    public void changeTextTo(int index, String text)
    {
        buttons.get(index).setText(text);
    }

    public void changeTextTo(int index, @StringRes int text)
    {
        buttons.get(index).setText(text);
    }

    public void changeIconTo(int index, @DrawableRes int res)
    {
        Button button = buttons.get(index);
        button.setCompoundDrawables(context.getResources().getDrawable(res,null),null,null,null);
    }

    public void hideButton(int index)
    {
        buttons.get(index).setVisibility(View.GONE);
    }

    public void showButton(int index)
    {
        buttons.get(index).setVisibility(View.VISIBLE);
    }

    public void changeNavigatorText(String text)
    {
        navigator.setText(text);
    }

    public void changeDirectText(String text)
    {
        directText.setText(text);
    }

    public void defaultNavigate() {
        changeNavigatorText(context.getResources().getString(R.string.nav_str));
        @StringRes int[] texts ={
                R.string.btn_main_menu,
                R.string.btn_store,
                R.string.btn_retrieve,
                R.string.btn_help,
                R.string.btn_about_us};
        @DrawableRes int[] drawables ={
                R.drawable.ic_home,
                R.drawable.ic_store,
                R.drawable.ic_retrieve,
                R.drawable.ic_help,
                R.drawable.ic_about_us};

        for (int i=0;i<buttons.size();i++) {
            changeTextTo(i,texts[i]);
            changeIconTo(i, drawables[i]);
            showButton(i);
        }
    }

    public void defaultToBackNext()
    {
        changeNavigatorText("Options");
        changeTextTo(0,R.string.btn_back);
        changeTextTo(1,R.string.btn_next);

        for (int i=2;i<buttons.size();i++) {
            hideButton(i);
        }
    }

    public void defaultToBackDone()
    {
        changeNavigatorText("Options");
        changeTextTo(0,R.string.btn_back);
        changeTextTo(1,R.string.btn_done);

        for (int i=2;i<buttons.size();i++) {
            hideButton(i);
        }
    }
}
