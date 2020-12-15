package com.zipwhip.zuno.game.commands;

import com.zipwhip.zuno.game.Game;
import com.zipwhip.zuno.game.GameManager;
import com.zipwhip.zuno.game.Player;
import com.zipwhip.zuno.game.text.MessageUtils;
import com.zipwhip.zuno.service.NotificationDispatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KeepCard implements Command {

    private final GameManager gameManager;
    private final MessageUtils messageUtils;
    private final NotificationDispatcher notificationDispatcher;

    @Override
    public String keyword() {
        return "/keep";
    }

    @Override
    public void execute(String source, String[] args) {
        Game game = gameManager.keepCard(source);

        game.getPlayers().forEach(player -> {
            if (player.equals(game.getCurrentPlayer())) {
                sendCurrentPlayerMessage(game, player);
            } else {
                sendOtherPlayersMessage(game, player);
            }
        });
    }

    private void sendOtherPlayersMessage(Game game, Player player) {
        notificationDispatcher.notifyPlayer(player, messageUtils.playerOutOfTurnMessage(game, player));
    }

    private void sendCurrentPlayerMessage(Game game, Player player) {
        notificationDispatcher.notifyPlayer(player, messageUtils.playerTurnMessage(game, player));
    }
}
