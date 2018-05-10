package inc.wastedmynd.keylesstorage.ui.adapters.ViewHolder;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import inc.wastedmynd.keylesstorage.R;
import inc.wastedmynd.keylesstorage.data.Token;

public class TokenViewHolder extends RecyclerView.ViewHolder {

    private Context context;
    public ConstraintLayout token_item;
    public ImageView token_item_icon;
    public TextView token_item_storage_unit;


    public TokenViewHolder(View parentView)
    {
        super(parentView);
        setContext(parentView.getContext());

        token_item = parentView.findViewById(R.id.token_item);
        token_item_icon = parentView.findViewById(R.id.token_item_icon);
        token_item_storage_unit = parentView.findViewById(R.id.token_item_text);
    }




    public void bindToken(Token token)
    {
        token_item_storage_unit.setText(token.toString());
    }


    public void bindTokenIcon(Token token, int iconRes)
    {
        token_item_icon.setImageResource(iconRes);
        token_item_storage_unit.setText(token.toString());
    }



    public void setupOnClickListener(final View.OnClickListener clickListener) {
        if (clickListener == null) return;

        token_item.setOnClickListener(clickListener);

    }

    //region getter and setters

    public Context getContext()
    {
        return context;
    }

    public void setContext(Context context)
    {
        this.context = context;
    }
    //endregion

}
