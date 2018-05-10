package inc.wastedmynd.keylesstorage.ui;

import android.app.Fragment;

public interface MainActivityListener {
    void onChangeFragment(Fragment fragment);
    void onDefaultMain();
    void onDefaultBackNext();
    void onDefaultBackDone();
    void onChangeInstructText(String text);
}
