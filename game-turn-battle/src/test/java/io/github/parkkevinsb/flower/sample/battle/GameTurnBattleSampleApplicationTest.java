package io.github.parkkevinsb.flower.sample.battle;

import io.github.parkkevinsb.flower.sample.battle.api.BattleResponse;
import io.github.parkkevinsb.flower.sample.battle.domain.Battle;
import io.github.parkkevinsb.flower.sample.battle.domain.BattleStatus;
import io.github.parkkevinsb.flower.sample.battle.domain.BattleStore;
import io.github.parkkevinsb.flower.sample.battle.domain.Combatant;
import io.github.parkkevinsb.flower.sample.battle.domain.Hero;
import io.github.parkkevinsb.flower.sample.battle.domain.ItemRarity;
import io.github.parkkevinsb.flower.sample.battle.domain.LootItem;
import io.github.parkkevinsb.flower.sample.battle.domain.MonsterType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameTurnBattleSampleApplicationTest {

    @Autowired
    TestRestTemplate rest;

    @Autowired
    BattleStore store;

    @BeforeEach
    void resetGame() {
        store.reset();
    }

    @Test
    void heroWinsAndLevelsAcrossBattles() throws Exception {
        Battle first = winBattle("BOAR-1", MonsterType.BOAR);
        assertThat(first.getStatus()).isEqualTo(BattleStatus.VICTORY);
        assertThat(first.getWinner()).isEqualTo(Combatant.HERO);
        assertThat(first.getRewardXp()).isEqualTo(MonsterType.BOAR.getXpReward());

        Hero afterFirst = store.snapshot().getHero();
        assertThat(afterFirst.getLevel()).isEqualTo(1);
        assertThat(afterFirst.getXp()).isEqualTo(35);
        assertThat(afterFirst.getCurrentHp()).isLessThan(afterFirst.getMaxHp());
        assertThat(afterFirst.getInventory()).hasSizeBetween(0, 1);

        winBattle("BOAR-2", MonsterType.BOAR);

        Hero afterSecond = store.snapshot().getHero();
        assertThat(afterSecond.getLevel()).isEqualTo(2);
        assertThat(afterSecond.getXp()).isEqualTo(15);
        assertThat(afterSecond.getMaxHp()).isGreaterThanOrEqualTo(118);
        assertThat(afterSecond.getCurrentHp()).isEqualTo(afterSecond.getMaxHp());
        assertThat(afterSecond.getAttack()).isGreaterThanOrEqualTo(27);
        assertThat(afterSecond.getInventory()).hasSizeBetween(0, 2);
    }

    @Test
    void deathResetsThePersistentHero() throws Exception {
        winBattle("SETUP-1", MonsterType.BOAR);
        winBattle("SETUP-2", MonsterType.BOAR);
        assertThat(store.snapshot().getHero().getLevel()).isEqualTo(2);

        startBattle("ABYSS-1", MonsterType.ABYSS_LORD);
        Battle defeat = playUntilFinished("ABYSS-1");

        assertThat(defeat.getStatus()).isEqualTo(BattleStatus.DEFEAT);
        assertThat(defeat.getWinner()).isEqualTo(Combatant.MONSTER);

        Hero hero = store.snapshot().getHero();
        assertThat(hero.getLevel()).isEqualTo(1);
        assertThat(hero.getXp()).isZero();
        assertThat(hero.getCurrentHp()).isEqualTo(hero.getMaxHp());
        assertThat(hero.getInventory()).isEmpty();
    }

    @Test
    void unknownBattleReturns404() {
        assertThat(rest.getForEntity("/battles/NOPE", BattleResponse.class)
                .getStatusCode()
                .value())
                .isEqualTo(404);
    }

    @Test
    void weightedMonsterRandomMakesBossesRare() {
        Map<MonsterType, Integer> counts = new EnumMap<>(MonsterType.class);
        Random random = new Random(7);

        for (int i = 0; i < 10_000; i++) {
            MonsterType monster = MonsterType.random(random);
            counts.merge(monster, 1, Integer::sum);
        }

        assertThat(counts.get(MonsterType.BOAR)).isBetween(1_800, 2_200);
        assertThat(counts.get(MonsterType.WYVERN)).isBetween(1_100, 1_300);
        assertThat(counts.get(MonsterType.DRAGON)).isBetween(700, 900);
        assertThat(counts.get(MonsterType.ABYSS_LORD)).isBetween(330, 470);
        assertThat(counts.get(MonsterType.VOID_EMPEROR)).isBetween(60, 140);
    }

    @Test
    void voidEmperorIsAboveAbyssLord() {
        assertThat(MonsterType.VOID_EMPEROR.getMaxHp()).isGreaterThan(MonsterType.ABYSS_LORD.getMaxHp());
        assertThat(MonsterType.VOID_EMPEROR.getAttack()).isGreaterThan(MonsterType.ABYSS_LORD.getAttack());
        assertThat(MonsterType.VOID_EMPEROR.getDefense()).isGreaterThan(MonsterType.ABYSS_LORD.getDefense());
        assertThat(MonsterType.VOID_EMPEROR.getXpReward()).isGreaterThan(MonsterType.ABYSS_LORD.getXpReward());
        assertThat(MonsterType.VOID_EMPEROR.getUniqueDropWeight())
                .isGreaterThan(MonsterType.ABYSS_LORD.getUniqueDropWeight());
    }

    @Test
    void fleeChanceDropsAsMonstersGetStronger() {
        assertThat(MonsterType.BOAR.getFleeChancePercent()).isGreaterThan(MonsterType.ORC.getFleeChancePercent());
        assertThat(MonsterType.ORC.getFleeChancePercent()).isGreaterThan(MonsterType.WRAITH.getFleeChancePercent());
        assertThat(MonsterType.WRAITH.getFleeChancePercent()).isGreaterThan(MonsterType.DRAGON.getFleeChancePercent());
        assertThat(MonsterType.DRAGON.getFleeChancePercent()).isGreaterThan(MonsterType.ABYSS_LORD.getFleeChancePercent());
        assertThat(MonsterType.ABYSS_LORD.getFleeChancePercent())
                .isGreaterThan(MonsterType.VOID_EMPEROR.getFleeChancePercent());
    }

    @Test
    void lootDropCanReturnNoItem() {
        int drops = 0;
        int noDrops = 0;
        Random random = new Random(11);

        for (int i = 0; i < 500; i++) {
            if (LootItem.randomDrop(random, MonsterType.BOAR) == null) {
                noDrops++;
            } else {
                drops++;
            }
        }

        assertThat(drops).isGreaterThan(150);
        assertThat(noDrops).isGreaterThan(150);
    }

    @Test
    void uniqueItemsHaveBetterStatsAndColorMetadata() {
        assertThat(LootItem.WORLDBREAKER_CHARM.getRarity()).isEqualTo(ItemRarity.UNIQUE);
        assertThat(LootItem.WORLDBREAKER_CHARM.getColor()).isEqualTo("#c95b1d");
        assertThat(LootItem.WORLDBREAKER_CHARM.powerScore())
                .isGreaterThan(LootItem.SUNFORGED_AXE.powerScore())
                .isGreaterThan(LootItem.SAPPHIRE_EDGE.powerScore())
                .isGreaterThan(LootItem.CRACKED_RING.powerScore());
    }

    private Battle winBattle(String id, MonsterType monster) throws InterruptedException {
        startBattle(id, monster);
        Battle result = playUntilFinished(id);
        assertThat(result.getStatus()).isEqualTo(BattleStatus.VICTORY);
        return result;
    }

    private void startBattle(String id, MonsterType monster) throws InterruptedException {
        rest.postForEntity("/battles/" + id + "?monster=" + monster.name(), null, String.class);
        awaitStatus(id, BattleStatus.WAITING_PLAYER_ACTION);
    }

    private Battle playUntilFinished(String id) throws InterruptedException {
        Battle battle = store.find(id);
        int guard = 0;
        while (battle == null || !battle.isFinished()) {
            if (guard++ > 40) {
                throw new AssertionError("battle did not finish after 40 attacks");
            }
            battle = playOneAttack(id);
        }
        return battle;
    }

    private Battle playOneAttack(String id) throws InterruptedException {
        Battle before = awaitStatus(id, BattleStatus.WAITING_PLAYER_ACTION);
        rest.postForEntity("/battles/" + id + "/attack", null, Void.class);
        awaitStatus(id, BattleStatus.ANIMATING_PLAYER_ATTACK);
        rest.postForEntity("/battles/" + id + "/animation-finished", null, Void.class);
        return awaitTurnAdvancedOrFinished(id, before.getTurn());
    }

    private Battle awaitStatus(String battleId, BattleStatus expected) throws InterruptedException {
        long deadline = System.currentTimeMillis() + 5_000L;
        while (System.currentTimeMillis() < deadline) {
            Battle battle = store.find(battleId);
            if (battle != null && battle.getStatus() == expected) {
                return battle;
            }
            Thread.sleep(50L);
        }
        Battle battle = store.find(battleId);
        throw new AssertionError("battle " + battleId + " did not reach status " + expected
                + ", last was " + (battle == null ? "null" : battle.getStatus()));
    }

    private Battle awaitTurnAdvancedOrFinished(String battleId, int previousTurn) throws InterruptedException {
        long deadline = System.currentTimeMillis() + 7_000L;
        while (System.currentTimeMillis() < deadline) {
            Battle battle = store.find(battleId);
            if (battle != null && battle.isFinished()) {
                return battle;
            }
            if (battle != null
                    && battle.getTurn() > previousTurn
                    && battle.getStatus() == BattleStatus.WAITING_PLAYER_ACTION) {
                return battle;
            }
            Thread.sleep(50L);
        }
        Battle battle = store.find(battleId);
        throw new AssertionError("battle " + battleId + " did not finish or advance turn"
                + ", last was " + (battle == null ? "null" : battle.getStatus()));
    }
}
