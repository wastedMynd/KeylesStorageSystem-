package inc.wastedmynd.keylesstorage.database.util;

/**
 * Created by sizwe on 2016/10/14 @ 1:35 PM.
 */
public interface DatabaseListener
{
    void onDatabaseItemRemoved(boolean isItemRemoved, Object item);

    void onDatabaseItemInserted(boolean isItemSaved, Object item);

    void onDatabaseItemUpdated(boolean isItemUpdated, Object item);
}
