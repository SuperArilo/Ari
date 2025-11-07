package com.tty.lib.tool;

import java.security.SecureRandom;

public class RandomGeneratorUtils {

    private static final SecureRandom secureRandom = new SecureRandom();

    public static int get(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("The maximum value must be greater than the minimum value");
        }
        return secureRandom.nextInt(max - min + 1) + min;
    }

}
