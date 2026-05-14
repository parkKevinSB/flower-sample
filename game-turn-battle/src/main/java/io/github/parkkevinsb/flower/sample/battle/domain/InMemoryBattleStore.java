package io.github.parkkevinsb.flower.sample.battle.domain;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

@Component
public class InMemoryBattleStore implements BattleStore {

    private final Random random = new Random();
    private final Map<String, Battle> battles = new LinkedHashMap<>();
    private Hero hero = Hero.initial();
    private String currentBattleId;

    @Override
    public synchronized Battle start(String battleId, MonsterType requestedMonster) {
        MonsterType monster = requestedMonster == null ? MonsterType.random(random) : requestedMonster;
        Battle fresh = Battle.start(battleId, hero, monster);
        battles.put(battleId, fresh);
        currentBattleId = battleId;
        return fresh;
    }

    @Override
    public synchronized Battle nextTurn(String battleId) {
        return mutate(battleId, current -> current.withTurn(current.getTurn() + 1));
    }

    @Override
    public synchronized Battle setStatus(String battleId, BattleStatus status) {
        return mutate(battleId, current -> current.withStatus(status));
    }

    @Override
    public synchronized Battle applyHeroAttack(String battleId) {
        return mutate(battleId, current -> {
            int damage = hero.damageAgainst(current.getMonsterType());
            String entry = "Hero swings the sword for " + damage + " damage.";
            return current.withMonsterDamage(damage, entry);
        });
    }

    @Override
    public synchronized Battle applyMonsterAttack(String battleId) {
        return mutate(battleId, current -> {
            int damage = hero.damageTakenFrom(current.getMonsterType());
            String entry = current.getMonsterType().getDisplayName() + " hits back for " + damage + " damage.";
            Battle next = current.withHeroDamage(damage, entry);
            hero = hero.withCurrentHp(next.getHeroHp());
            return next;
        });
    }

    @Override
    public synchronized Battle attemptFlee(String battleId) {
        return mutate(battleId, current -> {
            int chance = current.getMonsterType().getFleeChancePercent();
            if (random.nextInt(100) < chance) {
                return current.withEscaped(chance);
            }
            return current.withFleeFailure(chance);
        });
    }

    @Override
    public synchronized Battle finishVictory(String battleId) {
        return mutate(battleId, current -> {
            LootItem item = LootItem.randomDrop(random, current.getMonsterType());
            int xp = current.getMonsterType().getXpReward();
            HeroReward reward = hero.gainReward(xp, item, current.getHeroHp());
            hero = reward.getHero();
            return current.withVictory(item, xp, reward.getLogEntries(), hero.getCurrentHp(), hero.getMaxHp());
        });
    }

    @Override
    public synchronized Battle finishDefeat(String battleId) {
        return mutate(battleId, current -> {
            hero = Hero.initial();
            return current.withDefeat();
        });
    }

    @Override
    public synchronized Battle find(String battleId) {
        return battles.get(battleId);
    }

    @Override
    public synchronized Collection<Battle> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(battles.values()));
    }

    @Override
    public synchronized GameState snapshot() {
        Battle battle = currentBattleId == null ? null : battles.get(currentBattleId);
        return new GameState(hero, battle);
    }

    @Override
    public synchronized void reset() {
        battles.clear();
        hero = Hero.initial();
        currentBattleId = null;
    }

    private Battle mutate(String battleId, java.util.function.UnaryOperator<Battle> op) {
        Battle existing = battles.get(battleId);
        if (existing == null) {
            throw new IllegalStateException("unknown battleId: " + battleId);
        }
        Battle next = op.apply(existing);
        battles.put(battleId, next);
        return next;
    }
}
