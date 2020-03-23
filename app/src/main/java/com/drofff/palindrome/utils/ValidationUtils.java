package com.drofff.palindrome.utils;

import com.drofff.palindrome.exception.ValidationException;

public class ValidationUtils {

    private ValidationUtils() {}

    public static void validateNotNull(Object object, String errorMessage) {
        if(object == null) {
            throw new ValidationException(errorMessage);
        }
    }

}
