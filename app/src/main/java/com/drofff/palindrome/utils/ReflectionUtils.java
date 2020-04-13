package com.drofff.palindrome.utils;

import com.drofff.palindrome.exception.PalindromeException;

import java.lang.reflect.Constructor;

public class ReflectionUtils {

    private ReflectionUtils() {}

    public static Class<?> getClassByName(String className) {
        try {
            return Class.forName(className);
        } catch(ClassNotFoundException e) {
            throw new PalindromeException("Can not find a class with name " + className);
        }
    }

    public static <T> T constructInstance(Constructor<T> constructor, Object[] params) {
        try {
            return constructor.newInstance(params);
        } catch(Exception e) {
            throw new PalindromeException(e.getMessage());
        }
    }

}
