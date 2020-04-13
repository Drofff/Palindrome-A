package com.drofff.palindrome.utils;

import com.drofff.palindrome.context.BeanContext;
import com.drofff.palindrome.enums.HttpMethod;
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
import static com.drofff.palindrome.utils.IOUtils.readAllAsString;

public class HttpUtils {

    private static final String AUTHORIZATION_TOKEN_HEADER = "Authorization";

    private static final String CONTENT_TYPE_HEADER = "Content-Type";

    private static final String ACCEPT_HEADER = "Accept";

    private static final String JSON_MEDIA_TYPE = "application/json";

    private HttpUtils() {}

    public static JSONObject postToServerWithJsonBody(String url, JSONObject jsonBody) {
        try {
            return postRequestToServerWithJsonBody(url, jsonBody);
        } catch(IOException | JSONException e) {
            throw new RequestException(e.getMessage());
        }
    }

    private static JSONObject postRequestToServerWithJsonBody(String url, JSONObject jsonBody) throws IOException, JSONException {
        HttpURLConnection httpURLConnection = buildJsonRequestToUrlUsingMethod(url, POST);
        attachAuthorizationTokenIfPresent(httpURLConnection);
        attachJsonBodyToRequest(jsonBody, httpURLConnection);
        JSONObject response = getJsonResponse(httpURLConnection);
        httpURLConnection.disconnect();
        return response;
    }

    private static void attachJsonBodyToRequest(JSONObject jsonBody, HttpURLConnection connection) throws IOException {
        connection.setDoOutput(true);
        try(OutputStream outputStream = connection.getOutputStream()) {
            String jsonStr = jsonBody.toString();
            byte[] bodyBytes = jsonStr.getBytes();
            outputStream.write(bodyBytes);
        }
    }

    public static JSONObject getFromServer(String url) {
        try {
            return getRequestToServer(url);
        } catch(IOException | JSONException e) {
            throw new RequestException(e.getMessage());
        }
    }

    private static JSONObject getRequestToServer(String url) throws IOException, JSONException {
        HttpURLConnection httpURLConnection = buildJsonRequestToUrlUsingMethod(url, GET);
        attachAuthorizationTokenIfPresent(httpURLConnection);
        JSONObject response = getJsonResponse(httpURLConnection);
        httpURLConnection.disconnect();
        return response;
    }

    public static JSONObject getAtUrl(String url) {
        try {
            return getRequestAtUrl(url);
        } catch(IOException | JSONException e) {
            throw new RequestException(e.getMessage());
        }
    }

    private static JSONObject getRequestAtUrl(String url) throws IOException, JSONException {
        HttpURLConnection httpURLConnection = buildJsonRequestToUrlUsingMethod(url, GET);
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

    private static void attachAuthorizationTokenIfPresent(HttpURLConnection connection) {
        AuthorizationTokenService authorizationTokenService = BeanContext.getBeanOfClass(AuthorizationTokenService.class);
        authorizationTokenService.getAuthorizationTokenIfPresent()
                .ifPresent(token -> connection.setRequestProperty(AUTHORIZATION_TOKEN_HEADER, token));
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
