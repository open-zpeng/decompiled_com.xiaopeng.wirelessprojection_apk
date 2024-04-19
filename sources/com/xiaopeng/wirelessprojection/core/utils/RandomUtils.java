package com.xiaopeng.wirelessprojection.core.utils;

import java.util.Random;
/* loaded from: classes2.dex */
public class RandomUtils {
    public static final String LOWERCASE_ARRAY = "abcdefghijklmnopqrstuvwxyz";
    public static final int NAME_NUMBER_LENGTH = 6;
    public static final int PASSWORD_LETTER_LENGTH = 2;
    public static final int PASSWORD_NUMBER_LENGTH = 8;
    private static final String TAG = "RandomUtils";

    public static String getRandomNumbers(int i) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i2 = 0; i2 < i; i2++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    public static String getRandomLowercase(int i) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        char[] charArray = LOWERCASE_ARRAY.toCharArray();
        for (int i2 = 0; i2 < i; i2++) {
            sb.append(charArray[random.nextInt(charArray.length)]);
        }
        return sb.toString();
    }
}
