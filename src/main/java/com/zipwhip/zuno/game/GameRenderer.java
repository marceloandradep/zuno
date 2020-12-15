package com.zipwhip.zuno.game;

import com.zipwhip.zuno.game.deck.Card;
import com.zipwhip.zuno.game.text.Emojis;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GameRenderer {

    private static final String[] players = {
            Emojis.monkey, Emojis.dog, Emojis.fox, Emojis.cat, Emojis.lion,
            Emojis.tiger, Emojis.horse, Emojis.cow, Emojis.pig, Emojis.mouse
    };

    private static final String[] seasons = {
            Emojis.winter, Emojis.spring, Emojis.summer, Emojis.autumn
    };

    private static final String[] cards = {
            Emojis.skip, Emojis.drawTwo, Emojis.reverse, Emojis.wild, Emojis.wildDraw,
            Emojis.zero, Emojis.one, Emojis.two, Emojis.three, Emojis.four, Emojis.five,
            Emojis.six, Emojis.seven, Emojis.eight, Emojis.nine
    };

    public String renderActionCards() {
        return String.format("[%s skip, %s draw two, %s reverse, %s wild, %s wild draw]",
                Emojis.skip, Emojis.drawTwo, Emojis.reverse, Emojis.wild, Emojis.wildDraw);
    }

    public String renderPlayer(final Player player) {
        return renderPlayer(player, false);
    }

    public String renderPlayer(final Player player, boolean showNumOfCards) {
        return players[player.getAvatar()] +
                (showNumOfCards ? "  (" + player.numCards() + ")" : "");
    }

    public String renderPlayers(final Game game) {
        final List<Player> players = game.getTurnSequence();
        final StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < players.size(); i++) {
            if (i > 0) {
                stringBuilder.append("    ");
            }
            stringBuilder
                    .append(players.get(i).getAvatar())
                    .append(". ")
                    .append(renderPlayer(players.get(i), true));
        }

        return stringBuilder.toString();
    }

    public String renderCard(final Card card) {
        return (card.getValue() != null ? cards[card.getValue().ordinal()] : "") +
                seasons[card.getSeason().ordinal()];
    }

    public String renderPlayerCards(final Player player) {
        StringBuilder stringBuilder = new StringBuilder();

        List<Card> cards = player.getCards();

        for (int i = 0; i < cards.size(); i++) {
            if (i > 0) {
                stringBuilder.append("  ");
            }

            stringBuilder
                    .append(i + 1)
                    .append(". ")
                    .append(renderCard(cards.get(i)));
        }

        return stringBuilder.toString();
    }

    public String renderSeasons() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < Card.seasonCards.length; i++) {
            if (i > 0) {
                stringBuilder.append("  ");
            }

            stringBuilder
                    .append(i + 1)
                    .append(". ")
                    .append(renderCard(Card.seasonCards[i]));
        }

        return stringBuilder.toString();
    }
}
