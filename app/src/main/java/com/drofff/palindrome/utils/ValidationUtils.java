package com.drofff.palindrome.utils;

import com.drofff.palindrome.exception.ValidationException;

import java.util.Date;

public class ValidationUtils {

    private ValidationUtils() {}

    public static void validateNotNull(Object object, String errorMessage) {
        if(object == null) {
            throw new ValidationException(errorMessage);
        }
    }

    public static void validateIsFutureDateEpochSeconds(long epochSecondsDate, String errorMessage) {
        if(isPastOrPresent(epochSecondsDate)) {
            throw new ValidationException(errorMessage);
        }
    }

    private static boolean isPastOrPresent(long epochSecondsDate) {
        long nowEpochSeconds = new Date().getTime();
        return nowEpochSeconds >= epochSecondsDate;
    }

    public static void validateIsTrue(boolean condition, String errorMessage) {
        if(!condition) {
            throw new ValidationException(errorMessage);
        }
    }

}
