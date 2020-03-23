package com.drofff.palindrome.context;

import com.drofff.palindrome.service.AuthorizationTokenService;

import java.util.Optional;

public class UserContextHolder {

    private static UserContext USER_CONTEXT;

    private UserContextHolder() {}

    public static void initContext(String userInfoUrl) {
        AuthorizationTokenService authorizationTokenService = BeanContext.getBeanOfClass(AuthorizationTokenService.class);
        USER_CONTEXT = new UserContext(authorizationTokenService, userInfoUrl);
    }

    public static Optional<UserContext> getUserContext() {
        return Optional.ofNullable(USER_CONTEXT);
    }

}
