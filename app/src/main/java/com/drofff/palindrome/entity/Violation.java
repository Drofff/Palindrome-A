package com.drofff.palindrome.entity;

import org.json.JSONObject;

public class Violation {

    private String carNumber;

    private String location;

    private String violationTypeId;

    public Violation() {}

    public Violation(String carNumber, String location, String violationTypeId) {
        this.carNumber = carNumber;
        this.location = location;
        this.violationTypeId = violationTypeId;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getViolationTypeId() {
        return violationTypeId;
    }

    public void setViolationTypeId(String violationTypeId) {
        this.violationTypeId = violationTypeId;
    }

    public JSONObject toJSONObject() {
        return (JSONObject) JSONObject.wrap(this);
    }

}
