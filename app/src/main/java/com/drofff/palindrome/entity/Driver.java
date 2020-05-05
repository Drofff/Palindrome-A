package com.drofff.palindrome.entity;

import com.drofff.palindrome.exception.PalindromeException;

import org.json.JSONException;
import org.json.JSONObject;

public class Driver {

    private String firstName;

    private String middleName;

    private String lastName;

    private String photoUrl;

    private String licenceNumber;

    public static Driver fromJSONObject(JSONObject jsonObject) {
        try {
            return parseDriverDtoFromJSONObject(jsonObject);
        } catch(JSONException e) {
            throw new PalindromeException(e.getMessage());
        }
    }

    private static Driver parseDriverDtoFromJSONObject(JSONObject jsonObject) throws JSONException {
        String firstName = jsonObject.getString("firstName");
        String middleName = jsonObject.getString("middleName");
        String lastName = jsonObject.getString("lastName");
        String photoUrl = jsonObject.getString("photoUrl");
        String licenceNumber = jsonObject.getString("licenceNumber");
        return new Driver(firstName, middleName, lastName, photoUrl, licenceNumber);
    }

    private Driver(String firstName, String middleName, String lastName, String photoUrl, String licenceNumber) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.photoUrl = photoUrl;
        this.licenceNumber = licenceNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getLicenceNumber() {
        return licenceNumber;
    }

    public void setLicenceNumber(String licenceNumber) {
        this.licenceNumber = licenceNumber;
    }

}
