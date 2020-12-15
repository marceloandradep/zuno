package com.zipwhip.zuno.repositories;

import com.zipwhip.zuno.game.Game;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameRepository {

    private final Map<String, Game> gamesById = new ConcurrentHashMap<>();
    private final Map<String, Game> gamesBySource = new ConcurrentHashMap<>();

    public void save(final Game game) {
        synchronized (game) {
            gamesById.put(game.getId(), game);
            game.getPlayers().forEach(player -> gamesBySource.put(player.getSource(), game));
        }
    }

    public Game findById(String id) {
        return gamesById.get(id);
    }

    public Game findBySource(String source) {
        return gamesBySource.get(source);
    }

    public void delete(final Game game) {
        synchronized (game) {
            game.getPlayers().forEach(player -> gamesBySource.remove(player.getSource()));
            gamesById.remove(game.getId());
        }
    }
}
