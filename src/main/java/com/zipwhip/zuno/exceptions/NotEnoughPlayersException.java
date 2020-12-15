package com.zipwhip.zuno.exceptions;

public class NotEnoughPlayersException extends RuntimeException {

    public NotEnoughPlayersException() {
        super("The game doesn't have the minimum number of players.");
    }
}
