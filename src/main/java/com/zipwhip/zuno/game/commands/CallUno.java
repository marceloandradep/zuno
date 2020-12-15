package com.zipwhip.zuno.game.commands;

import com.zipwhip.zuno.exceptions.InvalidCommandException;
import com.zipwhip.zuno.game.Game;
import com.zipwhip.zuno.game.GameManager;
import com.zipwhip.zuno.game.Player;
import com.zipwhip.zuno.game.text.MessageUtils;
import com.zipwhip.zuno.service.NotificationDispatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CallUno implements Command {

    private final GameManager gameManager;
    private final MessageUtils messageUtils;
    private final NotificationDispatcher notificationDispatcher;

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

        Integer index = null;

        if (args.length == 2) {
            try {
                index = Integer.parseInt(args[1]) - 1;
            } catch (NumberFormatException nfe) {
                throw new InvalidCommandException(
                        "Invalid syntax. " + messageUtils.callUnoInstructions());
            }
        }

        Game game = gameManager.uno(source, index);

        if (index != null) {
            Player penalizedPlayer = game.getPlayers().get(index);

            game.getPlayers().forEach(player -> {
                if (player.equals(penalizedPlayer)) {
                    sendYouHaveNotCalledUnoMessage(player);
                } else {
                    sendSomeoneHasNotCalledUnoMessage(player, penalizedPlayer);
                }
            });
        }
    }
    private void sendSomeoneHasNotCalledUnoMessage(Player player, Player penalizedPlayer) {
        notificationDispatcher.notifyPlayer(player, messageUtils.someoneHasNotCalledUnoMessage(penalizedPlayer));
    }

    private void sendYouHaveNotCalledUnoMessage(Player player) {
        notificationDispatcher.notifyPlayer(player, messageUtils.youHaveNotCalledUnoMessage());
    }

}
