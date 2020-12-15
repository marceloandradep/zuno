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
public class JoinGame implements Command {

    private final GameManager gameManager;
    private final MessageUtils messageUtils;
    private final NotificationDispatcher notificationDispatcher;

    @Override
    public String keyword() {
        return "/join";
    }

    @Override
    public void execute(String source, String[] args) {
        if (args.length != 2) {
            throw new InvalidCommandException(messageUtils.joinGameInstructions());
        }

        String gameId = args[1];

        Game game = gameManager.joinGame(gameId, source);
        Player player = gameManager.findPlayerBySource(source);

        notificationDispatcher.notifyEveryoneButPlayer(game, player, messageUtils.aPlayerHasJoinedMessage(player));
        notificationDispatcher.notifyPlayer(player, messageUtils.youHaveJoinedMessage(player));
    }
}
