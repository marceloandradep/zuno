package com.zipwhip.zuno.game;

import com.zipwhip.zuno.exceptions.*;
import com.zipwhip.zuno.game.deck.Card;
import com.zipwhip.zuno.game.deck.CardValue;
import com.zipwhip.zuno.game.deck.Pile;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Game {

    private static final Random random = new Random();

    private final List<Integer> randomizedAvatarList =
            IntStream
                    .range(0, GameConstants.MAX_PLAYERS)
                    .boxed()
                    .collect(Collectors.toList());
    {
        Collections.shuffle(randomizedAvatarList);
    }

    @Getter
    private final String id;

    @Getter
    private final Player owner;

    @Getter
    private final List<Player> players = new ArrayList<>();

    @Getter
    private Player winner = null;

    private GameState state = GameState.WAITING;

    private int currentPlayerIndex = -1;
    private int orientation = 0;

    @Getter
    private Pile drawPile;

    @Getter
    private Pile discardPile;

    public Game(String id, String source) {
        this.id = id;
        this.owner = newPlayer(source);
    }

    public Player newPlayer(String source) {
        if (hasPlayerJoined(source)) {
            throw new PlayerAlreadyJoinedException();
        }

        if (isFull()) {
            throw new FullGameException();
        }

        final Player player =
                new Player(UUID.randomUUID().toString(), nextRandomAvatar(), source);

        players.add(player);

        return player;
    }

    public void start() {
        if (players.size() < 2) {
            throw new NotEnoughPlayersException();
        }

        if (isWaiting()) {
            state = GameState.PLAYING;
            initializeGame();
        } else {
            throw new IllegalStateException("Game started already or has finished.");
        }
    }

    public void draw() {
        if (isPlaying()) {
            state = GameState.DRAWING;
            getCurrentPlayer().take(drawPile.pop());
        } else {
            throw new IllegalStateException("You can only draw once.");
        }
    }

    public void keep() {
        if (isDrawing()) {
            state = GameState.PLAYING;
            endTurn();
        } else {
            throw new IllegalStateException("You have to draw first.");
        }
    }

    public void playCard(int cardIndex) {
        playCard(cardIndex, false);
    }

    public void playCard(int cardIndex, boolean uno) {
        Player player = getCurrentPlayer();

        Card playerCard = player.getCardAt(cardIndex);
        Card cardAtDiscard = discardPile.peek();

        if (playerCard.canGoOnTopOf(cardAtDiscard)) {
            if (isDrawing()) {
                state = GameState.PLAYING;
            }

            discardPile.push(player.discard(cardIndex));

            if (uno) {
                player.uno();
            }

            if (player.numCards() == 0) {
                finishGame();
                return;
            }

            if (playerCard.isActionCard()) {
                switch (playerCard.getValue()) {
                    case WILD:
                        waitForSeason();
                        break;
                    case WILD_DRAW:
                        wildDraw();
                        break;
                    case REVERSE:
                        reverse();
                        break;
                    case SKIP:
                        skip();
                        break;
                    case DRAW_TWO:
                        drawTwo();
                        break;
                }
            } else {
                endTurn();
            }
        } else {
            throw new CardDoesNotMatchException();
        }
    }

    public void pickSeason(int seasonIndex) {
        if (!GameState.PICKING_SEASON.equals(state)) {
            throw new IllegalStateException("Invalid play.");
        }

        boolean skipNextPlayer = false;
        if (discardPile.peek().getValue().equals(CardValue.WILD_DRAW)) {
            skipNextPlayer = true;
        }

        Card seasonCard = Card.seasonCards[seasonIndex];
        discardPile.push(seasonCard);
        endTurn();

        if (skipNextPlayer) {
            endTurn();
        }

        state = GameState.PLAYING;
    }

    public void uno(Player self) {
        self.uno();
    }

    public void uno(int index) {
        if (index < 0 || index >= players.size()) {
            throw new IllegalArgumentException("Invalid player number.");
        }

        Player player = players.get(index);
        if (player.isMustSayUno()) {
            playerDraw(player, 2);
            player.uno();
        } else {
            throw new InvalidUnoCallException();
        }
    }

    public void waitForSeason() {
        state = GameState.PICKING_SEASON;
    }

    public void wildDraw() {
        nextPlayerDraw(4);
        waitForSeason();
    }

    public void drawTwo() {
        nextPlayerDraw(2);
        skip();
    }

    public void reverse() {
        orientation *= -1;
        endTurn();
    }

    public void skip() {
        endTurn();
        endTurn();
    }

    public void finishGame() {
        winner = getCurrentPlayer();
        state = GameState.FINISHED;
    }

    public void nextPlayerDraw(int numCards) {
        playerDraw(nextPlayer(), numCards);
    }

    public void playerDraw(Player player, int numCards) {
        for (int i = 0; i < numCards; i++) {
            player.take(drawPile.pop());
        }
    }

    public int nextPlayerIndex() {
        return indexAfter(currentPlayerIndex);
    }

    public int indexAfter(int startingAt) {
        int index = startingAt + orientation;

        if (index == players.size()) {
            index = 0;
        } else if (index < 0) {
            index = players.size() - 1;
        }

        return index;
    }

    public int indexBefore(int startingAt) {
        int index = startingAt - orientation;

        if (index == players.size()) {
            index = 0;
        } else if (index < 0) {
            index = players.size() - 1;
        }

        return index;
    }

    public Player nextPlayer() {
        return players.get(nextPlayerIndex());
    }

    public Player playerAfter(Player player) {
        int startingIndex = getPlayers().indexOf(player);
        return players.get(indexAfter(startingIndex));
    }

    public Player playerBefore(Player player) {
        int startingIndex = getPlayers().indexOf(player);
        return players.get(indexBefore(startingIndex));
    }

    public void endTurn() {
        currentPlayerIndex = nextPlayerIndex();
    }

    public List<Player> getTurnSequence() {
        List<Player> turnSequence = new ArrayList<>();

        int index = currentPlayerIndex;

        for (int i = 0; i < players.size(); i++) {
            turnSequence.add(players.get(index));
            index = indexAfter(index);
        }

        return turnSequence;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public boolean isFull() {
        return players.size() == GameConstants.MAX_PLAYERS;
    }

    public boolean hasPlayerJoined(String source) {
        for (Player player : players) {
            if (player.getSource().equals(source)) {
                return true;
            }
        }

        return false;
    }

    public boolean isWaiting() {
        return GameState.WAITING.equals(state);
    }

    public boolean isPlaying() {
        return GameState.PLAYING.equals(state);
    }

    public boolean isDrawing() {
        return GameState.DRAWING.equals(state);
    }

    public boolean isPickingSeason() {
        return GameState.PICKING_SEASON.equals(state);
    }

    public boolean hasFinished() {
        return GameState.FINISHED.equals(state);
    }

    private void initializeGame() {
        currentPlayerIndex = random.nextInt(players.size());
        orientation = 1;

        drawPile = Pile.newDeck();
        discardPile = Pile.empty();

        players.forEach(player -> {
            for (int i = 0; i < GameConstants.INITIAL_CARDS_PER_PLAYER; i++) {
                player.take(drawPile.pop());
            }
        });

        discardPile.push(drawPile.pop());
    }

    private Integer nextRandomAvatar() {
        return randomizedAvatarList.get(players.size());
    }

}
