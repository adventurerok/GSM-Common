package com.ithinkrok.util;

import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Pair<K, V> {

    private final K first;
    private final V second;


    public Pair(K first, V second) {
        this.first = first;
        this.second = second;
    }


    public K first() {
        return first;
    }


    public V second() {
        return second;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pair<?, ?> pair = (Pair<?, ?>) o;

        if (first != null ? !first.equals(pair.first) : pair.first != null) return false;
        return second != null ? second.equals(pair.second) : pair.second == null;
    }


    @Override
    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        return result;
    }

    public static <A, B> Collector<Pair<A, B>, ?, Map<A, B>> collect() {
        return Collectors.toMap(Pair::first, Pair::second);
    }
}
