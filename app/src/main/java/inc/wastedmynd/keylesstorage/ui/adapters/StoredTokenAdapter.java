package inc.wastedmynd.keylesstorage.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import inc.wastedmynd.keylesstorage.R;
import inc.wastedmynd.keylesstorage.control.TokenHelper;
import inc.wastedmynd.keylesstorage.ui.adapters.ViewHolder.TokenViewHolder;
import inc.wastedmynd.keylesstorage.data.Token;

public class StoredTokenAdapter extends RecyclerView.Adapter<TokenViewHolder> {
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Token> availableTokens;
    private Token selectedToken;



    public StoredTokenAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        availableTokens = TokenHelper.getAvailableTokens(context);
    }


    @Override
    public TokenViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TokenViewHolder(layoutInflater.inflate(R.layout.item_token_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(final TokenViewHolder holder, int position) {
        final Token token = getAvailableTokens().get(position);

        holder.bindToken(token);

        holder.token_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedToken = token;
            }
        });
    }

    @Override
    public int getItemCount() {
        return getAvailableTokens().size();
    }


    private ArrayList<Token> getAvailableTokens() {
        return availableTokens;
    }

    public Token getSelectedToken() {
        return selectedToken;
    }
    public  Token selectToken(int storage_unit)
    {
       return getAvailableTokens().get(storage_unit);
    }
}

