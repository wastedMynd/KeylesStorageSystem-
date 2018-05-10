package inc.wastedmynd.keylesstorage.ui.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by sizwe on 2016/10/14 @ 11:53 PM.
 */
public class SimpleTasks
{
    public static void toastShortMessage(Context context, String message)
    {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void toastShortMessage(Context context,int resStringId)
    {
        Toast.makeText(context, resStringId, Toast.LENGTH_SHORT).show();
    }

    public static void toastLongMessage(Context context, String message)
    {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void toastLongMessage(Context context,int resStringId)
    {
        Toast.makeText(context, resStringId, Toast.LENGTH_LONG).show();
    }



    public static Intent getSharedUrlIntent(String mUrl)
    {
        if ( mUrl==null || mUrl.length()==0) return null;

        //send a single piece of content
        Uri uri = Uri.parse(mUrl);

        Intent sharedIntent = new Intent(Intent.ACTION_SEND);
        sharedIntent.putExtra(Intent.EXTRA_HTML_TEXT, mUrl);
        sharedIntent.putExtra(Intent.EXTRA_TEXT, mUrl);
        sharedIntent.putExtra(Intent.EXTRA_STREAM,uri);
        sharedIntent.setType("text/*");

        return Intent.createChooser(sharedIntent, "Share url with...");
    }


    public static Intent getSharedMultiUrlIntent(ArrayList<String> mUrls)
    {

        //send multiple pieces of content
        ArrayList<Uri> urls = new ArrayList<>(mUrls.size());

        for (String item : mUrls)
        {
            urls.add(Uri.parse(item));
        }

        if (urls.size() < 0) return null;

        Intent sharedIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        sharedIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, urls);
        sharedIntent.setType("text/*|text/html|text/link");


        return Intent.createChooser(sharedIntent, "Share items with...");
    }


    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    public static boolean isXLargeTablet(Context context)
    {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }


    public static void reportException(Context context,String title,Exception error)
    {
        final String EXCEPTION_LOG = "Exceptions";


        String strDate= DateFormats.getSimpleDateString(System.currentTimeMillis(),DateFormats.DayName_Day_Month_Year_Hours_Minutes_Secs_MillSec);

        StringBuilder newReport = new StringBuilder(title);
        newReport.append(" Date : ");
        newReport.append(strDate);
        newReport.append("\n");
        newReport.append("Localized Message: ");
        newReport.append("\n");
        newReport.append(error.toString());
        newReport.append("\n");
        newReport.append("\n");
        newReport.append("Message: ");
        newReport.append("\n");
        newReport.append(error.getMessage());
        newReport.append("\n");

        final SharedPreferences pref;
        pref = PreferenceManager.getDefaultSharedPreferences(context);

        String oldReports = pref.getString(EXCEPTION_LOG,"");

        StringBuilder report = new StringBuilder(newReport.toString());

        if(!oldReports.isEmpty())
        {
            report.append("\n");
            report.append(oldReports);
        }

        pref.edit().putString(EXCEPTION_LOG,report.toString()).apply();
    }
}
