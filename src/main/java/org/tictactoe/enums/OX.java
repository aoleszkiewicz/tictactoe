package org.tictactoe.enums;

public enum OX {
    O("O"),
    X("X"),
    EMPTY("");

    private final String action;

    public String getAction() {
        return action;
    }

    OX(String action) {
        this.action = action;
    }
}
