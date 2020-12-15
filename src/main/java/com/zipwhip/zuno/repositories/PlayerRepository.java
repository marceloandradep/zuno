package com.zipwhip.zuno.repositories;

import com.zipwhip.zuno.game.Player;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PlayerRepository {

    private final Map<String, Player> playersBySource = new ConcurrentHashMap<>();

    public void save(Player player) {
        playersBySource.put(player.getSource(), player);
    }

    public Player findBySource(String source) {
        return playersBySource.get(source);
    }

    public void delete(Player player) {
        playersBySource.remove(player.getSource());
    }

}
