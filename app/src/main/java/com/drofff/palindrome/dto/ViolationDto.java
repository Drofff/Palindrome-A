package com.drofff.palindrome.dto;

import com.drofff.palindrome.annotation.DateTime;
import com.drofff.palindrome.entity.ViolationType;
import com.drofff.palindrome.type.Displayable;

public class ViolationDto implements Displayable {

    private ViolationType violationType;

    private String location;

    @DateTime(format = "kk:mm dd.MM.yyyy")
    private String dateTime;

    private boolean paid;

    public ViolationType getViolationType() {
        return violationType;
    }

    public void setViolationType(ViolationType violationType) {
        this.violationType = violationType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

}
