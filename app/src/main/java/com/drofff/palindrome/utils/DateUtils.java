package com.drofff.palindrome.utils;

import com.drofff.palindrome.exception.PalindromeException;
import com.drofff.palindrome.exception.ValidationException;

import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.List;

import static com.drofff.palindrome.utils.ReflectionUtils.constructInstance;
import static com.drofff.palindrome.utils.ValidationUtils.validateNotNull;
import static java.lang.Math.min;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

public class DateUtils {

    private static final List<Integer> AVAILABLE_CONSTRUCTOR_SIZES = asList(3, 5, 6);

    private static final int[] DATE_PARAMETER_BIASES = { -1900, -1, 0, -1, -1, -1 };

    private DateUtils() {}

    public static Date dateOf(Integer[] dateArray) {
        validateDateArray(dateArray);
        Integer[] truncatedDateArray = truncateDateArrayToClosestSize(dateArray);
        Integer[] dateArrayWithBiases = addBiasesToDateArray(truncatedDateArray);
        Constructor<Date> dateConstructor = getDateConstructorOfSize(dateArrayWithBiases.length);
        return constructInstance(dateConstructor, dateArrayWithBiases);
    }

    private static void validateDateArray(Integer[] dateArray) {
        validateNotNull(dateArray, "Date array should not be null");
        validateDateArrayLength(dateArray);
    }

    private static void validateDateArrayLength(Integer[] dateArray) {
        int minSize = getMinConstructorSize();
        if(dateArray.length < minSize) {
            throw new ValidationException("Minimal date array length is " + minSize);
        }
    }

    private static int getMinConstructorSize() {
        return AVAILABLE_CONSTRUCTOR_SIZES.stream()
                .mapToInt(i -> i)
                .min()
                .orElseThrow(() -> new PalindromeException("Constructor sizes are not available"));
    }

    private static Integer[] truncateDateArrayToClosestSize(Integer[] dateArray) {
        int closestSize = roundToClosestConstructorSize(dateArray.length);
        return stream(dateArray)
                .limit(closestSize)
                .collect(toList())
                .toArray(new Integer[] {});
    }

    private static int roundToClosestConstructorSize(int desiredSize) {
        return AVAILABLE_CONSTRUCTOR_SIZES.stream()
                .mapToInt(i -> i)
                .filter(size -> desiredSize >= size)
                .max()
                .orElseThrow(() -> new PalindromeException("No matching constructor for date of size " + desiredSize));
    }

    private static Integer[] addBiasesToDateArray(Integer[] dateArray) {
        Integer[] dateArrayWithBiases = new Integer[dateArray.length];
        int parametersCount = min(dateArray.length, DATE_PARAMETER_BIASES.length);
        for(int i = 0; i < parametersCount; i++) {
            dateArrayWithBiases[i] = dateArray[i] + DATE_PARAMETER_BIASES[i];
        }
        return dateArrayWithBiases;
    }

    private static Constructor<Date> getDateConstructorOfSize(int size) {
        Class<?>[] argumentTypes = getArgumentTypesForConstructorOfSize(size);
        return getDateConstructorWithArgumentTypes(argumentTypes);
    }

    private static Class<?>[] getArgumentTypesForConstructorOfSize(int constructorSize) {
        return range(0, constructorSize)
                .mapToObj(i -> int.class)
                .collect(toList())
                .toArray(new Class[] {});
    }

    private static Constructor<Date> getDateConstructorWithArgumentTypes(Class<?>[] argumentTypes) {
        try {
            return Date.class.getConstructor(argumentTypes);
        } catch(NoSuchMethodException e) {
            throw new PalindromeException(e.getMessage());
        }
    }

}
