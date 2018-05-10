package inc.wastedmynd.keylesstorage.control;

import android.content.Context;

import java.util.ArrayList;

import inc.wastedmynd.keylesstorage.data.Token;
import inc.wastedmynd.keylesstorage.database.TokenDatabase;

import static inc.wastedmynd.keylesstorage.control.PassPhraseGenerator.getPassPhrase;

public class TokenHelper {

    public static ArrayList<Token> getStoredTokens(Context context)
    {
        ArrayList<Token> storedTokens = new ArrayList<>(); //container for stored token on the TokenDatabase

        ArrayList<Object> items = new TokenDatabase(context).getItems(); // Tokens present in the TokenDatabase

        for (Object o : items) {
            storedTokens.add((Token) o);
        }

        return  storedTokens;
    }

    private static  int[] getStorageUnits()
    {
        int[] range = new int[TokenDatabase.TOKEN_LIMIT];
        for (int num =0; num < TokenDatabase.TOKEN_LIMIT; num++) range[num] = num;
        return range;
    }

    public static ArrayList<Token> getAvailableTokens(Context context)
    {

        ArrayList<Token> storedTokens = getStoredTokens(context);
        ArrayList<Token> availableTokens = new ArrayList<>();

        int[] storageUnits = getStorageUnits();

        if(!storedTokens.isEmpty())
        {
            for (Token token: storedTokens)
            {
                for(int i = 0; i< storageUnits.length ; i++ )
                {
                    if(i == token.getStorageUnit()) continue;

                    availableTokens.add(new Token(i,getPassPhrase()));
                }
            }
        }
        else
        {
            for(int i = 0; i< storageUnits.length ; i++ )
            {
                availableTokens.add(new Token(i,getPassPhrase()));
            }
        }


        return availableTokens;
    }
}
