package inc.wastedmynd.keylesstorage.ui.utils;

import java.util.Random;

/**
 * Created by sizwe on 2016/11/01 @ 11:35 PM.
 */
public class RandomUtils
{
    private static Random r = new Random();

    public static int randomInt(int range)
    {
        return (r.nextInt(range));
    }

    public static float randomFloat(int range)
    {
        return ((float)Math.random()*range);
    }

    public static <T> T randomElement(T[] array)
    {
        return (array[randomIndex(array)]);
    }

    public static int randomIndex(Object[] array)
    {
        return (randomInt(array.length));
    }

}
