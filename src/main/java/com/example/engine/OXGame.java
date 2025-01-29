package com.example.engine;

public interface OXGame {
        void initialize();
        void setField(int index);
        OXEnum getField(int index);
        OXEnum getTurn();
        OXEnum getWinner();
        int getStep();
}
