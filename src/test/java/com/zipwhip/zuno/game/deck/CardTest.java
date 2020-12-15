package com.zipwhip.zuno.game.deck;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CardTest {

    @Test
    public void testSmileyMatch() {
        // given
        Card bottom = new Card(Season.SPRING, CardValue.ZERO);
        Card top = new Card(Season.AUTUMN, CardValue.ZERO);

        // when
        boolean matches = top.canGoOnTopOf(bottom);

        // then
        assertThat(matches).isTrue();
    }

    @Test
    public void testActionCardMatch() {
        // given
        Card bottom = new Card(Season.SPRING, CardValue.SKIP);
        Card top = new Card(Season.AUTUMN, CardValue.SKIP);

        // when
        boolean matches = top.canGoOnTopOf(bottom);

        // then
        assertThat(matches).isTrue();
    }

    @Test
    public void testSeasonMatch() {
        // given
        Card bottom = new Card(Season.SPRING, CardValue.ZERO);
        Card top = new Card(Season.SPRING, CardValue.ONE);

        // when
        boolean matches = top.canGoOnTopOf(bottom);

        // then
        assertThat(matches).isTrue();
    }

    @Test
    public void testActionCardSeasonMatch() {
        // given
        Card bottom = new Card(Season.SPRING, CardValue.SKIP);
        Card top = new Card(Season.SPRING, CardValue.DRAW_TWO);

        // when
        boolean matches = top.canGoOnTopOf(bottom);

        // then
        assertThat(matches).isTrue();
    }

    @Test
    public void testNoValueSeasonMatch() {
        // given
        Card bottom = new Card(Season.SPRING, null);
        Card top = new Card(Season.SPRING, CardValue.ONE);

        // when
        boolean matches = top.canGoOnTopOf(bottom);

        // then
        assertThat(matches).isTrue();
    }

    @Test
    public void testWildMatch() {
        // given
        Card bottom = new Card(Season.SPRING, CardValue.ZERO);
        Card top = new Card(Season.AUTUMN, CardValue.WILD);

        // when
        boolean matches = top.canGoOnTopOf(bottom);

        // then
        assertThat(matches).isTrue();
    }

    @Test
    public void testWildDrawMatch() {
        // given
        Card bottom = new Card(Season.SPRING, CardValue.ZERO);
        Card top = new Card(Season.AUTUMN, CardValue.WILD_DRAW);

        // when
        boolean matches = top.canGoOnTopOf(bottom);

        // then
        assertThat(matches).isTrue();
    }

    @Test
    public void testMismatch() {
        // given
        Card bottom = new Card(Season.SPRING, CardValue.ZERO);
        Card top = new Card(Season.AUTUMN, CardValue.THREE);

        // when
        boolean matches = top.canGoOnTopOf(bottom);

        // then
        assertThat(matches).isFalse();
    }

}
