package com.zipwhip.zuno.game;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class TurnControllerTest {

    @Test
    public void testFullRotation() {
        TurnController turnController = new TurnController(3, 0);

        int index = turnController.getCurrentIndex();
        Assertions.assertThat(index).isEqualTo(0);

        turnController.endTurn();

        index = turnController.getCurrentIndex();
        Assertions.assertThat(index).isEqualTo(1);

        turnController.endTurn();

        index = turnController.getCurrentIndex();
        Assertions.assertThat(index).isEqualTo(2);

        turnController.endTurn();

        index = turnController.getCurrentIndex();
        Assertions.assertThat(index).isEqualTo(0);
    }

    @Test
    public void testReverseFullRotation() {
        TurnController turnController = new TurnController(3, 0);

        int index = turnController.getCurrentIndex();
        Assertions.assertThat(index).isEqualTo(0);

        turnController.reverse();
        turnController.endTurn();

        index = turnController.getCurrentIndex();
        Assertions.assertThat(index).isEqualTo(2);

        turnController.endTurn();

        index = turnController.getCurrentIndex();
        Assertions.assertThat(index).isEqualTo(1);

        turnController.endTurn();

        index = turnController.getCurrentIndex();
        Assertions.assertThat(index).isEqualTo(0);
    }

    @Test
    public void testSkipNext() {
        TurnController turnController = new TurnController(3, 0);

        int index = turnController.getCurrentIndex();
        Assertions.assertThat(index).isEqualTo(0);

        turnController.skipNext();

        index = turnController.getCurrentIndex();
        Assertions.assertThat(index).isEqualTo(2);
    }

    @Test
    public void testIndexAfter() {
        TurnController turnController = new TurnController(3, 0);

        int index = turnController.indexAfter(0);
        Assertions.assertThat(index).isEqualTo(1);

        index = turnController.indexAfter(2);
        Assertions.assertThat(index).isEqualTo(0);
    }

    @Test
    public void testIndexBefore() {
        TurnController turnController = new TurnController(3, 0);

        int index = turnController.indexBefore(0);
        Assertions.assertThat(index).isEqualTo(2);

        index = turnController.indexBefore(2);
        Assertions.assertThat(index).isEqualTo(1);
    }

}
