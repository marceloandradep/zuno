package com.zipwhip.zuno.game;

import com.zipwhip.zuno.game.deck.Card;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class Player {

    private final String id = UUID.randomUUID().toString();
    private final Integer avatar;
    private final String source;
    private final List<Card> cards = new ArrayList<>();

    private boolean mustCallUno = false;

    public void take(Card card) {
        cards.add(card);
        mustCallUno = false;
    }

    public Card getCardAt(int cardIndex) {
        return cards.get(cardIndex);
    }

    public Card discard(int cardIndex) {
        Card card = cards.remove(cardIndex);
        if (numCards() == 1) {
            mustCallUno = true;
        }
        return card;
    }

    public void uno() {
        mustCallUno = false;
    }

    public int numCards() {
        return cards.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return id.equals(player.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
