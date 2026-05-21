package io.github.parkkevinsb.flower.sample.durable.workflow;

import io.github.parkkevinsb.flower.core.flow.Flow;
import io.github.parkkevinsb.flower.sample.durable.domain.OrderRepository;
import io.github.parkkevinsb.flower.sample.durable.workflow.step.FinishAuditStep;
import io.github.parkkevinsb.flower.sample.durable.workflow.step.PauseAuditStep;
import io.github.parkkevinsb.flower.sample.durable.workflow.step.StartAuditStep;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuditFlowFactory {

    private final OrderRepository orders;

    public AuditFlowFactory(OrderRepository orders) {
        this.orders = orders;
    }

    public Flow create(String message) {
        String auditId = "audit-" + UUID.randomUUID().toString().substring(0, 8);
        return Flow.builder("audit", auditId)
                .step("write-audit-start", new StartAuditStep(orders, message))
                .step("pause", new PauseAuditStep())
                .step("write-audit-finish", new FinishAuditStep(orders, message))
                .build();
    }
}
