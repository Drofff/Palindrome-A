package com.drofff.palindrome.dto;

import com.drofff.palindrome.R;
import com.drofff.palindrome.annotation.TextViewId;
import com.drofff.palindrome.type.Displayable;

public class CarDto implements Displayable {

    @TextViewId(R.id.car_model)
    private String label;

    @TextViewId(R.id.car_number_value)
    private String number;

    @TextViewId(R.id.body_number_value)
    private String bodyNumber;

    @TextViewId(R.id.color_value)
    private String color;

    private String bodyType;

    @TextViewId(R.id.weigh_value)
    private Double weight;

    private String licenceCategory;

    @TextViewId(R.id.engine_value)
    private String engine;

    @TextViewId(R.id.registration_date_value)
    private String registrationDate;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

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

    public String getBodyType() {
        return bodyType;
    }

    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getLicenceCategory() {
        return licenceCategory;
    }

    public void setLicenceCategory(String licenceCategory) {
        this.licenceCategory = licenceCategory;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

}
