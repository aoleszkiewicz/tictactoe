package org.tictactoe.model;

import org.tictactoe.interfaces.IPlayer;

public class Player implements IPlayer {

    String name;

    public Player(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
