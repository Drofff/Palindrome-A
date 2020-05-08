package com.drofff.palindrome.entity;

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
