package com.drofff.palindrome.entity;

import com.drofff.palindrome.exception.PalindromeException;

import org.json.JSONException;
import org.json.JSONObject;

import static com.drofff.palindrome.constants.SecurityConstants.AUTHENTICATION_TOKEN;
import static com.drofff.palindrome.constants.SecurityConstants.AUTHORIZATION_TOKEN;
import static com.drofff.palindrome.constants.SecurityConstants.USER_ID;

public class ApiTokens {

    private String authorizationToken;

    private String authenticationToken;

    private String userId;

    public static ApiTokens fromJSONObject(JSONObject jsonObject) {
        try {
            return parseApiTokensFromJSONObject(jsonObject);
        } catch(JSONException e) {
            throw new PalindromeException(e.getMessage());
        }
    }

    private static ApiTokens parseApiTokensFromJSONObject(JSONObject jsonObject) throws JSONException {
        String authorizationToken = jsonObject.getString(AUTHORIZATION_TOKEN);
        String authenticationToken = jsonObject.getString(AUTHENTICATION_TOKEN);
        String userId = jsonObject.getString(USER_ID);
        return new ApiTokens(authorizationToken, authenticationToken, userId);
    }

    private ApiTokens(String authorizationToken, String authenticationToken, String userId) {
        this.authorizationToken = authorizationToken;
        this.authenticationToken = authenticationToken;
        this.userId = userId;
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public void setAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }

    public String getAuthenticationToken() {
        return authenticationToken;
    }

    public void setAuthenticationToken(String authenticationToken) {
        this.authenticationToken = authenticationToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
