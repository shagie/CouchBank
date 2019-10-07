package net.shagie.couchbank.load;

import java.util.Arrays;

public class BeTwoSix {
    private char[] ca;

    BeTwoSix(long val) {
        ca = new char[4];
        ca[3] = (char) ((char)(val % 26) + 'a');
        val /= 26;
        ca[2] = (char) ((char)(val % 26) + 'a');
        val /= 26;
        ca[1] = (char) ((char)(val % 26) + 'a');
        val /= 26;
        ca[0] = (char) ((char)(val % 26) + 'a');
    }

    public String asString() {
        return new String(ca);
    }

    @Override
    public String toString() {
        return "BeTwoSix{" +
                "ca=" + Arrays.toString(ca) +
                '}';
    }
}
