package com.drofff.palindrome.exception;

import java.util.ArrayList;
import java.util.List;

public class ValidationException extends RuntimeException {

    private final List<Integer> errorFieldIds;

    public ValidationException(String message) {
        super(message);
        this.errorFieldIds = new ArrayList<>();
    }

    public ValidationException(List<Integer> errorFieldIds) {
        this.errorFieldIds = errorFieldIds;
    }

    public ValidationException(String message, List<Integer> errorFieldIds) {
        super(message);
        this.errorFieldIds = errorFieldIds;
    }

    public List<Integer> getErrorFieldIds() {
        return errorFieldIds;
    }

}
