package com.zipwhip.zuno.game.deck;

import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
@RequiredArgsConstructor
public class Card {

    public static final List<Card> allCards = new ArrayList<>();
    static {
        for (Season season : Season.values()) {
            for (CardValue cardValue : CardValue.values()) {
                allCards.add(new Card(season, cardValue));
            }
        }
    }

    public static final Card[] seasonCards;
    static {
        seasonCards = new Card[Season.values().length];
        int idx = 0;
        for (Season season : Season.values()) {
            seasonCards[idx++] = new Card(season, null);
        }
    }

    Season season;
    CardValue value;

    public boolean isActionCard() {
        return value.ordinal() < CardValue.ZERO.ordinal();
    }

    public boolean canGoOnTopOf(Card another) {
        if (isWildCard()) {
            return true;
        }

        return season.equals(another.season) || (value == null || value.equals(another.value));
    }

    public boolean isWildCard() {
        return CardValue.WILD.equals(value) || CardValue.WILD_DRAW.equals(value);
    }

}
