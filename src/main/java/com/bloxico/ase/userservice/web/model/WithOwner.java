package com.bloxico.ase.userservice.web.model;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.function.Function;

import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor(access = PRIVATE)
public class WithOwner<R> {

    Long owner;
    R request;

    public static <R> WithOwner<R> of(long owner, R request) {
        return new WithOwner<>(owner, requireNonNull(request));
    }

    public static <R> WithOwner<R> any(R request) {
        return new WithOwner<>(null, requireNonNull(request));
    }

    public <R2> WithOwner<R2> update(Function<R, R2> updater) {
        requireNonNull(updater);
        return new WithOwner<>(owner, requireNonNull(updater.apply(request)));
    }

}
