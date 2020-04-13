package com.drofff.palindrome.service;

import com.drofff.palindrome.annotation.StringResource;
import com.drofff.palindrome.entity.Police;
import com.drofff.palindrome.exception.PalindromeException;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import org.json.JSONObject;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.drofff.palindrome.R.string.police_info_url;
import static com.drofff.palindrome.utils.HttpUtils.getFromServer;
import static java.util.concurrent.TimeUnit.HOURS;

public class UserContextImpl implements UserContext {

    private static final Executor USER_CONTEXT_EXECUTOR = Executors.newSingleThreadExecutor();

    private static final String CURRENT_USER_KEY = "currentUser";

    private LoadingCache<String, Police> userCache = Caffeine.newBuilder()
            .expireAfterWrite(1, HOURS)
            .build(key -> requestCurrentUserInfo());

    private final AuthorizationTokenService authorizationTokenService;

    private final String userInfoUrl;

    public UserContextImpl(AuthorizationTokenService authorizationTokenService,
                           @StringResource(id = police_info_url) String userInfoUrl) {
        this.authorizationTokenService = authorizationTokenService;
        this.userInfoUrl = userInfoUrl;
    }

    private Police requestCurrentUserInfo() {
        validateAuthorizationTokenIsPresent();
        CompletableFuture<Police> userInfoResult = new CompletableFuture<>();
        USER_CONTEXT_EXECUTOR.execute(() -> requestPoliceFromServer(userInfoResult));
        Thread.yield();
        return userInfoResult.join();
    }

    private void validateAuthorizationTokenIsPresent() {
        if(isAuthorizationTokenMissing()) {
            throw new PalindromeException("User is not authenticated");
        }
    }

    private boolean isAuthorizationTokenMissing() {
        return !isAuthorizationTokenPresent();
    }

    private boolean isAuthorizationTokenPresent() {
        return authorizationTokenService.getAuthorizationTokenIfPresent().isPresent();
    }

    private void requestPoliceFromServer(CompletableFuture<Police> resultFuture) {
        JSONObject response = getFromServer(userInfoUrl);
        Police police = Police.fromJSONObject(response);
        resultFuture.complete(police);
    }

    @Override
    public Police getCurrentUser() {
        return userCache.get(CURRENT_USER_KEY);
    }

}
