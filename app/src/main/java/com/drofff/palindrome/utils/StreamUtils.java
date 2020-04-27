package com.drofff.palindrome.utils;

import com.drofff.palindrome.collector.QueueCollector;

class StreamUtils {

    private StreamUtils() {}

    static <T> QueueCollector<T> toQueue() {
        return new QueueCollector<>();
    }

}
