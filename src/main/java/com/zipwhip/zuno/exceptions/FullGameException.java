package com.zipwhip.zuno.exceptions;

public class FullGameException extends RuntimeException {

    public FullGameException() {
        super("The game is full.");
    }
}
