package io.github.parkkevinsb.flower.sample.battle.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Immutable battle snapshot.
 *
 * <p>This sample intentionally keeps the game domain as plain classes rather
 * than Java records. Spring Boot already requires Java 17+, but the Flower
 * idea being demonstrated is independent from record syntax.
 */
public final class Battle {

    private final String battleId;
    private final int turn;
    private final int heroHp;
    private final int heroMaxHp;
    private final MonsterType monsterType;
    private final int monsterHp;
    private final BattleStatus status;
    private final Combatant winner;
    private final LootItem rewardItem;
    private final int rewardXp;
    private final List<String> log;
    private final Instant updatedAt;

    public Battle(
            String battleId,
            int turn,
            int heroHp,
            int heroMaxHp,
            MonsterType monsterType,
            int monsterHp,
            BattleStatus status,
            Combatant winner,
            LootItem rewardItem,
            int rewardXp,
            List<String> log,
            Instant updatedAt
    ) {
        this.battleId = battleId;
        this.turn = turn;
        this.heroHp = heroHp;
        this.heroMaxHp = heroMaxHp;
        this.monsterType = monsterType;
        this.monsterHp = monsterHp;
        this.status = status;
        this.winner = winner;
        this.rewardItem = rewardItem;
        this.rewardXp = rewardXp;
        this.log = log == null ? List.of() : List.copyOf(log);
        this.updatedAt = updatedAt;
    }

    public static Battle start(String battleId, Hero hero, MonsterType monsterType) {
        return new Battle(
                battleId,
                1,
                hero.getCurrentHp(),
                hero.getMaxHp(),
                monsterType,
                monsterType.getMaxHp(),
                BattleStatus.TURN_STARTED,
                null,
                null,
                0,
                List.of(monsterType.getDisplayName() + " appears."),
                Instant.now());
    }

    public Battle withStatus(BattleStatus next) {
        return copy(turn, heroHp, monsterHp, next, winner, rewardItem, rewardXp, log);
    }

    public Battle withTurn(int nextTurn) {
        return copy(nextTurn, heroHp, monsterHp, BattleStatus.TURN_STARTED, winner, rewardItem, rewardXp,
                append("Turn " + nextTurn + " begins."));
    }

    public Battle withHeroDamage(int damage, String logEntry) {
        return copy(turn, Math.max(0, heroHp - damage), monsterHp, status, winner, rewardItem, rewardXp,
                append(logEntry));
    }

    public Battle withMonsterDamage(int damage, String logEntry) {
        return copy(turn, heroHp, Math.max(0, monsterHp - damage), status, winner, rewardItem, rewardXp,
                append(logEntry));
    }

    public Battle withVictory(LootItem item, int xp, List<String> rewardLogs, int finalHeroHp, int finalHeroMaxHp) {
        List<String> nextLog = new ArrayList<>(log);
        nextLog.add("Monster defeated.");
        nextLog.addAll(rewardLogs);
        return new Battle(
                battleId,
                turn,
                finalHeroHp,
                finalHeroMaxHp,
                monsterType,
                monsterHp,
                BattleStatus.VICTORY,
                Combatant.HERO,
                item,
                xp,
                nextLog,
                Instant.now());
    }

    public Battle withDefeat() {
        return copy(turn, heroHp, monsterHp, BattleStatus.DEFEAT, Combatant.MONSTER, rewardItem, rewardXp,
                append("You died. The hero is reset."));
    }

    public Battle withEscaped(int chancePercent) {
        return copy(turn, heroHp, monsterHp, BattleStatus.ESCAPED, null, rewardItem, rewardXp,
                append("Hero escaped from " + monsterType.getDisplayName() + " (" + chancePercent + "% chance)."));
    }

    public Battle withFleeFailure(int chancePercent) {
        return copy(turn, heroHp, monsterHp, BattleStatus.FLEE_FAILED, winner, rewardItem, rewardXp,
                append("Escape failed (" + chancePercent + "% chance)."));
    }

    public boolean isFinished() {
        return status == BattleStatus.VICTORY || status == BattleStatus.DEFEAT || status == BattleStatus.ESCAPED;
    }

    public String getBattleId() {
        return battleId;
    }

    public int getTurn() {
        return turn;
    }

    public int getHeroHp() {
        return heroHp;
    }

    public int getHeroMaxHp() {
        return heroMaxHp;
    }

    public MonsterType getMonsterType() {
        return monsterType;
    }

    public int getMonsterHp() {
        return monsterHp;
    }

    public int getMonsterMaxHp() {
        return monsterType.getMaxHp();
    }

    public BattleStatus getStatus() {
        return status;
    }

    public Combatant getWinner() {
        return winner;
    }

    public LootItem getRewardItem() {
        return rewardItem;
    }

    public int getRewardXp() {
        return rewardXp;
    }

    public List<String> getLog() {
        return log;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    private Battle copy(
            int nextTurn,
            int nextHeroHp,
            int nextMonsterHp,
            BattleStatus nextStatus,
            Combatant nextWinner,
            LootItem nextRewardItem,
            int nextRewardXp,
            List<String> nextLog
    ) {
        return new Battle(
                battleId,
                nextTurn,
                nextHeroHp,
                heroMaxHp,
                monsterType,
                nextMonsterHp,
                nextStatus,
                nextWinner,
                nextRewardItem,
                nextRewardXp,
                nextLog,
                Instant.now());
    }

    private List<String> append(String entry) {
        List<String> next = new ArrayList<>(log);
        next.add(entry);
        return next;
    }
}
