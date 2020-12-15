package com.zipwhip.zuno.exceptions;

public class CardDoesNotMatchException extends RuntimeException {

    public CardDoesNotMatchException() {
        super("The card you've played doesn't match. Try to match with one of the emojis.");
    }
}
