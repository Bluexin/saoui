package com.saomc.util;

public final class JString implements Strings {

    private final String string;

    public JString(Object object) {
        string = object instanceof String ? (String) object : String.valueOf(object);
    }

    public JString() {
        string = "";
    }

    @Override
    public final String toString() {
        return string;
    }

}
