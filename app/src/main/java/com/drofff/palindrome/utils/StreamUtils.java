package com.drofff.palindrome.utils;

import com.drofff.palindrome.collector.QueueCollector;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Stream;

class StreamUtils {

    private StreamUtils() {}

    static <T> QueueCollector<T> toQueue() {
        return new QueueCollector<>();
    }

    public static <T> Stream<T> streamOfEnumeration(Enumeration<T> enumeration) {
        List<T> list = new ArrayList<>();
        while(enumeration.hasMoreElements()) {
            T elem = enumeration.nextElement();
            list.add(elem);
        }
        return list.stream();
    }

}
