package com.drofff.palindrome.entity;

import com.drofff.palindrome.exception.PalindromeException;

import org.json.JSONException;
import org.json.JSONObject;

import static com.drofff.palindrome.utils.StringUtils.joinNonNullPartsWith;

public class Police {

    private String id;

    private String firstName;

    private String lastName;

    private String middleName;

    private String position;

    private String tokenNumber;

    private String photoUrl;

    private String department;

    private boolean twoStepAuthEnabled;

    public static Police fromJSONObject(JSONObject jsonObject) {
        try {
            return parsePoliceFromJSONObject(jsonObject);
        } catch (JSONException e) {
            throw new PalindromeException(e.getMessage());
        }
    }

    private static Police parsePoliceFromJSONObject(JSONObject jsonObject) throws JSONException {
        Police police = new Police();
        police.id = jsonObject.getString("id");
        police.firstName = jsonObject.getString("firstName");
        police.lastName = jsonObject.getString("lastName");
        police.middleName = jsonObject.getString("middleName");
        police.position = jsonObject.getString("position");
        police.tokenNumber = jsonObject.getString("tokenNumber");
        police.photoUrl = jsonObject.getString("photoUrl");
        police.department = jsonObject.getString("department");
        police.twoStepAuthEnabled = jsonObject.getBoolean("twoStepAuthEnabled");
        return police;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getTokenNumber() {
        return tokenNumber;
    }

    public void setTokenNumber(String tokenNumber) {
        this.tokenNumber = tokenNumber;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getFullName() {
        return joinNonNullPartsWith(" ", firstName, middleName, lastName);
    }

    public boolean isTwoStepAuthEnabled() {
        return twoStepAuthEnabled;
    }

    public void setTwoStepAuthEnabled(boolean twoStepAuthEnabled) {
        this.twoStepAuthEnabled = twoStepAuthEnabled;
    }

}
