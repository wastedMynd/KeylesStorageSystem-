package inc.wastedmynd.keylesstorage.ui.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sizwe on 21-Dec-15.
 */
public enum DateFormats
{
    Day_Month_Year("dd-MMMM, yyyy"),

    //with day field eg. Fri
    DayName_Day_Month_Year_Hours_Minutes_Secs_MillSec("EEEE, dd MMMM yyyy ,HH:MM:ss:sss"),
    DayName_Day_Month_Year_Hours_Minutes_Secs("EEEE, dd MMMM yyyy ,HH:MM:ss"),
    DayName_Day_Month_Year_Hours_Minutes("EEEE, dd MMMM yyyy ,HH:MM"),
    DayName_Day_Month_Year_Hours("EEEE, dd MMMM yyyy ,HH"),
    DayName_Day_Month_Year("EEEE, dd MMMM yyyy"),
    DayName_Day_Month("EEEE, dd MMMM"),
    Day_Month("dd MMMM"),
    DayName_Day("EEEE, dd"),
    DayName("EEEE"),

    //without day field, with time
    Day_Month_Year_Hours_Minutes_Secs_MillSec("dd MMMM yyyy ,HH:MM:ss.SSSZ"),
    Month_Year_Hours_Minutes_Secs_MillSec("MMMM yyyy ,HH:MM:ss.SSSZ"),
    Year_Hours_Minutes_Secs_MillSec("yyyy ,HH:MM:ss.SSSZ"),
    Hours_Minutes_Secs_MillSec("HH:MM:ss.SSSZ"),
    Hours_Minutes("HH:MM"),
    Minutes_Secs_MillSec("MM:ss.SSSZ"),
    Secs_MillSec("ss.SSSZ"),
    MillSec("SSSZ");

    private String format;

    DateFormats(String mFormat)
    {
        format = mFormat;
    }

    public String getFormat()
    {
        return format;
    }


    public static String getSimpleDateString(long date)
    {
        String dateString;

        DateFormats dateFormat = Day_Month_Year;

        Date currentDate = new Date(System.currentTimeMillis());

        Date subjectDate = new Date(date);

        if (subjectDate.getYear() == currentDate.getYear())
        {
            dateFormat = Day_Month;

            if (subjectDate.getMonth() == currentDate.getMonth())
            {
                dateFormat = DayName_Day;
            }
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat.getFormat());

        dateString = simpleDateFormat.format(new Date(date));

        return dateString;
    }

    public static String getSimpleDateString(long date, DateFormats dateFormat)
    {
        String dateString;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat.getFormat());

        dateString = simpleDateFormat.format(new Date(date));

        return dateString;
    }

    public static String getEstimatedRemaingTimeString(long started, long load, long load_done)
    {
        final long SEC = getEstimatedRemainingSec(started, load, load_done);

        long sec = SEC;
        long min = 0;
        long hours = 0;
        long days = 0;
        long week = 0;
        long month = 0;

        String time = new String();

        if (sec > 0) min = sec % 60;
        if (min > 0) hours = min % 60;
        if (hours > 0) days = hours % 24;
        if (days > 0) week = days % 7;
        if (week > 0) month = week % 28;
        if (month > 0) time += month;

        if (week > 0) time += " - " + week;
        if (days > 0) time += " - " + days;
        if (hours >= 0) time += " - " + hours;
        if (min >= 0) time += " : " + min;
        if (sec >= 0) time += " ." + sec;

        return time;
    }

    public static long getEstimatedRemainingSec(long started, long load, long load_done)
    {
        long elapsed_time = System.currentTimeMillis() - started;
        long work_time = elapsed_time * load / load_done;
        long remaining_time = work_time - elapsed_time;

        return (remaining_time / 1000);
    }

}
