package io.github.parkkevinsb.flower.sample.durable.workflow;

import io.github.parkkevinsb.flower.core.engine.Engine;
import io.github.parkkevinsb.flower.core.persistence.FlowCheckpointStore;
import io.github.parkkevinsb.flower.core.recovery.FlowFactoryRegistry;
import io.github.parkkevinsb.flower.core.recovery.FlowRecoveryService;
import io.github.parkkevinsb.flower.core.worker.DuplicatePolicy;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

@Component
public final class DurableOrderRecovery implements SmartLifecycle {

    private final Engine engine;
    private final FlowCheckpointStore checkpointStore;
    private final OrderFlowFactory orderFlowFactory;
    private volatile boolean running;

    public DurableOrderRecovery(
            Engine engine,
            FlowCheckpointStore checkpointStore,
            OrderFlowFactory orderFlowFactory) {
        this.engine = engine;
        this.checkpointStore = checkpointStore;
        this.orderFlowFactory = orderFlowFactory;
    }

    @Override
    public void start() {
        if (running) {
            return;
        }
        engine.attach();
        FlowFactoryRegistry registry = FlowFactoryRegistry.builder()
                .register(OrderFlowFactory.FLOW_TYPE, orderFlowFactory)
                .build();
        FlowRecoveryService.create(checkpointStore, registry)
                .recoverActiveForWorker(engine.worker(OrderFlowFactory.WORKER_NAME), DuplicatePolicy.IGNORE);
        running = true;
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public int getPhase() {
        return -100;
    }
}
