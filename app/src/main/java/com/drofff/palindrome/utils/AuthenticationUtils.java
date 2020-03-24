package com.drofff.palindrome.utils;

import com.drofff.palindrome.context.UserContext;
import com.drofff.palindrome.context.UserContextHolder;
import com.drofff.palindrome.entity.Police;

public class AuthenticationUtils {

    private AuthenticationUtils() {}

    public static Police getCurrentUser() {
        UserContext userContext = UserContextHolder.getUserContext();
        return userContext.getCurrentUser();
    }

}