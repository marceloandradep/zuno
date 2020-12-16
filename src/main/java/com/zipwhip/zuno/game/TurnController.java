package com.zipwhip.zuno.game;

import lombok.Getter;

public class TurnController {

    @Getter
    private int currentIndex;
    private int direction;
    private final int length;

    public TurnController(final int length, final int startingIndex) {
        this.length = length;
        this.currentIndex = startingIndex;
        this.direction = 1;
    }

    public void reverse() {
        direction *= -1;
    }

    public int indexAfter(final int startingAt) {
        int index = startingAt + direction;

        if (index == length) {
            index = 0;
        } else if (index < 0) {
            index = length - 1;
        }

        return index;
    }

    public int indexBefore(int startingAt) {
        int index = startingAt - direction;

        if (index == length) {
            index = 0;
        } else if (index < 0) {
            index = length - 1;
        }

        return index;
    }

    public int nextIndex() {
        return indexAfter(currentIndex);
    }

    public void skipNext() {
        currentIndex = indexAfter(nextIndex());
    }

    public void endTurn() {
        currentIndex = nextIndex();
    }


}
