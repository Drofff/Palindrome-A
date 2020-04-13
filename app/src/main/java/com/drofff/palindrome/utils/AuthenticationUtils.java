package com.drofff.palindrome.utils;

import com.drofff.palindrome.context.BeanContext;
import com.drofff.palindrome.entity.Police;
import com.drofff.palindrome.service.UserContext;

public class AuthenticationUtils {

    private AuthenticationUtils() {}

    public static Police getCurrentUser() {
        UserContext userContext = BeanContext.getBeanOfClass(UserContext.class);
        return userContext.getCurrentUser();
    }

}