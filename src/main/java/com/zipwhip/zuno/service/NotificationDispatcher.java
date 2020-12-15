package com.zipwhip.zuno.service;

import com.zipwhip.zuno.game.Game;
import com.zipwhip.zuno.game.Player;
import com.zipwhip.zuno.integrations.ZipwhipClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationDispatcher {

    private final ZipwhipClient zipwhipClient;

    public void notifyPlayer(Player player, String message) {
        zipwhipClient.sendText(player.getSource(), message);
    }

    public void notifySource(String source, String message) {
        zipwhipClient.sendText(source, message);
    }

    public void notifyEveryone(Game game, String message) {
        game.getPlayers()
                .parallelStream()
                .forEach(player -> notifyPlayer(player, message));
    }

    public void notifyEveryoneButPlayer(Game game, Player player, String message) {
        game.getPlayers()
                .parallelStream()
                .forEach(p -> {
                    if (!p.getSource().equals(player.getSource())) {
                        notifyPlayer(p, message);
                    }
                });
    }

}
