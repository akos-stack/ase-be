package com.bloxico.ase.userservice.util;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

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

}
