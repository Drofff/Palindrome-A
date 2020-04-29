package com.drofff.palindrome.utils;

import java.util.Arrays;
import java.util.Objects;

import static java.lang.Character.toUpperCase;
import static java.util.stream.Collectors.joining;

public class StringUtils {

    private static final String[] HEX_STR_VALUES = { "a", "b", "c", "d", "e", "f" };

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

    public static String asHexStr(int octet) {
        int leftHexDigit = getLeftHexDigit(octet);
        int rightHexDigit = getRightHexDigit(octet);
        return hexDigitAsStr(leftHexDigit) + hexDigitAsStr(rightHexDigit);
    }

    private static int getLeftHexDigit(int octet) {
        return octet >> 4;
    }

    private static int getRightHexDigit(int octet) {
        return octet & 0xf;
    }

    private static String hexDigitAsStr(int digit) {
        if(digit > 9) {
            int pos = digit - 10;
            return HEX_STR_VALUES[pos];
        }
        return digit + "";
    }

    public static String upperCaseFirstChar(String str) {
        char[] strChars = str.toCharArray();
        strChars[0] = toUpperCase(strChars[0]);
        return new String(strChars);
    }

}
