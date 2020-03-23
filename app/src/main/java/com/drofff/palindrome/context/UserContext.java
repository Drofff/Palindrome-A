package com.drofff.palindrome.context;

import com.drofff.palindrome.entity.Police;
import com.drofff.palindrome.exception.PalindromeException;
import com.drofff.palindrome.service.AuthorizationTokenService;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import org.json.JSONObject;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.drofff.palindrome.utils.HttpUtils.getAtUrl;
import static java.util.concurrent.TimeUnit.HOURS;

public class UserContext {

    private static final Executor USER_CONTEXT_EXECUTOR = Executors.newSingleThreadExecutor();

    private static final String CURRENT_USER_KEY = "currentUser";

    private LoadingCache<String, Police> userCache = Caffeine.newBuilder()
            .expireAfterWrite(1, HOURS)
            .build(key -> requestCurrentUserInfo());

    private final AuthorizationTokenService authorizationTokenService;

    private final String userInfoUrl;

    UserContext(AuthorizationTokenService authorizationTokenService, String userInfoUrl) {
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
        JSONObject response = getAtUrl(userInfoUrl);
        Police police = Police.fromJSONObject(response);
        resultFuture.complete(police);
    }

    public Police getCurrentUser() {
        return userCache.get(CURRENT_USER_KEY);
    }

}
