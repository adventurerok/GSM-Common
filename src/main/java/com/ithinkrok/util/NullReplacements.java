package com.ithinkrok.util;

import java.util.function.Consumer;

public class NullReplacements {

    private static final Consumer<?> NULL_CONSUMER = o -> {};

    @SuppressWarnings("unchecked")
    public static <T> Consumer<T> nullConsumer() {
        return (Consumer<T>) NULL_CONSUMER;
    }

}
