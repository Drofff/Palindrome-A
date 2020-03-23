package com.drofff.palindrome.utils;

import com.drofff.palindrome.context.UserContext;
import com.drofff.palindrome.context.UserContextHolder;
import com.drofff.palindrome.entity.Police;
import com.drofff.palindrome.exception.PalindromeException;

public class AuthenticationUtils {

    private AuthenticationUtils() {}

    public static Police getCurrentUser() {
        UserContext userContext = UserContextHolder.getUserContext()
                .orElseThrow(() -> new PalindromeException("User context has not been initialized"));
        return userContext.getCurrentUser();
    }

}