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
public class PlayCard implements Command {

    private final GameManager gameManager;
    private final MessageUtils messageUtils;
    private final NotificationDispatcher notificationDispatcher;

    @Override
    public String keyword() {
        return "/play";
    }

    @Override
    public void execute(String source, String[] args) {
        if (args.length < 2 || args.length > 3) {
            throw new InvalidCommandException(
                    "Invalid syntax. " + messageUtils.playInstructions());
        }

        int cardNumber;
        try {
            cardNumber = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
            throw new InvalidCommandException(
                    "Invalid syntax. " + messageUtils.playInstructions());
        }

        boolean uno =
                args.length == 3 && args[2].equalsIgnoreCase("uno");

        Game game = gameManager.playCard(source, cardNumber - 1, uno);

        if (game.hasFinished()) {
            sendGameFinishedMessage(game);
            gameManager.quit(source);
        } else if (game.isPickingSeason()) {
            sendSeasonChoicesMessage(game);
        } else {
            game.getPlayers().forEach(player -> {
                if (player.equals(game.getCurrentPlayer())) {
                    sendCurrentPlayerMessage(game, player);
                } else {
                    sendOtherPlayersMessage(game, player);
                }
            });
        }
    }

    private void sendGameFinishedMessage(Game game) {
        notificationDispatcher.notifyPlayer(game.getWinner(), messageUtils.winner());
        notificationDispatcher.notifyEveryoneButPlayer(game, game.getWinner(), messageUtils.loser(game));
    }

    private void sendSeasonChoicesMessage(Game game) {
        notificationDispatcher.notifyPlayer(game.getCurrentPlayer(), messageUtils.pickSeasonInstructions());
    }

    private void sendOtherPlayersMessage(Game game, Player player) {
        notificationDispatcher.notifyPlayer(player, messageUtils.playerOutOfTurnMessage(game, player));
    }

    private void sendCurrentPlayerMessage(Game game, Player player) {
        notificationDispatcher.notifyPlayer(player, messageUtils.playerTurnMessage(game, player));
    }
}
