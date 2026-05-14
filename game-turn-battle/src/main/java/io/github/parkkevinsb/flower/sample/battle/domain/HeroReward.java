package io.github.parkkevinsb.flower.sample.battle.domain;

import java.util.List;

public final class HeroReward {

    private final Hero hero;
    private final List<String> logEntries;

    public HeroReward(Hero hero, List<String> logEntries) {
        this.hero = hero;
        this.logEntries = List.copyOf(logEntries);
    }

    public Hero getHero() {
        return hero;
    }

    public List<String> getLogEntries() {
        return logEntries;
    }
}
