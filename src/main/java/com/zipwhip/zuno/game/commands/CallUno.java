package com.zipwhip.zuno.game.commands;

import com.zipwhip.zuno.exceptions.InvalidCommandException;
import com.zipwhip.zuno.game.GameManager;
import com.zipwhip.zuno.game.text.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CallUno implements Command {

    private final GameManager gameManager;
    private final MessageUtils messageUtils;

    @Override
    public String keyword() {
        return "/uno";
    }

    @Override
    public void execute(String source, String[] args) {
        if (args.length > 2) {
            throw new InvalidCommandException(
                    "Invalid syntax. " + messageUtils.callUnoInstructions());
        }

        Integer avatar = null;

        if (args.length == 2) {
            try {
                avatar = Integer.parseInt(args[1]);
            } catch (NumberFormatException nfe) {
                throw new InvalidCommandException(
                        "Invalid syntax. " + messageUtils.callUnoInstructions());
            }
        }

        gameManager.uno(source, avatar);
    }
}
