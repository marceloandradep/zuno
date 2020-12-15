package com.zipwhip.zuno.game;

import com.zipwhip.zuno.exceptions.GameNotFoundException;
import com.zipwhip.zuno.exceptions.NotPlayerTurnException;
import com.zipwhip.zuno.exceptions.PlayerAlreadyJoinedException;
import com.zipwhip.zuno.exceptions.PlayerNotOwnerException;
import com.zipwhip.zuno.repositories.GameRepository;
import com.zipwhip.zuno.repositories.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GameManager {

    private final IdGenerator idGenerator;
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;

    public Game createGame(final String source) {
        final Game game = new Game(idGenerator.generate(), source);

        playerRepository.save(game.getOwner());
        gameRepository.save(game);

        return game;
    }

    public Game joinGame(final String gameId, final String source) {
        final Game game = findGameById(gameId);

        if (game == null) {
            throw new GameNotFoundException();
        }

        if (!game.isWaiting()) {
            throw new IllegalStateException("Game started already.");
        }

        Game anotherGame = gameRepository.findBySource(source);
        if (anotherGame != null) {
            throw new PlayerAlreadyJoinedException();
        }

        synchronized (this) {
            playerRepository.save(game.newPlayer(source));
            gameRepository.save(game);
        }

        return game;
    }

    public Game startGame(final String source) {
        final Game game = findGameBySource(source);

        if (game == null) {
            throw new GameNotFoundException();
        }

        if (!game.getOwner().getSource().equals(source)) {
            throw new PlayerNotOwnerException();
        }

        game.start();
        return game;
    }

    public Game playCard(final String source, final int cardIndex, boolean uno) {
        Game game = findGameBySource(source);

        if (!game.getCurrentPlayer().getSource().equals(source)) {
            throw new NotPlayerTurnException();
        }

        if (game.isPickingSeason()) {
            game.pickSeason(cardIndex);
        } else {
            game.playCard(cardIndex, uno);
        }

        return game;
    }

    public Game drawCard(final String source) {
        Game game = findGameBySource(source);

        if (!game.getCurrentPlayer().getSource().equals(source)) {
            throw new NotPlayerTurnException();
        }

        game.draw();
        return game;
    }

    public Game keepCard(final String source) {
        Game game = findGameBySource(source);

        if (!game.getCurrentPlayer().getSource().equals(source)) {
            throw new NotPlayerTurnException();
        }

        game.keep();
        return game;
    }

    public Game uno(final String source, final Integer index) {
        Game game = findGameBySource(source);

        synchronized (this) {
            if (index == null) {
                Player player = findPlayerBySource(source);
                game.uno(player);
            } else {
                game.uno(index);
            }
        }

        return game;
    }

    public void quit(final String source) {
        final Game game = findGameBySource(source);

        if (game == null) {
            throw new GameNotFoundException();
        }

        gameRepository.delete(game);
        game.getPlayers().forEach(playerRepository::delete);
    }

    public Game findGameBySource(final String source) {
        return gameRepository.findBySource(source);
    }

    public Game findGameById(final String id) {
        return gameRepository.findById(id);
    }

    public Player findPlayerBySource(String source) {
        return playerRepository.findBySource(source);
    }

}
