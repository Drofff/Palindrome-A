package com.drofff.palindrome.utils;

import com.drofff.palindrome.exception.PalindromeException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;

import static java.util.Arrays.asList;

public class ReflectionUtils {

    private static final List<Class<?>> BOXED_PRIMITIVE_CLASSES = asList(Integer.class, Long.class,
            Float.class, Double.class, Short.class, Byte.class, Character.class, Boolean.class, Void.class);

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

    static boolean isPrimitiveType(Class<?> type) {
        return type.isPrimitive() || isBoxedPrimitiveType(type);
    }

    private static boolean isBoxedPrimitiveType(Class<?> type) {
        return BOXED_PRIMITIVE_CLASSES.stream()
                .anyMatch(boxedPrimitive -> boxedPrimitive.isAssignableFrom(type));
    }

}
