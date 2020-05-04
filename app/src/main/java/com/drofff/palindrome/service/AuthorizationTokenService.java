package com.drofff.palindrome.service;

import java.util.Optional;

public interface AuthorizationTokenService {

    Optional<String> getAuthorizationTokenIfPresent();

    void saveAuthorizationToken(String token);

}
