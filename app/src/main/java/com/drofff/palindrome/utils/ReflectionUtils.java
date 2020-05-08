package com.drofff.palindrome.utils;

import com.drofff.palindrome.exception.PalindromeException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class ReflectionUtils {

    private ReflectionUtils() {}

    public static Class<?> getClassByName(String className) {
        try {
            return Class.forName(className);
        } catch(ClassNotFoundException e) {
            throw new PalindromeException("Can not find a class with name " + className);
        }
    }

    public static <T> T constructInstanceOfClass(Class<T> clazz) {
        try {
            Object instance = clazz.newInstance();
            return clazz.cast(instance);
        } catch(Exception e) {
            throw new PalindromeException(e.getMessage());
        }
    }

    public static <T> T constructInstance(Constructor<T> constructor, Object[] params) {
        try {
            return constructor.newInstance(params);
        } catch(Exception e) {
            throw new PalindromeException(e.getMessage());
        }
    }

    static Object getFieldValueOfObject(Field field, Object object) {
        try {
            field.setAccessible(true);
            return field.get(object);
        } catch(IllegalAccessException e) {
            throw new PalindromeException(e.getMessage());
        }
    }

}
