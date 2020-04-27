package com.drofff.palindrome.service;

import java.util.Optional;

public interface AuthenticationService {

    void saveUserAuthentication(String userId, String token);

    Optional<String> requestAuthorizationToken();

}
