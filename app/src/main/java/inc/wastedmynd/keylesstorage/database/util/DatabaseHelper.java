package inc.wastedmynd.keylesstorage.database.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sizwe on 2016/10/14 @ 1:41 PM @ 6:35 PM @ 6:35 PM.
 */
public class DatabaseHelper extends SQLiteOpenHelper
{

    //region Database Variables

    //region Database Scheme

    private String databaseScheme;

    public String getDatabaseScheme()
    {
        return databaseScheme;
    }

    private void setDatabaseScheme(String databaseScheme)
    {
        this.databaseScheme = databaseScheme;
    }

    //endregion

    //endregion

    public DatabaseHelper(Context context, String databaseName, String databaseScheme, int databaseVersion)
    {

        super(context, databaseName, null, databaseVersion);

        setDatabaseScheme(databaseScheme);

        onCreate(getReadableDatabase());
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {

        if(db!=null&& getDatabaseScheme()!=null)
            db.execSQL(getDatabaseScheme());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2)
    {
        //db.execSQL("DROP TABLE IF EXISTS " + getDatabaseName());

        if(!db.isOpen()) onCreate(db);
    }

}
