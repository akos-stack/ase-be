package com.bloxico.ase.userservice.util;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class JwtBlacklistInMemory {

    private JwtBlacklistInMemory() {
        throw new AssertionError();
    }

    private static final Set<String> BLACKLIST = ConcurrentHashMap.newKeySet();

    public static boolean contains(String token) {
        return BLACKLIST.contains(token);
    }

    public static boolean add(String token) {
        return BLACKLIST.add(token);
    }

    public static boolean addAll(Collection<? extends String> tokens) {
        return BLACKLIST.addAll(tokens);
    }

}
