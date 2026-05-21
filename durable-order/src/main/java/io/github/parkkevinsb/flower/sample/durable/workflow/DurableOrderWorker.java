package io.github.parkkevinsb.flower.sample.durable.workflow;

import io.github.parkkevinsb.flower.core.engine.Engine;
import io.github.parkkevinsb.flower.core.engine.EngineDump;
import io.github.parkkevinsb.flower.core.flow.FlowSnapshot;
import io.github.parkkevinsb.flower.core.worker.Worker;
import org.springframework.stereotype.Component;

@Component
public final class DurableOrderWorker {

    public static final String NAME = OrderFlowFactory.WORKER_NAME;

    private final Engine engine;
    private final OrderFlowFactory orderFlowFactory;
    private final AuditFlowFactory auditFlowFactory;

    public DurableOrderWorker(
            Engine engine,
            OrderFlowFactory orderFlowFactory,
            AuditFlowFactory auditFlowFactory) {
        this.engine = engine;
        this.orderFlowFactory = orderFlowFactory;
        this.auditFlowFactory = auditFlowFactory;
    }

    public void submitOrder(String orderId) {
        worker().submit(orderFlowFactory.create(orderId));
    }

    public void submitAudit(String message) {
        worker().submit(auditFlowFactory.create(message));
    }

    public void cancelAll() {
        Worker worker = worker();
        for (EngineDump.WorkerDump workerDump : engine.dump().workers()) {
            if (!NAME.equals(workerDump.name())) {
                continue;
            }
            for (FlowSnapshot flow : workerDump.flows()) {
                worker.cancel(flow.flowId());
            }
        }
    }

    private Worker worker() {
        return engine.worker(NAME);
    }
}
