package com.drofff.palindrome.utils;

import com.drofff.palindrome.exception.ValidationException;

import static com.drofff.palindrome.utils.StringUtils.isBlank;

public class ValidationUtils {

    private ValidationUtils() {}

    public static void validateNotBlank(String str, String errorMessage) {
        validateNotNull(str, errorMessage);
        if(isBlank(str)) {
            throw new ValidationException(errorMessage);
        }
    }

    public static void validateNotNull(Object object, String errorMessage) {
        if(object == null) {
            throw new ValidationException(errorMessage);
        }
    }

}
