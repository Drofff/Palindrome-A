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

}
