package com.zipwhip.zuno.exceptions;

public class GameNotFoundException extends RuntimeException {

    public GameNotFoundException() {
        super("Game not found.");
    }
}
