package io.github.parkkevinsb.flower.sample.durable.workflow;

import io.github.parkkevinsb.flower.core.flow.Flow;
import io.github.parkkevinsb.flower.core.flow.FlowId;
import io.github.parkkevinsb.flower.core.recovery.FlowFactory;
import io.github.parkkevinsb.flower.core.step.RecoveryPolicy;
import io.github.parkkevinsb.flower.sample.durable.domain.OrderRepository;
import io.github.parkkevinsb.flower.sample.durable.workflow.step.CompleteOrderStep;
import io.github.parkkevinsb.flower.sample.durable.workflow.step.ReserveInventoryStep;
import io.github.parkkevinsb.flower.sample.durable.workflow.step.ShipOrderStep;
import io.github.parkkevinsb.flower.sample.durable.workflow.step.ValidateOrderStep;
import io.github.parkkevinsb.flower.sample.durable.workflow.step.WaitPaymentStep;
import org.springframework.stereotype.Component;

@Component
public class OrderFlowFactory implements FlowFactory {

    public static final String FLOW_TYPE = "order";
    public static final String WORKER_NAME = "orders";
    private static final String DEFINITION_VERSION = "durable-order/v1";
    private static final RecoveryPolicy RECOVERABLE = RecoveryPolicy.REENTER_IDEMPOTENT;

    private final OrderRepository orders;

    public OrderFlowFactory(OrderRepository orders) {
        this.orders = orders;
    }

    @Override
    public Flow create(FlowId flowId) {
        return create(flowId.flowKey());
    }

    public Flow create(String orderId) {
        return Flow.builder(FLOW_TYPE, orderId)
                .durable()
                .definitionVersion(DEFINITION_VERSION)
                .durableStep("validate-order", new ValidateOrderStep(orders, orderId), RECOVERABLE)
                .durableStep("wait-payment", new WaitPaymentStep(orders, orderId), RECOVERABLE)
                .durableStep("reserve-inventory", new ReserveInventoryStep(orders, orderId), RECOVERABLE)
                .durableStep("ship-order", new ShipOrderStep(orders, orderId), RECOVERABLE)
                .durableStep("complete-order", new CompleteOrderStep(orders, orderId), RECOVERABLE)
                .build();
    }
}
