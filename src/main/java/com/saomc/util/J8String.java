package com.saomc.util;

import java.util.stream.Stream;

public final class J8String {

    private J8String() {
    }

    public static String join(String s0, String... s1) {
        final StringBuilder builder = new StringBuilder(s0);
        Stream.of(s1).forEach(s -> builder.append(s).append(s0));

        return builder.toString().trim();
    }

}
