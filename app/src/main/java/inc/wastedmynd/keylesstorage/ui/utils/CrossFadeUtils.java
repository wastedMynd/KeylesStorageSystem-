package inc.wastedmynd.keylesstorage.ui.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

/**
 * Created by sizwe on 2016/11/05 @ 1:52 AM.
 */
public class CrossFadeUtils
{
    private View mContentView;
    private View mLoadingView;
    private int mShortAnimationDuration;


    public CrossFadeUtils(View mContentView, View mLoadingView)
    {
        this.mContentView = mContentView;
        this.mLoadingView = mLoadingView;

        // Initially hide the content view.
        mContentView.setVisibility(View.GONE);

        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = 300;
    }


    public void processWork()
    {

        mContentView.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter()
                {
                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        mContentView.setVisibility(View.GONE);
                    }
                });

        mLoadingView.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter()
                {
                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        mLoadingView.setVisibility(View.VISIBLE);
                    }
                });

    }

    public void crossfade()
    {

        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        mContentView.setAlpha(0f);
        mContentView.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        mContentView.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        mLoadingView.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter()
                {
                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        mLoadingView.setVisibility(View.GONE);
                    }
                });
    }
}
