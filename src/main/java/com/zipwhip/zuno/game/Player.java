package com.zipwhip.zuno.game;

import com.zipwhip.zuno.game.deck.Card;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode
public class Player {

    private final String id;

    @EqualsAndHashCode.Exclude
    private final Integer avatar;

    @EqualsAndHashCode.Exclude
    private final String source;

    @EqualsAndHashCode.Exclude
    private final List<Card> cards = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    private boolean mustSayUno = false;

    public void take(Card card) {
        cards.add(card);
        mustSayUno = false;
    }

    public Card getCardAt(int cardIndex) {
        return cards.get(cardIndex);
    }

    public Card discard(int cardIndex) {
        Card card = cards.remove(cardIndex);
        if (numCards() == 1) {
            mustSayUno = true;
        }
        return card;
    }

    public void uno() {
        mustSayUno = false;
    }

    public int numCards() {
        return cards.size();
    }

}
