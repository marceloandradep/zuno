package com.zipwhip.zuno.game;

import com.zipwhip.zuno.exceptions.FullGameException;
import com.zipwhip.zuno.exceptions.InvalidUnoCallException;
import com.zipwhip.zuno.exceptions.NotEnoughPlayersException;
import com.zipwhip.zuno.exceptions.PlayerAlreadyJoinedException;
import com.zipwhip.zuno.game.deck.Card;
import com.zipwhip.zuno.game.deck.CardValue;
import com.zipwhip.zuno.game.deck.Season;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class GameTest {

    @Test
    public void testAddNewPlayer() {
        // given
        Game game = new Game("id", "player-1");

        // when
        Player player = game.newPlayer("player-2");

        // then
        assertThat(player.getSource()).isEqualTo("player-2");
        assertThat(game.getPlayers().size()).isEqualTo(2);
    }

    @Test
    public void testAddSamePlayerTwice() {
        // given
        Game game = new Game("id", "player-1");

        // when
        game.newPlayer("player-2");

        // then
        assertThatExceptionOfType(PlayerAlreadyJoinedException.class)
                .isThrownBy(() -> game.newPlayer("player-2"));
    }

    @Test
    public void testFullGame() {
        // given
        Game game = new Game("id", "player-1");

        // when
        for (int i = 1; i < GameConstants.MAX_PLAYERS; i++) {
            game.newPlayer("player-" + (i + 1));
        }

        // then
        assertThatExceptionOfType(FullGameException.class)
                .isThrownBy(() -> game.newPlayer("player-11"));
    }

    @Test
    public void testStartGame() {
        // given
        Game game = createDefaultNewGame();

        // when
        game.start();

        // then
        assertThat(game.isPlaying()).isTrue();
        assertThat(game.getCurrentPlayer()).isNotNull();
        assertThat(game.getTurnSequence().size()).isEqualTo(game.getPlayers().size());
        assertThat(game.getDiscardPile().peek()).isNotNull();

        for (Player player : game.getPlayers()) {
            assertThat(player.getCards().size()).isEqualTo(GameConstants.INITIAL_CARDS_PER_PLAYER);
        }
    }

    @Test
    public void testStartGameWithoutEnoughPlayers() {
        // given
        Game game = new Game("id", "player-1");

        // then
        assertThatExceptionOfType(NotEnoughPlayersException.class)
                .isThrownBy(game::start);
    }

    @Test
    public void testStartGameTwice() {
        // given
        Game game = createDefaultNewGame();

        // when
        game.start();

        // then
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(game::start);
    }

    @Test
    public void testDrawCard() {
        // given
        Game game = createDefaultStartedGame();
        Player expectedCurrentPlayer = game.getCurrentPlayer();
        int expectedCards = expectedCurrentPlayer.numCards() + 1;

        // when
        game.draw();

        // then
        assertThat(game.getCurrentPlayer()).isEqualTo(expectedCurrentPlayer);
        assertThat(game.getCurrentPlayer().numCards()).isEqualTo(expectedCards);
        assertThat(game.isDrawing()).isTrue();
    }

    @Test
    public void testDrawCardTwice() {
        // given
        Game game = createDefaultStartedGame();
        Player expectedCurrentPlayer = game.getCurrentPlayer();
        int expectedCards = expectedCurrentPlayer.numCards() + 1;

        // when
        game.draw();

        // then
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(game::draw);

        assertThat(game.getCurrentPlayer()).isEqualTo(expectedCurrentPlayer);
        assertThat(game.getCurrentPlayer().numCards()).isEqualTo(expectedCards);
        assertThat(game.isDrawing()).isTrue();
    }

    @Test
    public void testKeepWithoutDrawing() {
        // given
        Game game = createDefaultStartedGame();
        Player expectedCurrentPlayer = game.getCurrentPlayer();
        int expectedCards = expectedCurrentPlayer.numCards();

        // then
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(game::keep);

        assertThat(game.getCurrentPlayer()).isEqualTo(expectedCurrentPlayer);
        assertThat(game.getCurrentPlayer().numCards()).isEqualTo(expectedCards);
        assertThat(game.isPlaying()).isTrue();
    }

    @Test
    public void testDrawAndKeepCard() {
        // given
        Game game = createDefaultStartedGame();
        Player player = game.getCurrentPlayer();
        Player expectedNextPlayer = game.nextPlayer();
        int expectedCards = expectedNextPlayer.numCards() + 1;

        // when
        game.draw();
        game.keep();

        // then
        assertThat(game.getCurrentPlayer()).isEqualTo(expectedNextPlayer);
        assertThat(player.numCards()).isEqualTo(expectedCards);
        assertThat(game.isPlaying()).isTrue();
    }

    @Test
    public void testDrawAndPlayCard() {
        // given
        Game game = createDefaultStartedGame();
        Player player = game.getCurrentPlayer();

        Card topCard = new Card(Season.SPRING, CardValue.ZERO);
        Card drawCard = new Card(Season.SPRING, CardValue.EIGHT);

        game.getDiscardPile().push(topCard);
        game.getDrawPile().push(drawCard);

        Player expectedNextPlayer = game.nextPlayer();
        int expectedCards = expectedNextPlayer.numCards();

        // when
        game.draw();
        game.playCard(player.getCards().size() - 1);

        // then
        assertThat(game.getCurrentPlayer()).isEqualTo(expectedNextPlayer);
        assertThat(player.numCards()).isEqualTo(expectedCards);
        assertThat(game.isPlaying()).isTrue();
    }

    @Test
    public void testPlayOrdinaryCard() {
        // given
        Game game = createDefaultStartedGame();
        Player player = game.getCurrentPlayer();

        Card topCard = new Card(Season.SPRING, CardValue.ZERO);
        Card playerCard = new Card(Season.SPRING, CardValue.EIGHT);

        game.getDiscardPile().push(topCard);
        player.getCards().add(0, playerCard);

        int expectedCards = player.numCards() - 1;
        Player expectedNextPlayer = game.nextPlayer();

        // when
        game.playCard(0);

        // then
        assertThat(player.numCards()).isEqualTo(expectedCards);
        assertThat(game.getCurrentPlayer()).isEqualTo(expectedNextPlayer);
        assertThat(game.getDiscardPile().peek()).isEqualTo(playerCard);
    }

    @Test
    public void testPlayWildCard() {
        // given
        Game game = createDefaultStartedGame();
        Player player = game.getCurrentPlayer();

        Card topCard = new Card(Season.SPRING, CardValue.ZERO);
        Card playerCard = new Card(Season.SPRING, CardValue.WILD);

        game.getDiscardPile().push(topCard);
        player.getCards().add(0, playerCard);

        Player expectedNextPlayer = game.nextPlayer();
        int expectedCards = player.numCards() - 1;

        // when
        game.playCard(0);
        assertThat(game.isPickingSeason());

        game.pickSeason(0);

        // then
        assertThat(player.numCards()).isEqualTo(expectedCards);
        assertThat(game.getCurrentPlayer()).isEqualTo(expectedNextPlayer);
        assertThat(game.getDiscardPile().peek()).isEqualTo(Card.seasonCards[0]);
        assertThat(game.isPlaying()).isTrue();
    }

    @Test
    public void testPlayWildDrawCard() {
        // given
        Game game = createDefaultStartedGame();
        Player player = game.getCurrentPlayer();
        Player attackedPlayer = game.nextPlayer();
        Player expectedNextPlayer = game.playerAfter(attackedPlayer);

        Card topCard = new Card(Season.SPRING, CardValue.ZERO);
        Card playerCard = new Card(Season.SPRING, CardValue.WILD_DRAW);

        game.getDiscardPile().push(topCard);
        player.getCards().add(0, playerCard);

        int expectedCards = player.numCards() - 1;
        int attackedPlayerExpectedCards = attackedPlayer.numCards() + 4;

        // when
        game.playCard(0);
        assertThat(game.isPickingSeason());

        game.pickSeason(0);

        // then
        assertThat(player.numCards()).isEqualTo(expectedCards);
        assertThat(attackedPlayer.numCards()).isEqualTo(attackedPlayerExpectedCards);
        assertThat(game.getCurrentPlayer()).isEqualTo(expectedNextPlayer);
        assertThat(game.getDiscardPile().peek()).isEqualTo(Card.seasonCards[0]);
        assertThat(game.isPlaying()).isTrue();
    }

    @Test
    public void testPlayReverseCard() {
        // given
        Game game = createDefaultStartedGame();
        Player player = game.getCurrentPlayer();
        Player expectedNextPlayer = game.playerBefore(player);

        Card topCard = new Card(Season.SPRING, CardValue.ZERO);
        Card playerCard = new Card(Season.SPRING, CardValue.REVERSE);

        game.getDiscardPile().push(topCard);
        player.getCards().add(0, playerCard);

        int expectedCards = player.numCards() - 1;

        // when
        game.playCard(0);

        // then
        assertThat(player.numCards()).isEqualTo(expectedCards);
        assertThat(game.getCurrentPlayer()).isEqualTo(expectedNextPlayer);
        assertThat(game.getDiscardPile().peek()).isEqualTo(playerCard);
        assertThat(game.isPlaying()).isTrue();
    }

    @Test
    public void testPlaySkipCard() {
        // given
        Game game = createDefaultStartedGame();
        Player player = game.getCurrentPlayer();
        Player attackedPlayer = game.nextPlayer();
        Player expectedNextPlayer = game.playerAfter(attackedPlayer);

        Card topCard = new Card(Season.SPRING, CardValue.ZERO);
        Card playerCard = new Card(Season.SPRING, CardValue.SKIP);

        game.getDiscardPile().push(topCard);
        player.getCards().add(0, playerCard);

        int expectedCards = player.numCards() - 1;

        // when
        game.playCard(0);

        // then
        assertThat(player.numCards()).isEqualTo(expectedCards);
        assertThat(game.getCurrentPlayer()).isEqualTo(expectedNextPlayer);
        assertThat(game.getDiscardPile().peek()).isEqualTo(playerCard);
        assertThat(game.isPlaying()).isTrue();
    }

    @Test
    public void testPlayDrawTwoCard() {
        // given
        Game game = createDefaultStartedGame();
        Player player = game.getCurrentPlayer();
        Player attackedPlayer = game.nextPlayer();
        Player expectedNextPlayer = game.playerAfter(attackedPlayer);

        Card topCard = new Card(Season.SPRING, CardValue.ZERO);
        Card playerCard = new Card(Season.SPRING, CardValue.DRAW_TWO);

        game.getDiscardPile().push(topCard);
        player.getCards().add(0, playerCard);

        int expectedCards = player.numCards() - 1;
        int attackedPlayerExpectedCards = attackedPlayer.numCards() + 2;

        // when
        game.playCard(0);

        // then
        assertThat(player.numCards()).isEqualTo(expectedCards);
        assertThat(attackedPlayer.numCards()).isEqualTo(attackedPlayerExpectedCards);
        assertThat(game.getCurrentPlayer()).isEqualTo(expectedNextPlayer);
        assertThat(game.getDiscardPile().peek()).isEqualTo(playerCard);
        assertThat(game.isPlaying()).isTrue();
    }

    @Test
    public void testGameFinish() {
        // given
        Game game = createDefaultStartedGame();
        Player player = game.getCurrentPlayer();

        while (player.numCards() > 0) {
            player.discard(0);
        }

        Card topCard = new Card(Season.SPRING, CardValue.ZERO);
        Card playerCard = new Card(Season.SPRING, CardValue.DRAW_TWO);

        game.getDiscardPile().push(topCard);
        player.getCards().add(0, playerCard);

        int expectedCards = 0;

        // when
        game.playCard(0);

        // then
        assertThat(player.numCards()).isEqualTo(expectedCards);
        assertThat(game.getCurrentPlayer()).isEqualTo(player);
        assertThat(game.getDiscardPile().peek()).isEqualTo(playerCard);
        assertThat(game.hasFinished()).isTrue();
        assertThat(game.getWinner()).isEqualTo(player);
    }

    @Test
    public void testPlayerSayUno() {
        // given
        Game game = createDefaultStartedGame();
        Player player = game.getCurrentPlayer();

        while (!player.isMustSayUno()) {
            player.discard(0);
        }

        int expectedCards = 3;

        // when
        game.uno(game.getPlayers().indexOf(player));

        // then
        assertThat(player.numCards()).isEqualTo(expectedCards);
        assertThat(game.getCurrentPlayer()).isEqualTo(player);
        assertThat(player.isMustSayUno()).isFalse();
    }

    @Test
    public void testPlayerSayInvalidUno() {
        // given
        Game game = createDefaultStartedGame();
        Player player = game.getCurrentPlayer();

        int expectedCards = player.numCards();

        // then
        assertThatExceptionOfType(InvalidUnoCallException.class)
                .isThrownBy(() -> game.uno(game.getPlayers().indexOf(player)));

        // then
        assertThat(player.numCards()).isEqualTo(expectedCards);
        assertThat(game.getCurrentPlayer()).isEqualTo(player);
        assertThat(player.isMustSayUno()).isFalse();
    }

    @Test
    public void testSelfSayUno() {
        // given
        Game game = createDefaultStartedGame();
        Player player = game.getCurrentPlayer();

        while (!player.isMustSayUno()) {
            player.discard(0);
        }

        int expectedCards = 1;

        // when
        game.uno(player);

        // then
        assertThat(player.numCards()).isEqualTo(expectedCards);
        assertThat(game.getCurrentPlayer()).isEqualTo(player);
        assertThat(player.isMustSayUno()).isFalse();
    }

    @Test
    public void testPlayerLeaveUnoState() {
        // given
        Game game = createDefaultStartedGame();
        Player player = game.getCurrentPlayer();

        while (!player.isMustSayUno()) {
            player.discard(0);
        }

        player.take(new Card(Season.SPRING, CardValue.EIGHT));

        // then
        assertThat(player.isMustSayUno()).isFalse();
    }

    private Game createDefaultNewGame() {
        Game game = new Game("id", "player-1");
        game.newPlayer("player-2");
        game.newPlayer("player-3");
        game.newPlayer("player-4");
        return game;
    }

    private Game createDefaultStartedGame() {
        Game game = new Game("id", "player-1");
        game.newPlayer("player-2");
        game.newPlayer("player-3");
        game.newPlayer("player-4");

        game.start();
        return game;
    }

}
