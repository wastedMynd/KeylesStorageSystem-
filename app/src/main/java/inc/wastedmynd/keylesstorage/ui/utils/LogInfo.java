package inc.wastedmynd.keylesstorage.ui.utils;

import android.util.Log;

/**
 * Created by sizwe on 2017/02/08 @ 9:45 PM.
 */
public class LogInfo
{
    //region log
    private String log = "LogInfo";

    public String getLog()
    {
        return log;
    }

    public void setLog(String log)
    {
        this.log = log;
    }
    //endregion

    public LogInfo(String log)
    {
        setLog(log);
    }

    public void error(String e)
    {
        Log.e(getLog(),e);
    }

    public void display(String d)
    {
        Log.d(getLog(),d);
    }

    public void info(String i)
    {
        Log.i(getLog(),i);
    }

    public void w(String w)
    {
        Log.w(getLog(),w);
    }

    public void wft(String wft)
    {
        Log.wtf(getLog(),wft);
    }
}
