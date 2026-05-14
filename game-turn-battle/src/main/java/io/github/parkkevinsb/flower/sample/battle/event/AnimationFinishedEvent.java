package io.github.parkkevinsb.flower.sample.battle.event;

/**
 * Plain event published when the client-side animation for a player action
 * finishes. The sample uses this to gate progression through the
 * resolve-action Step, so the Flow does not race ahead of the UI.
 */
public final class AnimationFinishedEvent {

    private final String battleId;

    public AnimationFinishedEvent(String battleId) {
        this.battleId = battleId;
    }

    public String getBattleId() {
        return battleId;
    }
}
