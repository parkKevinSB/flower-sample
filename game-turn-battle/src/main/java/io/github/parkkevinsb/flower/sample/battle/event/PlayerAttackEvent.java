package io.github.parkkevinsb.flower.sample.battle.event;

/**
 * Plain Bloom event published when the player clicks the sword attack.
 *
 * <p>The Step callback only signals Flower. Combat state changes still happen
 * from the Worker thread inside Step.onTick.
 */
public final class PlayerAttackEvent {

    private final String battleId;

    public PlayerAttackEvent(String battleId) {
        this.battleId = battleId;
    }

    public String getBattleId() {
        return battleId;
    }
}
