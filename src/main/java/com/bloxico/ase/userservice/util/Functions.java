package com.bloxico.ase.userservice.util;

import java.util.function.*;

public class Functions {

    private Functions() {
        throw new AssertionError();
    }

    public static <T> UnaryOperator<T> doto(Consumer<? super T> c) {
        return x -> {
            c.accept(x);
            return x;
        };
    }

    public static <T> BinaryOperator<T> throwingMerger() {
        return (u, v) -> {
            throw new IllegalStateException(String.format("Duplicate key %s", u));
        };
    }

}
