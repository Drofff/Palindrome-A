package com.drofff.palindrome.utils;

import com.drofff.palindrome.context.BeanContext;
import com.drofff.palindrome.enums.HttpMethod;
import com.drofff.palindrome.exception.PalindromeException;
import com.drofff.palindrome.exception.RequestException;
import com.drofff.palindrome.service.AuthorizationTokenService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.drofff.palindrome.enums.HttpMethod.GET;
import static com.drofff.palindrome.enums.HttpMethod.POST;
import static com.drofff.palindrome.utils.FormattingUtils.getNamedExceptionMessage;
import static com.drofff.palindrome.utils.IOUtils.readAllAsString;

public class HttpUtils {

    private static final String AUTHORIZATION_TOKEN_HEADER = "Authorization";

    private static final String CONTENT_TYPE_HEADER = "Content-Type";

    private static final String ACCEPT_HEADER = "Accept";

    private static final String JSON_MEDIA_TYPE = "application/json";

    private static final JSONObject EMPTY_JSON = null;

    private HttpUtils() {}

    public static JSONObject postToServer(String url) {
        return postToServerWithJsonBody(url, EMPTY_JSON);
    }

    public static JSONObject postToServerWithJsonBody(String url, JSONObject jsonBody) {
        try {
            return authorizedRequestAtUrl(url, POST, jsonBody);
        } catch(IOException | JSONException e) {
            throw asRequestException(e);
        }
    }

    public static JSONObject getFromServer(String url) {
        try {
            return authorizedRequestAtUrl(url, GET, EMPTY_JSON);
        } catch(IOException | JSONException e) {
            throw asRequestException(e);
        }
    }

    private static JSONObject authorizedRequestAtUrl(String url, HttpMethod method, JSONObject body) throws IOException, JSONException {
        HttpURLConnection request = buildJsonRequestToUrlUsingMethod(url, method);
        attachAuthorizationToken(request);
        if(body != null) {
            attachJsonBodyToRequest(body, request);
        }
        JSONObject response = getJsonResponse(request);
        request.disconnect();
        return response;
    }

    private static void attachAuthorizationToken(HttpURLConnection connection) {
        AuthorizationTokenService authorizationTokenService = BeanContext.getBeanOfClass(AuthorizationTokenService.class);
        String authorizationToken = authorizationTokenService.getAuthorizationTokenIfPresent()
                .orElseThrow(() -> new PalindromeException("Missing authorization token"));
        connection.setRequestProperty(AUTHORIZATION_TOKEN_HEADER, authorizationToken);
    }

    public static JSONObject postAtUrlWithJsonBody(String url, JSONObject jsonBody) {
        try {
            return requestAtUrl(url, POST, jsonBody);
        } catch(IOException | JSONException e) {
            throw asRequestException(e);
        }
    }

    public static JSONObject getAtUrl(String url) {
        try {
            return requestAtUrl(url, GET, EMPTY_JSON);
        } catch(IOException | JSONException e) {
            throw asRequestException(e);
        }
    }

    private static <T extends Exception> RequestException asRequestException(T e) {
        String errorMessage = getNamedExceptionMessage(e);
        return new RequestException(errorMessage);
    }

    private static JSONObject requestAtUrl(String url, HttpMethod method, JSONObject body) throws IOException, JSONException {
        HttpURLConnection httpURLConnection = buildJsonRequestToUrlUsingMethod(url, method);
        if(body != EMPTY_JSON) {
            attachJsonBodyToRequest(body, httpURLConnection);
        }
        JSONObject response = getJsonResponse(httpURLConnection);
        httpURLConnection.disconnect();
        return response;
    }

    private static HttpURLConnection buildJsonRequestToUrlUsingMethod(String url, HttpMethod method) throws IOException {
        URL request = new URL(url);
        HttpURLConnection httpURLConnection = (HttpURLConnection) request.openConnection();
        httpURLConnection.setRequestMethod(method.name());
        httpURLConnection.setRequestProperty(CONTENT_TYPE_HEADER, JSON_MEDIA_TYPE);
        httpURLConnection.setRequestProperty(ACCEPT_HEADER, JSON_MEDIA_TYPE);
        return httpURLConnection;
    }

    private static void attachJsonBodyToRequest(JSONObject jsonBody, HttpURLConnection connection) throws IOException {
        connection.setDoOutput(true);
        try(OutputStream outputStream = connection.getOutputStream()) {
            String jsonStr = jsonBody.toString();
            byte[] bodyBytes = jsonStr.getBytes();
            outputStream.write(bodyBytes);
        }
    }

    private static JSONObject getJsonResponse(HttpURLConnection connection) throws IOException, JSONException {
        String responseStr = readHttpResponseAsStr(connection);
        return new JSONObject(responseStr);
    }

    private static String readHttpResponseAsStr(HttpURLConnection connection) throws IOException {
        InputStream inputStream = connection.getInputStream();
        return readAllAsString(inputStream);
    }

}
