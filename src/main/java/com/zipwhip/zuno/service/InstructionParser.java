package com.zipwhip.zuno.service;

import com.zipwhip.zuno.game.commands.Command;
import com.zipwhip.zuno.game.commands.ChatCommand;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InstructionParser {

    private final NotificationDispatcher notificationDispatcher;
    private final Map<String, Command> commands = new HashMap<>();
    private final ChatCommand chatCommand;

    public InstructionParser(NotificationDispatcher notificationDispatcher, ChatCommand chatCommand, List<Command> commands) {
        this.notificationDispatcher = notificationDispatcher;
        this.chatCommand = chatCommand;

        commands.forEach(command -> {
            if (command.keyword() != null) {
                this.commands.put(command.keyword(), command);
            }
        });
    }

    public void parseAndExecute(final String source, final String rawInstruction) {
        try {
            final String instruction = rawInstruction.trim();

            if (instruction.startsWith("/")) {
                String[] tokens = instruction.split("\\s+");

                Command command = commands.get(tokens[0]);
                if (command == null) {
                    throw new RuntimeException("Command not found.");
                }

                command.execute(source, tokens);
            } else {
                chatCommand.execute(source, instruction);
            }
        } catch (Exception e) {
            notificationDispatcher.notifySource(source, e.getMessage());
        }
    }

}
