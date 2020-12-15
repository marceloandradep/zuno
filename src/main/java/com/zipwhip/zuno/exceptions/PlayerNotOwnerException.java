package com.zipwhip.zuno.exceptions;

public class PlayerNotOwnerException extends RuntimeException {

    public PlayerNotOwnerException() {
        super("You're not the owner of the game session.");
    }
}
