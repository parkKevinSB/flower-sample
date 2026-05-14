package io.github.parkkevinsb.flower.sample.cafe.workflow.worker;

import io.github.parkkevinsb.flower.core.engine.Engine;
import io.github.parkkevinsb.flower.core.flow.Flow;
import org.springframework.stereotype.Component;

/**
 * Application-owned handle to the Flower Worker that runs cafe order Flows.
 *
 * <p>The actual Flower {@code Worker} instance is created by
 * flower-spring-boot-starter from {@code application.yml}. This class keeps
 * the worker name and submit call visible in application code.
 */
@Component
public final class CafeOrderWorker {

    public static final String NAME = "orders";

    private final Engine engine;

    public CafeOrderWorker(Engine engine) {
        this.engine = engine;
    }

    public void submit(Flow flow) {
        engine.worker(NAME).submit(flow);
    }
}
