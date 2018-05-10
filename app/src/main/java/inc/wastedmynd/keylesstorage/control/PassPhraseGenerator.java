package inc.wastedmynd.keylesstorage.control;

import inc.wastedmynd.keylesstorage.ui.utils.RandomUtils;

public class PassPhraseGenerator {

    public static String getPassPhrase()
    {
        final int firstNumeric = RandomUtils.randomInt(9);
        StringBuilder passPhrase = new StringBuilder(String.valueOf(firstNumeric));
        int next, prev = firstNumeric;


        for (int r = 0; r < 5;) {

            next = RandomUtils.randomInt(prev);
            if(next != prev) {
                passPhrase.append(String.valueOf(next));
                prev = next;
                r++;
            }
            else if(prev == 0)
                prev++;
            else if(prev > 5) prev--;
        }

        return passPhrase.toString();
    }
}
