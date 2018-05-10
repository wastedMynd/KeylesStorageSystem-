package inc.wastedmynd.keylesstorage.ui.utils;

/**
 * days are defined here.
 * <p/>
 * Created by sizwe on 2016/12/02 @ 3:30 PM.
 */
public enum DayFormats
{
    Sunday("Su", 1),
    Monday("M", 2),
    Tuesday("T", 3),
    Wednesday("W", 4),
    Thursday("Th", 5),
    Friday("F", 6),
    Saturday("Sa", 7);

    private String dayName;
    private int dayNumber;

    DayFormats(String dayName, int dayNumber)
    {
        setDayName(dayName);
        setDayNumber(dayNumber);
    }

    private void setDayName(String dayName)
    {
        this.dayName = dayName;
    }

    private void setDayNumber(int dayNumber) {this.dayNumber = dayNumber;}

    public int getDayNumber()
    {
        return  dayNumber;
    }

    @Override
    public String toString()
    {
        return this.dayName;
    }

    public String getDayName()
    {
        String dayName = toString();

        if (this == getTodaysFormat())
        {
            dayName = "today";
        }

        return dayName;
    }

    public static DayFormats getDayFormat(long timeStamp)
    {
        final String thisDayName = DateFormats.getSimpleDateString(timeStamp, DateFormats.DayName);

        DayFormats thisDayFormat = Sunday;

        for (DayFormats format : DayFormats.values())
        {
            String dayFormatName = format.name();

            if (thisDayName.equals(dayFormatName))
            {
                thisDayFormat = format;
                break;
            }
        }

        return thisDayFormat;
    }

    public static DayFormats getTodaysFormat()
    {
        final long currentDateAndTime = System.currentTimeMillis();
        final DayFormats todayFormat = getDayFormat(currentDateAndTime);
        return todayFormat;
    }

    public static DayFormats getTomorrowsFormat()
    {
        final DayFormats today = DayFormats.getTodaysFormat();

        DayFormats tomorrow = Monday;

        switch (today)
        {
            case Sunday:
                tomorrow = Monday;
                break;
            case Monday:
                tomorrow = Tuesday;
                break;
            case Tuesday:
                tomorrow = Wednesday;
                break;
            case Wednesday:
                tomorrow = Thursday;
                break;
            case Thursday:
                tomorrow = Friday;
                break;
            case Friday:
                tomorrow = Saturday;
                break;
            case Saturday:
                tomorrow = Sunday;
                break;
        }

        return tomorrow;
    }

    public static DayFormats getYesterdaysFormat()
    {
        final DayFormats today = DayFormats.getTodaysFormat();

        DayFormats yesterday = Monday;

        switch (today)
        {
            case Sunday:
                yesterday = Saturday;
                break;
            case Monday:
                yesterday = Sunday;
                break;
            case Tuesday:
                yesterday = Monday;
                break;
            case Wednesday:
                yesterday = Tuesday;
                break;
            case Thursday:
                yesterday = Wednesday;
                break;
            case Friday:
                yesterday = Thursday;
                break;
            case Saturday:
                yesterday = Friday;
                break;
        }

        return yesterday;
    }
}
