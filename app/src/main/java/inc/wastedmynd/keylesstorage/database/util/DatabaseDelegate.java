package inc.wastedmynd.keylesstorage.database.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by sizwe on 2016/10/14 @ 2:11 PM.
 */
public abstract class DatabaseDelegate
{
    //region Database Variables



    //region Context
    private Context context;

    public Context getContext()
    {
        return context;
    }

    private void setContext(Context context)
    {
        this.context = context;
    }
    //endregion

    //region Database

    private SQLiteDatabase database;

    protected SQLiteDatabase getDatabase()
    {
        return database;
    }

    private void setDatabase(SQLiteDatabase database)
    {
        this.database = database;
    }

    //endregion

    //region Database Name

    private String databaseName = "KeylessStorage";

    protected String getDatabaseName()
    {
        return databaseName;
    }

    protected void setDatabaseName(String databaseName)
    {
        this.databaseName = databaseName;
    }

    //endregion

    //region Database Version

    private int databaseVersion=1;

    protected int getDatabaseVersion()
    {
        return databaseVersion;
    }

    protected void setDatabaseVersion(int databaseVersion)
    {
        this.databaseVersion = databaseVersion;
    }

    // endregion

    //region Database Scheme

    private String databaseScheme;

    protected String getDatabaseScheme()
    {
        return databaseScheme;
    }

    protected void setDatabaseScheme(String databaseScheme)
    {
        this.databaseScheme = databaseScheme;
    }

    //endregion

    //region DatabaseListener

    private DatabaseListener databaseListener;

    protected DatabaseListener getDatabaseListener()
    {
        return databaseListener;
    }

    public void setDatabaseListener(DatabaseListener databaseListener)
    {
        this.databaseListener = databaseListener;
    }
    //endregion

    //region Database DatabaseHelper

    private DatabaseHelper databaseHepler;

    private DatabaseHelper getDatabaseHepler()
    {
        return databaseHepler;
    }

    protected void setDatabaseHepler(DatabaseHelper databaseHepler)
    {
        this.databaseHepler = databaseHepler;
    }

    //endregion

    //endregion

    public DatabaseDelegate(Context context)
    {
        setContext(context);
        setDatabaseHepler(new DatabaseHelper(context,getDatabaseName(),onCreateScheme() ,getDatabaseVersion()));
    }

    protected DatabaseDelegate openDatabase()
    {
        try
        {
            setDatabase(getDatabaseHepler().getWritableDatabase());
        }
        catch (Exception ignored) {}
        return this;
    }

    protected void closeDatabase()
    {
        if (getDatabase()!=null)
        getDatabase().close();
    }

    //database access functions

    protected abstract String onCreateScheme();

    //client accessed functions

    public abstract boolean addItem(Object item);

    public abstract boolean updateItem(Object item);

    public abstract boolean removeItem(Object item);

    public abstract boolean checkExists(Object item);

    public abstract ArrayList<Object> getItems();

    public abstract ArrayList<Object> getItemsOf(Object item);

    public abstract int getCount() ;
}
