package com.drofff.palindrome.service;

import java.util.Optional;

public interface AuthorizationTokenService {

    void saveAuthorizationToken(String token);

    Optional<String> getAuthorizationTokenIfPresent();

}
