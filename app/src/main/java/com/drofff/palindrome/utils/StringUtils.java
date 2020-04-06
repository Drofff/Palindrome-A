package com.drofff.palindrome.utils;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class StringUtils {

    private StringUtils() {}

    public static String joinNonNullPartsWithSpace(String ... parts) {
       return Arrays.stream(parts)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));
    }

    public static String shortenIfNeeded(String str, int maxLength) {
        if(isAboveMaxLength(str, maxLength)) {
            String shortStr = str.substring(0, maxLength - 3);
            return shortStr + "..";
        }
        return str;
    }

    private static boolean isAboveMaxLength(String str, int maxLength) {
        return str.length() > maxLength;
    }

    public static boolean isBlank(String str) {
        return str.trim().isEmpty();
    }

}
