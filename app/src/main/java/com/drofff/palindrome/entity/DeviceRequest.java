package com.drofff.palindrome.entity;

import com.drofff.palindrome.exception.PalindromeException;

import org.json.JSONException;
import org.json.JSONObject;

import static com.drofff.palindrome.constants.JsonConstants.OPTION_ID_KEY;
import static com.drofff.palindrome.constants.JsonConstants.TOKEN_KEY;

public class DeviceRequest {

    private String token;

    private String optionId;

    public static DeviceRequest fromJSONObject(JSONObject jsonObject) {
        try {
            return parseDeviceRequestFromJSONObject(jsonObject);
        } catch(JSONException e) {
            throw new PalindromeException(e.getMessage());
        }
    }

    private static DeviceRequest parseDeviceRequestFromJSONObject(JSONObject jsonObject) throws JSONException {
        String token = jsonObject.getString(TOKEN_KEY);
        String optionId = jsonObject.getString(OPTION_ID_KEY);
        return new DeviceRequest(token, optionId);
    }

    private DeviceRequest(String token, String optionId) {
        this.token = token;
        this.optionId = optionId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

}
