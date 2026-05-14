# game-turn-battle

Spring Boot 3.3 + Java 21 sample showing a small **Flower** turn loop as an
actual playable 1:1 battle: a persistent hero fights one random monster at a
time, wins loot and XP, levels up automatically, and resets on death.

This sample is part of the [`flower-sample`](../README.md) collection and
runs on **port 8082**.

## Flow

```text
POST /battles

Flow: turn-battle
  -> start-turn              logs the current turn
  -> wait-player-action      waits for PlayerAttackEvent or PlayerFleeEvent from Bloom
  -> resolve-player-attack   applies sword damage; waits for animation event
  -> check-after-player      victory ends the Flow immediately
  -> monster-action          server-side monster windup and attack
  -> check-after-monster     death ends the Flow and resets the hero
  -> finish-turn             bumps turn; goTo("start-turn")
```

## What This Sample Shows

Flower remains the orchestration layer. Bloom is only the event bus used at
the application boundary.

The important event rule is visible in `WaitPlayerActionStep` and
`ResolveActionStep`: callbacks only call `ctx.signal(...)`. They do not update
`BattleStore` and they do not return a `StepResult`. The Worker observes the
signal during `onTick`, then transitions the Flow and mutates game state from
inside the Step. This also applies to flee attempts: Bloom delivers
`PlayerFleeEvent`, and the escape roll is made inside the Flower Step.

This sample also avoids Java `record` in the game domain. Records are fine in
Spring Boot sample apps, but plain classes keep the sample from implying that
Flower's core idea depends on Java 17 syntax.

## Game Rules

The hero has level, XP, current HP, max HP, attack, defense, and inventory. A
new battle starts with the hero's current HP, so damage carries across battles.
HP is restored to full only on level-up or after death resets the hero.

Monsters are picked randomly by the server with weighted odds. The middle and
late monsters show up often enough to keep the run tense, Abyss Lord is a rare
boss encounter, and Void Emperor is the nearly impossible top-end roll.

| Monster         | Odds | HP  | Attack | Defense | XP  |
|-----------------|------|-----|--------|---------|-----|
| Wild Boar       | 20%  | 45  | 12     | 1       | 35  |
| Orc             | 22%  | 85  | 20     | 4       | 65  |
| Skeleton Knight | 18%  | 95  | 18     | 5       | 80  |
| Wraith          | 15%  | 120 | 24     | 7       | 105 |
| Wyvern          | 12%  | 180 | 40     | 8       | 125 |
| Dragon          | 8%   | 200 | 70     | 10      | 170 |
| Abyss Lord      | 4%   | 600 | 200    | 22      | 340 |
| Void Emperor    | 1%   | 1400 | 520    | 55      | 1200 |

The player can `Attack` or try to `Flee`. A successful flee ends the battle
with no loot or XP, and the hero keeps current HP. A failed flee consumes the
turn and the monster attacks. On victory the server rolls for loot, adds XP,
and applies level-ups. No item drop is possible, especially from weak monsters.
On defeat the battle shows death and the persistent hero is reset to level 1
with full HP and an empty inventory.

| Monster         | Flee chance |
|-----------------|-------------|
| Wild Boar       | 85%         |
| Orc             | 70%         |
| Skeleton Knight | 62%         |
| Wraith          | 50%         |
| Wyvern          | 38%         |
| Dragon          | 25%         |
| Abyss Lord      | 12%         |
| Void Emperor    | 3%          |

| Monster         | Item drop chance |
|-----------------|------------------|
| Wild Boar       | 45%              |
| Orc             | 50%              |
| Skeleton Knight | 55%              |
| Wraith          | 60%              |
| Wyvern          | 65%              |
| Dragon          | 72%              |
| Abyss Lord      | 85%              |
| Void Emperor    | 95%              |

Loot has four rarities:

| Rarity | UI color    | Notes |
|--------|-------------|-------|
| Common | white       | small stat bumps |
| Magic  | blue        | stronger focused bonuses |
| Rare   | yellow      | high mixed bonuses |
| Unique | dark orange | intentionally overpowered chase items |

Every item can add attack, defense, max HP, or a mix of those stats. Stronger
monsters have better odds for Rare and Unique drops.

## Web UI

Run the app and open:

```text
http://localhost:8082/
```

The UI is a static Spring resource at `src/main/resources/static/index.html`.
Sprites live as separate editable SVG assets under
`src/main/resources/static/assets/sprites/`, so the game screen can be tuned
without touching the Flower/Bloom sample code. The page drives the same
REST/Bloom endpoints used by tests.

## API

```bash
# Start a random encounter.
curl -X POST http://localhost:8082/battles

# Start a deterministic encounter, useful for tests or demos.
curl -X POST "http://localhost:8082/battles/B-1?monster=BOAR"

# Player sword swing. This publishes PlayerAttackEvent into Bloom.
curl -X POST http://localhost:8082/battles/B-1/attack

# Try to escape. This publishes PlayerFleeEvent into Bloom.
curl -X POST http://localhost:8082/battles/B-1/flee

# Client animation finished. ResolveActionStep also has a timeout fallback.
curl -X POST http://localhost:8082/battles/B-1/animation-finished

# Read the current hero plus current battle.
curl http://localhost:8082/game

# Read battle history.
curl http://localhost:8082/battles
```

## Running

Install local snapshots first if needed:

```bash
cd ../bloom && mvn install
cd ../flower && mvn install
```

Then from the `flower-sample` root:

```bash
./gradlew :game-turn-battle:bootRun
```

## Tests

From the `flower-sample` root:

```bash
./gradlew :game-turn-battle:test
```

The tests boot the full Spring context and cover:

- victory reward + persistent hero XP/inventory
- automatic level-up across multiple battles
- death resetting the hero
- HP carrying across battles until level-up/death
- flee odds dropping as monster strength increases
- weighted monster encounters so Dragon, Abyss Lord, and Void Emperor stay rare
- item rarity color/stat metadata
- unknown battle lookup returning 404
