package com.drofff.palindrome.exception;

public class PalindromeException extends RuntimeException {

    public PalindromeException() {
        super();
    }

    public PalindromeException(String message) {
        super(message);
    }

    public PalindromeException(String message, Throwable cause) {
        super(message, cause);
    }

    public PalindromeException(Throwable cause) {
        super(cause);
    }

    protected PalindromeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
