package com.zipwhip.zuno.game.commands;

import com.zipwhip.zuno.game.Game;
import com.zipwhip.zuno.game.GameManager;
import com.zipwhip.zuno.game.text.MessageUtils;
import com.zipwhip.zuno.service.NotificationDispatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuitGame implements Command {

    private final GameManager gameManager;
    private final MessageUtils messageUtils;
    private final NotificationDispatcher notificationDispatcher;

    @Override
    public String keyword() {
        return "/quit";
    }

    @Override
    public void execute(String source, String[] args) {
        Game game = gameManager.findGameBySource(source);
        notificationDispatcher.notifyEveryone(game, messageUtils.gameClosed(source));
        gameManager.quit(source);
    }
}
