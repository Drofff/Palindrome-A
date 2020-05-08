package com.drofff.palindrome.entity;

import androidx.annotation.NonNull;

import static com.drofff.palindrome.constants.UiConstants.MAX_SPINNER_TEXT_LENGTH;
import static com.drofff.palindrome.utils.StringUtils.shortenIfNeeded;

public class ViolationType {

    private String id;

    private String name;

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
