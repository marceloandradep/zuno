package com.zipwhip.zuno.exceptions;

public class NotPlayerTurnException extends RuntimeException {

    public NotPlayerTurnException() {
        super("It is not your turn.");
    }
}
