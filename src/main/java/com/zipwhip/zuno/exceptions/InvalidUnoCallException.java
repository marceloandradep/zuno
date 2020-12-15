package com.zipwhip.zuno.exceptions;

public class InvalidUnoCallException extends RuntimeException {

    public InvalidUnoCallException() {
        super("That player have more than one card or called uno already.");
    }
}
