package io.github.parkkevinsb.flower.sample.battle.api;

import io.github.parkkevinsb.flower.sample.battle.domain.Battle;
import io.github.parkkevinsb.flower.sample.battle.domain.BattleStatus;
import io.github.parkkevinsb.flower.sample.battle.domain.Combatant;
import io.github.parkkevinsb.flower.sample.battle.domain.LootItem;
import io.github.parkkevinsb.flower.sample.battle.domain.MonsterType;

import java.time.Instant;
import java.util.List;

public final class BattleResponse {

    private final String battleId;
    private final int turn;
    private final int heroHp;
    private final int heroMaxHp;
    private final MonsterType monsterType;
    private final String monsterName;
    private final int monsterHp;
    private final int monsterMaxHp;
    private final BattleStatus status;
    private final Combatant winner;
    private final LootItem rewardItem;
    private final String rewardItemName;
    private final String rewardRarity;
    private final String rewardRarityClass;
    private final String rewardColor;
    private final String rewardStatSummary;
    private final int rewardXp;
    private final List<String> log;
    private final Instant updatedAt;

    public BattleResponse(Battle battle) {
        this.battleId = battle.getBattleId();
        this.turn = battle.getTurn();
        this.heroHp = battle.getHeroHp();
        this.heroMaxHp = battle.getHeroMaxHp();
        this.monsterType = battle.getMonsterType();
        this.monsterName = battle.getMonsterType().getDisplayName();
        this.monsterHp = battle.getMonsterHp();
        this.monsterMaxHp = battle.getMonsterMaxHp();
        this.status = battle.getStatus();
        this.winner = battle.getWinner();
        this.rewardItem = battle.getRewardItem();
        this.rewardItemName = battle.getRewardItem() == null ? null : battle.getRewardItem().getDisplayName();
        this.rewardRarity = battle.getRewardItem() == null ? null : battle.getRewardItem().getRarityName();
        this.rewardRarityClass = battle.getRewardItem() == null ? null : battle.getRewardItem().getRarityClass();
        this.rewardColor = battle.getRewardItem() == null ? null : battle.getRewardItem().getColor();
        this.rewardStatSummary = battle.getRewardItem() == null ? null : battle.getRewardItem().getStatSummary();
        this.rewardXp = battle.getRewardXp();
        this.log = battle.getLog();
        this.updatedAt = battle.getUpdatedAt();
    }

    public static BattleResponse of(Battle battle) {
        return new BattleResponse(battle);
    }

    public String getBattleId() { return battleId; }
    public int getTurn() { return turn; }
    public int getHeroHp() { return heroHp; }
    public int getHeroMaxHp() { return heroMaxHp; }
    public MonsterType getMonsterType() { return monsterType; }
    public String getMonsterName() { return monsterName; }
    public int getMonsterHp() { return monsterHp; }
    public int getMonsterMaxHp() { return monsterMaxHp; }
    public BattleStatus getStatus() { return status; }
    public Combatant getWinner() { return winner; }
    public LootItem getRewardItem() { return rewardItem; }
    public String getRewardItemName() { return rewardItemName; }
    public String getRewardRarity() { return rewardRarity; }
    public String getRewardRarityClass() { return rewardRarityClass; }
    public String getRewardColor() { return rewardColor; }
    public String getRewardStatSummary() { return rewardStatSummary; }
    public int getRewardXp() { return rewardXp; }
    public List<String> getLog() { return log; }
    public Instant getUpdatedAt() { return updatedAt; }
}
