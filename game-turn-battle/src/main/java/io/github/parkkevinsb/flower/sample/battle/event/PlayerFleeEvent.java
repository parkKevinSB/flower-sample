package io.github.parkkevinsb.flower.sample.battle.event;

/**
 * Plain Bloom event published when the player tries to escape.
 *
 * <p>The Step callback only signals Flower. The escape roll still happens
 * from the Worker thread inside Step.onTick.
 */
public final class PlayerFleeEvent {

    private final String battleId;

    public PlayerFleeEvent(String battleId) {
        this.battleId = battleId;
    }

    public String getBattleId() {
        return battleId;
    }
}
