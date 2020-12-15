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
public class ChatCommand implements Command {

    private final GameManager gameManager;
    private final MessageUtils messageUtils;
    private final NotificationDispatcher dispatcher;

    @Override
    public String keyword() {
        return null;
    }

    @Override
    public void execute(String source, String...args) {
        if (args.length > 0) {
            Game game = gameManager.findGameBySource(source);
            Player sender = gameManager.findPlayerBySource(source);

            if (game != null) {
                game.getPlayers().forEach(player -> {
                    if (!player.getSource().equals(source)) {
                        dispatcher.notifySource(player.getSource(), messageUtils.chatMessage(sender, args[0]));
                    }
                });
            } else {
                dispatcher.notifySource(source, messageUtils.createGameInstructions());
            }
        }
    }
}
