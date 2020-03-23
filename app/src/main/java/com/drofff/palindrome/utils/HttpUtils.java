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

public class HttpUtils {

    private static final String AUTHORIZATION_TOKEN_HEADER = "Authorization";

    private static final String CONTENT_TYPE_HEADER = "Content-Type";

    private static final String ACCEPT_HEADER = "Accept";

    private static final String JSON_MEDIA_TYPE = "application/json";

    private HttpUtils() {}

    public static JSONObject postAtUrlWithJsonBody(String url, JSONObject jsonBody) {
        try {
            return postRequestAtUrlWithJsonBody(url, jsonBody);
        } catch(IOException | JSONException e) {
            e.printStackTrace();
            throw new RequestException(e.getMessage());
        }
    }

    private static JSONObject postRequestAtUrlWithJsonBody(String url, JSONObject jsonBody) throws IOException, JSONException {
        HttpURLConnection httpURLConnection = requestUrlWithMethod(url, POST);
        attachAuthorizationTokenIfPresent(httpURLConnection);
        attachJsonContentTypeHeaders(httpURLConnection);
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

    public static JSONObject getAtUrl(String url) {
        try {
            return getRequestAtUrl(url);
        } catch(IOException | JSONException e) {
            throw new RequestException(e.getMessage());
        }
    }

    private static JSONObject getRequestAtUrl(String url) throws IOException, JSONException {
        HttpURLConnection httpURLConnection = requestUrlWithMethod(url, GET);
        attachAuthorizationTokenIfPresent(httpURLConnection);
        attachJsonContentTypeHeaders(httpURLConnection);
        JSONObject response = getJsonResponse(httpURLConnection);
        httpURLConnection.disconnect();
        return response;
    }

    private static HttpURLConnection requestUrlWithMethod(String url, HttpMethod method) throws IOException {
        URL request = new URL(url);
        HttpURLConnection httpURLConnection = (HttpURLConnection) request.openConnection();
        httpURLConnection.setRequestMethod(method.name());
        return httpURLConnection;
    }

    private static void attachAuthorizationTokenIfPresent(HttpURLConnection connection) {
        AuthorizationTokenService authorizationTokenService = BeanContext.getBeanOfClass(AuthorizationTokenService.class);
        authorizationTokenService.getAuthorizationTokenIfPresent()
                .ifPresent(token -> connection.setRequestProperty(AUTHORIZATION_TOKEN_HEADER, token));
    }

    private static void attachJsonContentTypeHeaders(HttpURLConnection httpURLConnection) {
        httpURLConnection.setRequestProperty(CONTENT_TYPE_HEADER, JSON_MEDIA_TYPE);
        httpURLConnection.setRequestProperty(ACCEPT_HEADER, JSON_MEDIA_TYPE);
    }

    private static JSONObject getJsonResponse(HttpURLConnection connection) throws IOException, JSONException {
        String responseStr = readHttpResponseAsStr(connection);
        return new JSONObject(responseStr);
    }

    private static String readHttpResponseAsStr(HttpURLConnection connection) throws IOException {
        InputStream inputStream = connection.getInputStream();
        return StreamUtils.readAllAsString(inputStream);
    }

}
