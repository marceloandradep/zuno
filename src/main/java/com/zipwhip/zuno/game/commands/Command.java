package com.zipwhip.zuno.game.commands;

public interface Command {

    String keyword();
    void execute(String source, String[] args);

}
