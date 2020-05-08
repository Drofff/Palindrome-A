package com.drofff.palindrome.utils;

import android.app.Activity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.drofff.palindrome.annotation.TextViewId;

import java.lang.reflect.Field;
import java.util.Map;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.drofff.palindrome.utils.ReflectionUtils.getFieldValueOfObject;
import static com.drofff.palindrome.utils.ValidationUtils.validateNotNull;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

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

    public static boolean isHomeButton(MenuItem menuItem) {
        return menuItem.getItemId() == android.R.id.home;
    }

    public static <T> void putMappedTextViewValuesIntoView(T source, View view) {
        Map<Integer, Field> textViewIdMappings = getTextViewIdMappingsOfClass(source.getClass());
        textViewIdMappings.entrySet()
                .forEach(textViewMapping -> mapTextViewValueOfSource(source, textViewMapping, view));
    }

    private static Map<Integer, Field> getTextViewIdMappingsOfClass(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        return stream(fields)
                .filter(UiUtils::hasTextViewIdAnnotation)
                .map(annotatedField -> {
                    int textViewId = getTextViewIdFromAnnotatedField(annotatedField);
                    return new Object() {
                      int viewId = textViewId;
                      Field field = annotatedField;
                    };
                }).collect(toMap(obj -> obj.viewId, obj -> obj.field));
    }

    private static boolean hasTextViewIdAnnotation(Field field) {
        return field.getAnnotation(TextViewId.class) != null;
    }

    private static int getTextViewIdFromAnnotatedField(Field field) {
        TextViewId textViewId = field.getAnnotation(TextViewId.class);
        validateNotNull(textViewId, "Field should obtain TextViewId annotation");
        return textViewId.value();
    }

    private static <T> void mapTextViewValueOfSource(T source, Map.Entry<Integer, Field> textViewMapping, View rootView) {
        Field mappedField = textViewMapping.getValue();
        String value = getFieldValueOfObject(mappedField, source).toString();
        Integer textViewId = textViewMapping.getKey();
        TextView textView = rootView.findViewById(textViewId);
        textView.setText(value);
    }

}
