package com.zipwhip.zuno.exceptions;

public class PlayerAlreadyJoinedException extends RuntimeException {

    public PlayerAlreadyJoinedException() {
        super("You're already playing.");
    }
}
