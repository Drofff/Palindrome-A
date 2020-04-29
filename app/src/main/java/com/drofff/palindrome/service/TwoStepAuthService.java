package com.drofff.palindrome.service;

public interface TwoStepAuthService {

    void enableTwoStepAuth();

    void disableTwoStepAuth();

    boolean isTwoStepAuthEnabled();

}
