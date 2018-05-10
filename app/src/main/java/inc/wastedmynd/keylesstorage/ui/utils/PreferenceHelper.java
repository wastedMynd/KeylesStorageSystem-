package inc.wastedmynd.keylesstorage.ui.utils;

/**
 * Created by sizwe on 2017/02/13 @ 9:25 PM.
 */
public class PreferenceHelper
{
    //region pref_general.xml keys

    public final static String GENERAL_PREF_KEY_NETWORK_CONNECTION = "_pref_connection";

    //quick pick settings
    public final static String GENERAL_PREF_KEY_SORT_NUMBERS = "_pref_ticket_sort_numbers";
    public final static String GENERAL_PREF_KEY_QUICKPICK_ITEMS = "_pref_quickpick_results";

    //recycle bin settings
    public final static String GENERAL_PREF_KEY_RECYCLE = "_pref_recycle_bin_switch";
    public final static String GENERAL_PREF_KEY_RECYCLE_ITEMS = "_pref_recycled_items";

    //endregion

    //region pref_notifications.xml keys

    //notifiaction settings
    public final static String NOTIFICATION_PREF_KEY_NOTIFY = "_pref_draw_result_notification";
    public final static String NOTIFICATION_PREF_KEY_TONE = "_pref_notifications_new_draw_result_ringtone";
    public final static String NOTIFICATION_PREF_KEY_VIBRATE = "notifications_new_message_vibrate";

    //notification behaviour
    public final static String NOTIFICATION_PREF_KEY_SNOOZE_INTERVAL = "_pref_snooze";

    //general notification schedule locale
    public final static String NOTIFICATION_PREF_KEY_LOCALE_MORNING = "_pref_local_morning";
    public final static String NOTIFICATION_PREF_KEY_LOCALE_AFTERNOON = "_pref_local_afternoon";
    public final static String NOTIFICATION_PREF_KEY_LOCALE_EVENING = "_pref_local_evening";
    public final static String NOTIFICATION_PREF_KEY_LOCALE_NIGHT = "_pref_local_night";

    //endregion

    //region pref_data_sync.xml keys
    public final static String DATASYNC_PREF_KEY_FREQ = "sync_frequency";
    //endregion
}
