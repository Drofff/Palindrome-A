package com.drofff.palindrome.entity;

import androidx.annotation.NonNull;

import com.drofff.palindrome.exception.PalindromeException;

import org.json.JSONException;
import org.json.JSONObject;

import static com.drofff.palindrome.constants.UiConstants.MAX_SPINNER_TEXT_LENGTH;
import static com.drofff.palindrome.utils.StringUtils.shortenIfNeeded;

public class ViolationType {

    private String id;

    private String name;

    public static ViolationType fromJSONObject(JSONObject jsonObject) {
        try {
            return parseViolationTypeFromJSONObject(jsonObject);
        } catch(JSONException e) {
            throw new PalindromeException(e.getMessage());
        }
    }

    private static ViolationType parseViolationTypeFromJSONObject(JSONObject jsonObject) throws JSONException {
        String id = jsonObject.getString("id");
        String name = jsonObject.getString("name");
        return new ViolationType(id, name);
    }

    private ViolationType(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return name != null ? shortenIfNeeded(name, MAX_SPINNER_TEXT_LENGTH) : "";
    }

}
