package com.drofff.palindrome.utils;

import com.drofff.palindrome.exception.PalindromeException;
import com.drofff.palindrome.type.FieldValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.drofff.palindrome.utils.ReflectionUtils.constructInstanceOfClass;
import static com.drofff.palindrome.utils.StreamUtils.toQueue;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

public class JsonUtils {

    private static final String PATH_SEGMENTS_SEPARATOR_PATTERN = "\\.";
    private static final String ARRAY_ELEMENT_PATTERN = "^(\\w+)\\[(\\d+)\\]$";
    private static final int ARRAY_ELEMENT_NAME_GROUP = 1;
    private static final int ARRAY_ELEMENT_POSITION_GROUP = 2;

    private JsonUtils() {}

    public static JSONObject getJSONObjectAtPath(JSONObject source, String path) {
        Queue<String> pathSegments = parsePathSegments(path);
        return getJSONObjectAtPathRecursively(source, pathSegments);
    }

    private static Queue<String> parsePathSegments(String path) {
        String[] pathSegments = path.split(PATH_SEGMENTS_SEPARATOR_PATTERN);
        return stream(pathSegments)
                .collect(toQueue());
    }

    private static JSONObject getJSONObjectAtPathRecursively(JSONObject source, Queue<String> pathSegments) {
        String segment = pathSegments.poll();
        if (segment == null) {
            return source;
        }
        JSONObject sourceSegment = getJSONObjectSegment(source, segment);
        return getJSONObjectAtPathRecursively(sourceSegment, pathSegments);
    }

    private static JSONObject getJSONObjectSegment(JSONObject jsonObject, String segment) {
        if (isArrayElement(segment)) {
            return getJSONObjectArrayElementByKey(jsonObject, segment);
        }
        return getJSONObjectByKey(jsonObject, segment);
    }

    private static boolean isArrayElement(String segment) {
        return segment.matches(ARRAY_ELEMENT_PATTERN);
    }

    private static JSONObject getJSONObjectArrayElementByKey(JSONObject jsonObject, String key) {
        String name = getArrayElementNameFromKey(key);
        JSONArray array = getJSONArrayFromObjectByKey(jsonObject, name);
        int position = getArrayElementPositionFromKey(key);
        return getJSONArrayElementAtPositionIfPresent(array, position)
                .orElseThrow(() -> new PalindromeException("JSONArray element position " + position +
                        " is out of range"));
    }

    private static String getArrayElementNameFromKey(String key) {
        return getArrayElementKeyGroupValue(key, ARRAY_ELEMENT_NAME_GROUP);
    }

    private static int getArrayElementPositionFromKey(String key) {
        String positionStr = getArrayElementKeyGroupValue(key, ARRAY_ELEMENT_POSITION_GROUP);
        return Integer.parseInt(positionStr);
    }

    private static String getArrayElementKeyGroupValue(String key, int groupNumber) {
        Matcher matcher = Pattern.compile(ARRAY_ELEMENT_PATTERN).matcher(key);
        if(matcher.find()) {
            return matcher.group(groupNumber);
        }
        throw new PalindromeException("Can not find an element of the pattern group " + groupNumber);
    }

    private static JSONObject getJSONObjectByKey(JSONObject jsonObject, String key) {
        try {
            return jsonObject.getJSONObject(key);
        } catch (JSONException e) {
            throw new PalindromeException(e.getMessage());
        }
    }

    public static List<JSONObject> getListFromJsonByKey(JSONObject jsonObject, String key) {
        JSONArray jsonArray = getJSONArrayFromObjectByKey(jsonObject, key);
        return streamOfJSONArray(jsonArray)
                .collect(toList());
    }

    private static JSONArray getJSONArrayFromObjectByKey(JSONObject jsonObject, String key) {
        try {
            return jsonObject.getJSONArray(key);
        } catch (JSONException e) {
            throw new PalindromeException(e.getMessage());
        }
    }

    private static Stream<JSONObject> streamOfJSONArray(JSONArray jsonArray) {
        return IntStream.range(0, jsonArray.length())
                .mapToObj(position -> getJSONArrayElementAtPositionIfPresent(jsonArray, position))
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    private static Optional<JSONObject> getJSONArrayElementAtPositionIfPresent(JSONArray jsonArray, int position) {
        try {
            JSONObject element = jsonArray.getJSONObject(position);
            return Optional.of(element);
        } catch (JSONException e) {
            return Optional.empty();
        }
    }

    public static JSONObject parseJSONObjectFromString(String str) {
        try {
            return new JSONObject(str);
        } catch(JSONException e) {
            throw new PalindromeException(e.getMessage());
        }
    }

    public static <T> T parseObjectOfClassFromJson(Class<T> objClass, JSONObject jsonObject) {
        List<Field> nonStaticFields = getNonStaticFieldsOfClass(objClass);
        List<FieldValue> fieldValues = getFieldValuesFromJson(nonStaticFields, jsonObject);
        T object = constructInstanceOfClass(objClass);
        fieldValues.forEach(fieldValue -> putFieldValueIntoObject(fieldValue, object));
        return object;
    }

    private static List<Field> getNonStaticFieldsOfClass(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        return stream(fields)
                .filter(JsonUtils::isNonStaticField)
                .collect(toList());
    }

    private static boolean isNonStaticField(Field field) {
        return !isStaticField(field);
    }

    private static boolean isStaticField(Field field) {
        int fieldModifiers = field.getModifiers();
        return isStatic(fieldModifiers);
    }

    private static List<FieldValue> getFieldValuesFromJson(List<Field> fields, JSONObject jsonObject) {
        return fields.stream()
                .map(field -> getFieldValueFromJson(field, jsonObject))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());
    }

    private static Optional<FieldValue> getFieldValueFromJson(Field field, JSONObject jsonObject) {
        try {
            String fieldName = field.getName();
            Object value = jsonObject.get(fieldName);
            FieldValue fieldValue = new FieldValue(field, value);
            return Optional.of(fieldValue);
        } catch(JSONException e) {
            return Optional.empty();
        }
    }

    private static void putFieldValueIntoObject(FieldValue fieldValue, Object object) {
        try {
            Field destinedField = fieldValue.getField();
            Object value = fieldValue.getValue();
            destinedField.setAccessible(true);
            destinedField.set(object, value);
        } catch(IllegalAccessException e) {
            throw new PalindromeException(e.getMessage());
        }
    }

}