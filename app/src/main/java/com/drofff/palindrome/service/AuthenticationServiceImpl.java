package com.drofff.palindrome.service;

import com.drofff.palindrome.annotation.StringResource;
import com.drofff.palindrome.exception.PalindromeException;
import com.drofff.palindrome.exception.RequestException;
import com.drofff.palindrome.exception.ValidationException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.drofff.palindrome.R.string.refresh_token_url;
import static com.drofff.palindrome.R.string.user_authentication_file;
import static com.drofff.palindrome.constants.SecurityConstants.AUTHENTICATION_TOKEN;
import static com.drofff.palindrome.constants.SecurityConstants.AUTHORIZATION_TOKEN;
import static com.drofff.palindrome.constants.SecurityConstants.DUE_DATE;
import static com.drofff.palindrome.constants.SecurityConstants.USER_ID;
import static com.drofff.palindrome.utils.HttpUtils.postToServerWithJsonBody;
import static com.drofff.palindrome.utils.ValidationUtils.validateIsFutureDateEpochSeconds;
import static com.drofff.palindrome.utils.ValidationUtils.validateNotNull;
import static java.util.Calendar.MONTH;

public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Executor AUTH_TOKEN_SERVICE_EXECUTOR = Executors.newSingleThreadExecutor();

    private static final int TOKEN_TIME_TO_LIVE_MONTHS = 2;

    private final String userAuthenticationFilename;
    private final String refreshTokenUrl;
    private final FileService fileService;

    public AuthenticationServiceImpl(@StringResource(id = user_authentication_file) String userAuthenticationFilename,
                                     @StringResource(id = refresh_token_url) String refreshTokenUrl,
                                     FileService fileService) {
        this.userAuthenticationFilename = userAuthenticationFilename;
        this.refreshTokenUrl = refreshTokenUrl;
        this.fileService = fileService;
    }

    @Override
    public void saveUserAuthentication(String userId, String token) {
        validateNotNull(userId, "User id is required");
        validateNotNull(token, "Token is required");
        String userAuthJson = wrapUserAuthenticationIntoJson(userId, token);
        fileService.saveFile(userAuthenticationFilename, userAuthJson);
    }

    private String wrapUserAuthenticationIntoJson(String userId, String token) {
        try {
            return wrapUserAuthenticationIntoJSONObject(userId, token).toString();
        } catch(JSONException e) {
            throw new PalindromeException(e.getMessage());
        }
    }

    private JSONObject wrapUserAuthenticationIntoJSONObject(String userId, String token) throws JSONException {
        JSONObject userAuthJson = new JSONObject();
        userAuthJson.put(USER_ID, userId);
        userAuthJson.put(AUTHENTICATION_TOKEN, token);
        userAuthJson.put(DUE_DATE, generateNextTokenDueDateEpochSeconds());
        return userAuthJson;
    }

    private long generateNextTokenDueDateEpochSeconds() {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(MONTH, TOKEN_TIME_TO_LIVE_MONTHS);
        Date dueDate = calendar.getTime();
        return dueDate.getTime();
    }

    @Override
    public Optional<String> requestAuthorizationToken() {
        try {
            return refreshAuthorizationToken();
        } catch(PalindromeException e) {
            return Optional.empty();
        }
    }

    private Optional<String> refreshAuthorizationToken() {
        JSONObject userAuthJson = getUserAuthenticationJson();
        CompletableFuture<String> authorizationTokenFuture = new CompletableFuture<>();
        getAuthorizationTokenAsync(userAuthJson, authorizationTokenFuture);
        return joinFutureResult(authorizationTokenFuture);
    }

    private JSONObject getUserAuthenticationJson() {
        try {
            String userAuthJsonStr = fileService.loadFileByName(userAuthenticationFilename);
            return parseUserAuthJSONObject(userAuthJsonStr);
        } catch(JSONException | ValidationException e) {
            throw new PalindromeException(e.getMessage());
        }
    }

    private JSONObject parseUserAuthJSONObject(String userAuthJsonStr) throws JSONException {
        JSONObject userAuthJson = new JSONObject(userAuthJsonStr);
        long dueDateEpochSeconds = userAuthJson.getLong(DUE_DATE);
        validateIsFutureDateEpochSeconds(dueDateEpochSeconds, "Refresh token has expired");
        return userAuthJson;
    }

    private void getAuthorizationTokenAsync(JSONObject userAuthJson, CompletableFuture<String> authorizationTokenFuture) {
        AUTH_TOKEN_SERVICE_EXECUTOR.execute(() -> {
            Optional<String> authorizationToken = getAuthorizationToken(userAuthJson);
            if(authorizationToken.isPresent()) {
                authorizationTokenFuture.complete(authorizationToken.get());
            } else {
                authorizationTokenFuture.cancel(false);
            }
        });
    }

    private Optional<String> getAuthorizationToken(JSONObject userAuthJson) {
        try {
            JSONObject response = postToServerWithJsonBody(refreshTokenUrl, userAuthJson);
            String authorizationToken = response.getString(AUTHORIZATION_TOKEN);
            return Optional.of(authorizationToken);
        } catch(RequestException | JSONException e) {
            return Optional.empty();
        }
    }

    private <T> Optional<T> joinFutureResult(CompletableFuture<T> future) {
        try {
            T result = future.join();
            return Optional.of(result);
        } catch(CancellationException e) {
            return Optional.empty();
        }
    }

}
