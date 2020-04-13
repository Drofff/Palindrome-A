package com.drofff.palindrome.utils;

import java.util.Arrays;
import java.util.Objects;

import static java.util.stream.Collectors.joining;

public class StringUtils {

    private StringUtils() {}

    public static String joinNonNullPartsWith(String delimiter, String ... parts) {
        return Arrays.stream(parts)
                .filter(Objects::nonNull)
                .collect(joining(delimiter));
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
