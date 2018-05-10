package inc.wastedmynd.keylesstorage.ui.adapters.ViewHolder;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

//import com.wang.avi.AVLoadingIndicatorView;

import inc.wastedmynd.keylesstorage.R;
import inc.wastedmynd.keylesstorage.ui.utils.CrossFadeUtils;

/**
 * Created by sizwe on 2017/01/28 @ 1:35 PM.
 */
public class ContentViewHolder {

    public CrossFadeUtils crossFadeUtils;

    public CoordinatorLayout contentViewRoot;
    public RecyclerView contentRecycler;
    public LinearLayout contentLoader;
    //public AVLoadingIndicatorView contentLoaderProgress;
    public TextView contentLoaderInfo;

    public ContentViewHolder(View view) {
        contentViewRoot =  view.findViewById(R.id.content_view_root);
        contentRecycler = view.findViewById(R.id.content_recycler);
        initialize(view);
    }

    public ContentViewHolder(Context context)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_content_view,null,false);
        initialize(view);
    }

    private void initialize(View view) {
        contentLoader = view.findViewById(R.id.content_loader);
        //contentLoaderProgress = view.findViewById(R.id.content_loader_progress);
        contentLoaderInfo = view.findViewById(R.id.content_loader_info);
        startContentLoaderProgress();
    }


    //region Start and Stop contentLoaderProgress
    public void startContentLoaderProgress() {
        //contentLoaderProgress.smoothToShow();
    }

    public void stopContentLoaderProgress() {
       // contentLoaderProgress.smoothToHide();
    }
    //endregion
}
