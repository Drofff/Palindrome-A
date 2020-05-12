package com.drofff.palindrome.dto;

import com.drofff.palindrome.R;
import com.drofff.palindrome.annotation.DateTime;
import com.drofff.palindrome.annotation.TextViewId;
import com.drofff.palindrome.type.Displayable;
import com.drofff.palindrome.type.ListedName;

public class CarDto implements Displayable {

    @TextViewId(R.id.car_number_value)
    private String number;

    @TextViewId(R.id.body_number_value)
    private String bodyNumber;

    @TextViewId(R.id.color_value)
    private String color;

    private ListedName brand;

    private String model;

    private ListedName bodyType;

    @TextViewId(R.id.weigh_value)
    private Double weight;

    private ListedName licenceCategory;

    private Double engineVolume;

    private ListedName engineType;

    @DateTime(format = "dd.MM.yyyy")
    @TextViewId(R.id.registration_date_value)
    private String registrationDate;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBodyNumber() {
        return bodyNumber;
    }

    public void setBodyNumber(String bodyNumber) {
        this.bodyNumber = bodyNumber;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public ListedName getBrand() {
        return brand;
    }

    public void setBrand(ListedName brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public ListedName getBodyType() {
        return bodyType;
    }

    public void setBodyType(ListedName bodyType) {
        this.bodyType = bodyType;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public ListedName getLicenceCategory() {
        return licenceCategory;
    }

    public void setLicenceCategory(ListedName licenceCategory) {
        this.licenceCategory = licenceCategory;
    }

    public Double getEngineVolume() {
        return engineVolume;
    }

    public void setEngineVolume(Double engineVolume) {
        this.engineVolume = engineVolume;
    }

    public ListedName getEngineType() {
        return engineType;
    }

    public void setEngineType(ListedName engineType) {
        this.engineType = engineType;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

}
