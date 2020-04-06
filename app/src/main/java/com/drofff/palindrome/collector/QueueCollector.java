package com.drofff.palindrome.collector;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class QueueCollector<T> implements Collector<T, List<T>, Queue<T>> {

    @Override
    public Supplier<List<T>> supplier() {
        return LinkedList::new;
    }

    @Override
    public BiConsumer<List<T>, T> accumulator() {
        return List::add;
    }

    @Override
    public BinaryOperator<List<T>> combiner() {
        return (list0, list1) -> {
            list0.addAll(list1);
            return list0;
        };
    }

    @Override
    public Function<List<T>, Queue<T>> finisher() {
        return ArrayDeque::new;
    }

    @Override
    public Set<Characteristics> characteristics() {
        throw new UnsupportedOperationException();
    }

}
