package com.drofff.palindrome.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Queue;

import static com.drofff.palindrome.utils.StreamUtils.toQueue;

public class FormattingUtils {

    private static final String PARAM_PREFIX = "${";
    private static final String PARAM_SUFFIX = "}";

    private FormattingUtils() {}

    public static String resolveStringParams(String source, Map<String, String> params) {
        Queue<Map.Entry<String, String>> paramsQueue = paramsAsQueue(params);
        return resolveParamsRecursive(source, paramsQueue);
    }

    private static Queue<Map.Entry<String, String>> paramsAsQueue(Map<String, String> params) {
        return params.entrySet().stream()
                .collect(toQueue());
    }

    private static String resolveParamsRecursive(String source, Queue<Map.Entry<String, String>> paramsQueue) {
        Map.Entry<String, String> param = paramsQueue.poll();
        if(param == null) {
            return source;
        }
        String sourceWithParam = putParam(source, param);
        return resolveParamsRecursive(sourceWithParam, paramsQueue);
    }

    private static String putParam(String source, Map.Entry<String, String> param) {
        String paramName = PARAM_PREFIX + param.getKey() + PARAM_SUFFIX;
        return source.replace(paramName, param.getValue());
    }

    static <T extends Exception> String getNamedExceptionMessage(T e) {
        String exceptionName = e.getClass().getSimpleName();
        return exceptionName + ": " + e.getMessage();
    }

    static String dateToStrByFormat(Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

}
