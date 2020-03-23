package com.drofff.palindrome.service;

import com.drofff.palindrome.exception.PalindromeException;
import com.drofff.palindrome.utils.StreamUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static com.drofff.palindrome.constants.SecurityConstants.TOKEN_PARAM;
import static java.util.Calendar.DAY_OF_MONTH;

public class JsonFileAuthorizationTokenService implements AuthorizationTokenService {

    private static final String TOKEN_FILENAME = "auth_token.json";
    private static final int TOKEN_TIME_TO_LIVE_DAYS = 2;

    private static final String DUE_DATE_PARAM = "due_date";

    private final File tokensDir;

    private String authorizationToken;

    public JsonFileAuthorizationTokenService(File tokensDir) {
        this.tokensDir = tokensDir;
    }

    public void saveAuthorizationToken(String token) {
        authorizationToken = token;
        String jsonToken = generateJsonToken(token);
        saveTokenToFile(jsonToken);
    }

    private String generateJsonToken(String token) {
        try {
            JSONObject jsonObject = generateTokenJSONObject(token);
            return jsonObject.toString();
        } catch(JSONException e) {
            throw new PalindromeException(e.getMessage());
        }
    }

    private JSONObject generateTokenJSONObject(String token) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(TOKEN_PARAM, token);
        long dueDateEpochSeconds = generateTokenExpirationDateEpochSeconds();
        jsonObject.put(DUE_DATE_PARAM, dueDateEpochSeconds);
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

    private void saveTokenToFile(String token) {
        try {
            writeTokenToFile(token);
        } catch(IOException e) {
            throw new PalindromeException("Can not save the token to a file " + TOKEN_FILENAME);
        }
    }

    private void writeTokenToFile(String token) throws IOException {
        String tokenPath = getJsonTokenFilePath();
        FileOutputStream fileOutputStream = new FileOutputStream(tokenPath);
        byte[] textBytes = token.getBytes();
        fileOutputStream.write(textBytes);
    }

    public Optional<String> getAuthorizationTokenIfPresent() {
        try {
            String authorizationToken = getAuthorizationToken();
            return Optional.of(authorizationToken);
        } catch(PalindromeException e) {
            return Optional.empty();
        }
    }

    private String getAuthorizationToken() {
        if(haveCachedAuthorizationToken()) {
            return authorizationToken;
        }
        String jsonToken = readJsonToken();
        return authorizationTokenFromJson(jsonToken);
    }

    private boolean haveCachedAuthorizationToken() {
        return authorizationToken != null;
    }

    private String readJsonToken() {
        try {
            return readJsonTokenFromFile();
        } catch(IOException e) {
            throw new PalindromeException("Can not read json token file");
        }
    }

    private String readJsonTokenFromFile() throws IOException {
        String tokenPath = getJsonTokenFilePath();
        FileInputStream fileInputStream = new FileInputStream(tokenPath);
        return StreamUtils.readAllAsString(fileInputStream);
    }

    private String getJsonTokenFilePath() {
        String rootPath = tokensDir.getAbsolutePath();
        return rootPath + "/" + TOKEN_FILENAME;
    }

    private String authorizationTokenFromJson(String json) {
        try {
            return parseTokenFromJson(json);
        } catch(JSONException e) {
             throw new PalindromeException("Invalid json token");
        }
    }

    private String parseTokenFromJson(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        long dueDateEpochSeconds = jsonObject.getLong(DUE_DATE_PARAM);
        validateTokenIsNonExpired(dueDateEpochSeconds);
        return jsonObject.getString(TOKEN_PARAM);
    }

    private void validateTokenIsNonExpired(long dueDateEpochSeconds) {
        if(isDueDateExpired(dueDateEpochSeconds)) {
            throw new PalindromeException("Token is expired");
        }
    }

    private boolean isDueDateExpired(long dueDateEpochSeconds) {
        long nowEpochSeconds = new Date().getTime();
        return nowEpochSeconds >= dueDateEpochSeconds;
    }

}
