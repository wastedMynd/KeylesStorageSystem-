package inc.wastedmynd.keylesstorage.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import inc.wastedmynd.keylesstorage.R;
import inc.wastedmynd.keylesstorage.ui.MainActivity;
import inc.wastedmynd.keylesstorage.ui.MainActivityListener;

public class HelpFragment extends Fragment {


    private MainActivityListener mainActivityListener;
    private boolean stillShowingInstructions = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        stillShowingInstructions =true;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if(stillShowingInstructions)
                {
                    @StringRes int[] instructions ={R.string.btn_help_instructions_storing, R.string.btn_help_instructions_retrieving};
                    for(int repeat =0; repeat < getResources().getInteger(R.integer.help_instruction_repeat);repeat++)
                    {
                        for (@StringRes int instruction : instructions)
                        {
                            mainActivityListener.onChangeInstructText(getResources().getString(instruction));
                            //allow the shopper time to read the instruction; before showing to the next one
                            try { Thread.sleep(getResources().getInteger(R.integer.help_instruction_delay)); } catch (InterruptedException e) { e.printStackTrace(); }
                        }
                    }

                    //automatically show the MainMenuFragment
                    mainActivityListener.onChangeFragment(new MainMenuFragment());
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivityListener = (MainActivity) getActivity();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stillShowingInstructions = false;
    }
}
