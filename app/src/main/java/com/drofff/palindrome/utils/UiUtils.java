package com.drofff.palindrome.utils;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.drofff.palindrome.utils.ValidationUtils.validateNotNull;

public class UiUtils {

    private UiUtils() {}

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
        View currentFocus = getOrCreateCurrentFocus(activity);
        validateNotNull(inputMethodManager, "Error reaching input method manager");
        inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
    }

    private static View getOrCreateCurrentFocus(Activity activity) {
        View currentFocus = activity.getCurrentFocus();
        return currentFocus == null ? new View(activity) : currentFocus;
    }

}
