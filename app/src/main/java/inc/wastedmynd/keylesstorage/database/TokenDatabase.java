package inc.wastedmynd.keylesstorage.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import inc.wastedmynd.keylesstorage.data.Token;
import inc.wastedmynd.keylesstorage.database.util.DatabaseDelegate;

public class TokenDatabase extends DatabaseDelegate {

    /**
    *Maximum number of tokens issued
    */
    public static final int TOKEN_LIMIT = 3;

    //database table
    private final String DATABASE_TABLE = "MyTokens";

    // Columns
    public final String ID_COLUMN = "id";
    public final String STORAGE_UNIT_COLUMN = "storage_unit";
    public final String PASSWORD_COLUMN = "password";
    public final String MAC_ADDRESS_COLUMN = "mac_address";
    public final String TIMESTAMP_COLUMN = "timestamp";

    public TokenDatabase(Context context) {
        super(context);
    }

    @Override
    protected String onCreateScheme() {

        StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        builder.append(DATABASE_TABLE);
        builder.append(String.format("(%s INTEGER PRIMARY KEY  AUTOINCREMENT,", ID_COLUMN));
        builder.append(String.format("%s INTEGER,", STORAGE_UNIT_COLUMN));
        builder.append(String.format("%s TEXT,", PASSWORD_COLUMN));
        builder.append(String.format("%s TEXT NOT NULL,", MAC_ADDRESS_COLUMN));
        builder.append(String.format("%s LONG);", TIMESTAMP_COLUMN));

        return builder.toString();
    }

    @Override
    public boolean addItem(Object item) {
        boolean result = false;

        if (item == null) return result;

        if (checkExists(item)) {
            return updateItem(item);
        }

        if (!(item instanceof Token)) return result;

        Token storageToken = (Token) item;

        if (!storageToken.isValid()) return result;

        ContentValues values = new ContentValues();

        values.put(STORAGE_UNIT_COLUMN, storageToken.getStorageUnit());

        values.put(PASSWORD_COLUMN, storageToken.getPassPhrase());

        values.put(MAC_ADDRESS_COLUMN, storageToken.getUserMacAddress());

        values.put(TIMESTAMP_COLUMN, storageToken.getTimeStamp());

        float res;
        openDatabase();
        res = getDatabase().insert(DATABASE_TABLE, null, values);
        closeDatabase();

        result = res > 0;

        storageToken.setId((int) res);

        if (getDatabaseListener() != null)
            getDatabaseListener().onDatabaseItemInserted(result, item);

        return result;
    }

    @Override
    public boolean updateItem(Object item) {
        boolean ret = false;

        if (!checkExists(item)) {
            return addItem(item);
        }

        if (!(item instanceof Token)) return ret;

        Token storageToken = (Token) item;

        String whereClause = STORAGE_UNIT_COLUMN + "=?";

        String[] whereArgs = {String.valueOf(storageToken.getStorageUnit())};

        ContentValues values = new ContentValues();

        values.put(PASSWORD_COLUMN, storageToken.getPassPhrase());

        values.put(MAC_ADDRESS_COLUMN, storageToken.getUserMacAddress());

        values.put(TIMESTAMP_COLUMN, storageToken.getTimeStamp());

        openDatabase();
        ret = getDatabase().update(DATABASE_TABLE, values, whereClause, whereArgs) > 0;
        closeDatabase();


        if (getDatabaseListener() != null) getDatabaseListener().onDatabaseItemUpdated(ret, item);

        return ret;
    }

    @Override
    public boolean removeItem(Object item) {
        boolean ret = false;

        if (!checkExists(item)) return ret;

        Token Token = (Token) item;

        String whereClause = STORAGE_UNIT_COLUMN + "=?";

        String[] whereArgs = {String.valueOf(Token.getStorageUnit())};

        openDatabase();
        int res = getDatabase().delete(DATABASE_TABLE, whereClause, whereArgs);
        closeDatabase();

        ret = res > 0;

        if (getDatabaseListener() != null) getDatabaseListener().onDatabaseItemRemoved(ret, item);

        return ret;
    }

    @Override
    public boolean checkExists(Object item) {
        if (item == null) return false;

        boolean exists = false;

        if (item instanceof Token) {
            Token storageToken = (Token) item;

            ArrayList<Object> items = getItems();

            for (Object obj : items) {
                Token ref = (Token) obj;

                exists = ref.equals(storageToken);

                if (exists) break;
            }
        }

        return exists;
    }

    @Override
    public ArrayList<Object> getItems() {
        ArrayList<Object> Tokens = new ArrayList<>();

        openDatabase();

        if (getDatabase() == null) {
            closeDatabase();
            return Tokens;
        }
        StringBuilder orderBy = new StringBuilder(ID_COLUMN);
        orderBy.append(" ASC");
        Cursor c = getDatabase().query(DATABASE_TABLE, null, null, null, null, null, orderBy.toString());

        if (c.moveToFirst()) {
            do {

                try {
                    //region get database content
                    int id = c.getInt(c.getColumnIndex(ID_COLUMN));

                    int storageUnit = c.getInt(c.getColumnIndex(STORAGE_UNIT_COLUMN));

                    String passPhrase = c.getString(c.getColumnIndex(PASSWORD_COLUMN));

                    String macAddress = c.getString(c.getColumnIndex(MAC_ADDRESS_COLUMN));

                    long timeStamp = c.getLong(c.getColumnIndex(TIMESTAMP_COLUMN));
                    //endregion

                    //region apply database content to Token item
                    Token item = new Token();

                    item.setId(id);

                    item.setStorageUnit(storageUnit);

                    item.setPassPhrase(passPhrase);

                    item.setUserMacAddress(macAddress);

                    item.setTimeStamp(timeStamp);

                    //endregion

                    Tokens.add(item);
                } catch (Exception ignored) {
                }

            } while (c.moveToNext());

            if (c != null && !c.isClosed()) c.close();
        }

        closeDatabase();

        return Tokens;
    }

    @Override
    public ArrayList<Object> getItemsOf(Object item) {

        if (item == null) return null;

        ArrayList<Object> tokens = new ArrayList<>();

        if (item instanceof Token) {
            Token token = (Token) item;

            ArrayList<Object> items = getItems();

            for (Object object : items) {
                Token presentToken = (Token) object;

                if (token.equals(presentToken)) {
                    tokens.add(presentToken);
                }
            }
        }


        return tokens;

    }

    public int getCount() {
        int count;
        openDatabase();
        Cursor c = getDatabase().query(DATABASE_TABLE, null, null, null, null, null, null);
        count = c.getCount();
        closeDatabase();
        return count;
    }



    public static Token getTokenFrom(int storageUnit, Context context) {
        Token token = null;

        TokenDatabase database = new TokenDatabase(context);

        ArrayList<Object> tokens = database.getItemsOf(storageUnit);

        if (!tokens.isEmpty()) token = (Token) tokens.get(0);

        return token;
    }

    public static boolean isPassPharseCorrect(Token token,Context context)
    {
        Token databaseToken = getTokenFrom(token.getStorageUnit(),context);

        if(databaseToken==null)return false;

        return (token.getPassPhrase().equals(databaseToken.getPassPhrase()));
    }

    public static boolean TokenExists(int storageUnit, Context context) {
        return (getTokenFrom(storageUnit, context) != null);
    }

    public static int getCount(Context context) {
        TokenDatabase database = new TokenDatabase(context);
        return database.getCount();
    }

}
