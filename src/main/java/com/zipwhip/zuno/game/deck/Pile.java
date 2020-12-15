package com.zipwhip.zuno.game.deck;

import java.util.Collections;
import java.util.Stack;

public class Pile {

    private final Stack<Card> cards = new Stack<>();

    private Pile() {
    }

    public static Pile empty() {
        return new Pile();
    }

    public static Pile newDeck() {
        final Pile pile = empty();
        pile.refill();
        return pile;
    }

    public void refill() {
        cards.addAll(Card.allCards);
        shuffle();
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card pop() {
        if (cards.isEmpty()) {
            refill();
        }
        return cards.pop();
    }

    public void push(Card card) {
        cards.push(card);
    }

    public Card peek() {
        if (!cards.isEmpty()) {
            return cards.peek();
        }

        return null;
    }

}
