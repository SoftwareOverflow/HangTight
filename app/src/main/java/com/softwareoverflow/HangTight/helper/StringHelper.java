package com.softwareoverflow.HangTight.helper;

import java.util.Locale;

public class StringHelper {

    public static String minuteSecondTimeFormat(int numSeconds){
        return String.format(Locale.getDefault(), "%02d:%02d", numSeconds / 60, numSeconds % 60);
    }
}
