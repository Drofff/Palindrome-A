package com.drofff.palindrome.service;

import com.drofff.palindrome.annotation.StringResource;
import com.drofff.palindrome.exception.PalindromeException;
import com.drofff.palindrome.exception.ValidationException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static com.drofff.palindrome.R.string.authorization_token_file;
import static com.drofff.palindrome.constants.SecurityConstants.AUTHORIZATION_TOKEN;
import static com.drofff.palindrome.constants.SecurityConstants.DUE_DATE;
import static com.drofff.palindrome.utils.ValidationUtils.validateIsFutureDateEpochSeconds;
import static com.drofff.palindrome.utils.ValidationUtils.validateNotNull;
import static java.util.Calendar.DAY_OF_MONTH;

public class AuthorizationTokenServiceImpl implements AuthorizationTokenService {

    private static final int TOKEN_TIME_TO_LIVE_DAYS = 2;

    private final String authorizationTokenFilename;
    private final FileService fileService;
    private final AuthenticationService authenticationService;

    private String authorizationToken;

    public AuthorizationTokenServiceImpl(@StringResource(id = authorization_token_file) String authorizationTokenFilename,
                                         FileService fileService, AuthenticationService authenticationService) {
        this.authorizationTokenFilename = authorizationTokenFilename;
        this.fileService = fileService;
        this.authenticationService = authenticationService;
    }

    @Override
    public void saveAuthorizationToken(String token) {
        validateNotNull(token, "Token should not be null");
        authorizationToken = token;
        String json = wrapTokenIntoJson(token);
        fileService.saveFile(authorizationTokenFilename, json);
    }

    private String wrapTokenIntoJson(String token) {
        try {
            return wrapTokenIntoJSONObject(token).toString();
        } catch(JSONException e) {
            throw new PalindromeException(e.getMessage());
        }
    }

    private JSONObject wrapTokenIntoJSONObject(String token) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(AUTHORIZATION_TOKEN, token);
        long dueDateEpochSeconds = generateTokenExpirationDateEpochSeconds();
        jsonObject.put(DUE_DATE, dueDateEpochSeconds);
        return jsonObject;
    }

    private long generateTokenExpirationDateEpochSeconds() {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(DAY_OF_MONTH, TOKEN_TIME_TO_LIVE_DAYS);
        Date dueDate = calendar.getTime();
        return dueDate.getTime();
    }

    @Override
    public Optional<String> getAuthorizationTokenIfPresent() {
        try {
            String token = getAuthorizationToken();
            return Optional.of(token);
        } catch(PalindromeException e) {
            return authenticationService.requestAuthorizationToken();
        }
    }

    private String getAuthorizationToken() {
        if(haveCachedAuthorizationToken()) {
            return authorizationToken;
        }
        String jsonWithToken = fileService.loadFileByName(authorizationTokenFilename);
        return authorizationTokenFromJson(jsonWithToken);
    }

    private boolean haveCachedAuthorizationToken() {
        return authorizationToken != null;
    }

    private String authorizationTokenFromJson(String json) {
        try {
            return parseTokenFromJson(json);
        } catch(JSONException e) {
            throw new PalindromeException("Invalid json token");
        } catch(ValidationException e) {
            throw new PalindromeException(e.getMessage());
        }
    }

    private String parseTokenFromJson(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        long dueDateEpochSeconds = jsonObject.getLong(DUE_DATE);
        validateIsFutureDateEpochSeconds(dueDateEpochSeconds, "Token has expired");
        return jsonObject.getString(AUTHORIZATION_TOKEN);
    }

}
