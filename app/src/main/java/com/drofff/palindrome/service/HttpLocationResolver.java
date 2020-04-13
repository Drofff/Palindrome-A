package com.drofff.palindrome.service;

import android.location.Location;

import com.drofff.palindrome.annotation.StringResource;
import com.drofff.palindrome.exception.PalindromeException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.drofff.palindrome.R.string.get_location_url;
import static com.drofff.palindrome.utils.FormattingUtils.resolveStringParams;
import static com.drofff.palindrome.utils.HttpUtils.getAtUrl;
import static com.drofff.palindrome.utils.JsonUtils.getJSONObjectAtPath;
import static com.drofff.palindrome.utils.StringUtils.joinNonNullPartsWith;

public class HttpLocationResolver implements LocationResolver {

    private static final Executor EXECUTOR = Executors.newSingleThreadExecutor();

    private static final String LONGITUDE_PARAM = "longitude";
    private static final String LATITUDE_PARAM = "latitude";

    private static final String JSON_RESPONSE_ADDRESS_PATH = "Response.View[0].Result[0].Location.Address";

    private static final String LABEL_KEY = "Label";
    private static final String POSTAL_CODE_KEY = "PostalCode";
    private static final String STREET_KEY = "Street";

    private static final String ADDRESS_DELIMITER = ", ";

    private final String getLocationUrl;

    public HttpLocationResolver(@StringResource(id = get_location_url) String getLocationUrl) {
        this.getLocationUrl = getLocationUrl;
    }

    public String resolveLocation(Location location) {
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        Map<String, String> locationParams = locationParamsOf(longitude, latitude);
        String getLocationUrlWithParams = resolveStringParams(getLocationUrl, locationParams);
        CompletableFuture<String> locationFuture = new CompletableFuture<>();
        EXECUTOR.execute(() -> requestResolvedLocationAtUrl(getLocationUrlWithParams, locationFuture));
        return locationFuture.join();
    }

    private static Map<String, String> locationParamsOf(double longitude, double latitude) {
        Map<String, String> locationParams = new HashMap<>();
        locationParams.put(LONGITUDE_PARAM, longitude + "");
        locationParams.put(LATITUDE_PARAM, latitude + "");
        return locationParams;
    }

    private void requestResolvedLocationAtUrl(String resolveLocationUrl, CompletableFuture<String> locationFuture) {
        JSONObject responseJson = getAtUrl(resolveLocationUrl);
        JSONObject addressJson = getJSONObjectAtPath(responseJson, JSON_RESPONSE_ADDRESS_PATH);
        String label = getStringFromJSONObjectByKeyIfPresent(addressJson, LABEL_KEY)
                .orElseThrow(() -> new PalindromeException("Missing location label"));
        String postalCode = getStringFromJSONObjectByKeyIfPresent(addressJson, POSTAL_CODE_KEY)
                .orElse(null);
        String street = getStringFromJSONObjectByKeyIfPresent(addressJson, STREET_KEY)
                .orElse(null);
        String location = joinNonNullPartsWith(ADDRESS_DELIMITER, label, postalCode, street);
        locationFuture.complete(location);
    }

    private Optional<String> getStringFromJSONObjectByKeyIfPresent(JSONObject jsonObject, String key) {
        try {
            String str = jsonObject.getString(key);
            return Optional.of(str);
        } catch(JSONException e) {
            return Optional.empty();
        }
    }

}
