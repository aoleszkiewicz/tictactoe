package com.example.engine;

public enum OXEnum {
    O("O"), X("X"), NONE("");

    private String str;

    private OXEnum(String str) {
        this.str = str;
    }

    @Override
    public String toString() {
        return str;
    }

    public static OXEnum fromString(String value) {
        if (value == null || value.isEmpty()) {
            return NONE;
        } else if (O.str.equalsIgnoreCase(value)) {
            return O;
        } else if (X.str.equalsIgnoreCase(value)) {
            return X;
        }
        return null;
    }
}
