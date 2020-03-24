package com.drofff.palindrome.context;

import com.drofff.palindrome.exception.PalindromeException;
import com.drofff.palindrome.service.AuthorizationTokenService;

import java.util.Optional;

public class UserContextHolder {

    private static UserContext userContext;

    private UserContextHolder() {}

    public static void initContext(String userInfoUrl) {
        AuthorizationTokenService authorizationTokenService = BeanContext.getBeanOfClass(AuthorizationTokenService.class);
        userContext = new UserContext(authorizationTokenService, userInfoUrl);
    }

    public static UserContext getUserContext() {
        return Optional.ofNullable(userContext)
                .orElseThrow(() -> new PalindromeException("User context has not been initialized"));
    }

    public static boolean isContextInitialized() {
        return userContext != null;
    }

}
