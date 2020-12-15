package com.zipwhip.zuno.game.text;

import com.zipwhip.zuno.game.Game;
import com.zipwhip.zuno.game.GameRenderer;
import com.zipwhip.zuno.game.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageUtils {

    private final GameRenderer gameRenderer;

    public String createGameInstructions() {
        return "Text '/create' to create a new game.";
    }

    public String joinGameInstructions() {
        return "Invalid syntax. Text '/join {game_id}' to join an existing game or '/create' to create a new one.";
    }

    public String playInstructions() {
        return "Text '/play {card_number}' to play a card or '/play {card_number} uno' to play and call uno. " +
                "Text '/draw' to draw a card from the pile.";
    }

    public String playOrKeepInstructions() {
        return "Text '/play {card_number}' to play or '/keep' to keep the card and end your turn.";
    }

    public String pickSeasonInstructions() {
        return String.format(
                "Pick a season %s.\n\nText '/play {season_number}' to play.",
                gameRenderer.renderSeasons());
    }

    public String help() {
        return String.format(
                "- /play {card_number}: play a card.\n\n" +
                        "- /play {card_number} uno: play a card and call uno when you have only 1 card left.\n\n" +
                        "- /draw: draw a card from the pile.\n\n" +
                        "- /uno: call uno if you haven't yet.\n\n" +
                        "- /uno {player_number}: announce a player hasn't called uno.\n\n" +
                        "- /quit: close the game.\n\n" +
                        "- /help: see these instructions.\n\n" +
                        "%s", gameRenderer.renderActionCards());
    }

    public String callUnoInstructions() {
        return "Text '/uno' to call uno for yourself or '/uno {player_number}' to announce that a player hasn't called uno.";
    }

    public String chatMessage(Player player, String message) {
        return String.format("%s says: %s", gameRenderer.renderPlayerAvatar(player), message);
    }

    public String gameCreatedMessage(Game game) {
        return String.format(
                "A game was created successfully.\n\nYour avatar is %s.\n\n" +
                        "Ask your friends to text '/join %s'. " +
                        "When ready text '/start' or '/help for instructions'.",
                gameRenderer.renderPlayerAvatar(game.getOwner()), game.getId());
    }

    public String youHaveJoinedMessage(Player player) {
        return String.format(
                "You've joined the game. Your avatar is %s. Waiting the game to start.\n\n%s",
                gameRenderer.renderPlayerAvatar(player),
                help());
    }

    public String aPlayerHasJoinedMessage(Player player) {
        return String.format(
                "Player %s joined the game.",
                gameRenderer.renderPlayerAvatar(player));
    }

    public String playerTurnMessage(Game game, Player player) {
        return String.format(
                "It's YOUR turn.\n\n%s\n\n%s",
                allPlayers(game, player),
                cardsInfo(game, player));
    }

    public String playerOutOfTurnMessage(Game game, Player player) {
        return String.format(
                "It's %s's turn.\n\n%s\n\n%s",
                gameRenderer.renderPlayerAvatar(game.getCurrentPlayer()),
                allPlayers(game, player),
                cardsInfo(game, player));
    }

    public String playerHasDrewCardMessage(Game game) {
        return String.format(
                "%s has drew a card.",
                gameRenderer.renderPlayerAvatar(game.getCurrentPlayer()));
    }

    public String youHaveDrewCardMessage(Game game, Player player) {
        return String.format(
                "It's YOUR turn.\n\n%s\n\n%s\n\n%s",
                allPlayers(game, player),
                cardsInfo(game, player),
                playOrKeepInstructions());
    }

    public String gameClosed(String source) {
        return String.format("The game was closed by %s", source);
    }

    public String cardsInfo(Game game, Player player) {
        return String.format(
                "[ %s ]\n\nYour cards: %s.",
                gameRenderer.renderCard(game.getDiscardPile().peek()),
                gameRenderer.renderPlayerCards(player));
    }

    public String allPlayers(Game game, Player recipient) {
        return gameRenderer.renderPlayers(game, recipient);
    }

    public String loser(Game game) {
        return String.format("Player %s won the game!", gameRenderer.renderPlayerAvatar(game.getWinner()));
    }

    public String winner() {
        return "Congratulations! You won the game!";
    }

}
