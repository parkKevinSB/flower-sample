package io.github.parkkevinsb.flower.sample.battle.workflow.worker;

import io.github.parkkevinsb.flower.core.engine.Engine;
import io.github.parkkevinsb.flower.core.flow.Flow;
import org.springframework.stereotype.Component;

/**
 * Application-owned handle to the Flower Worker that runs turn-battle Flows.
 *
 * <p>The actual Worker instance is created by flower-spring-boot-starter
 * from {@code application.yml}.
 */
@Component
public final class TurnBattleWorker {

    public static final String NAME = "battles";

    private final Engine engine;

    public TurnBattleWorker(Engine engine) {
        this.engine = engine;
    }

    public void submit(Flow flow) {
        engine.worker(NAME).submit(flow);
    }
}
