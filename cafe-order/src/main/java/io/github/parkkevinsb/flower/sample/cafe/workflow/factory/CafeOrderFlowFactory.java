package io.github.parkkevinsb.flower.sample.cafe.workflow.factory;

import io.github.parkkevinsb.flower.core.flow.Flow;
import org.springframework.stereotype.Component;

import static io.github.parkkevinsb.flower.sample.cafe.workflow.factory.CafeOrderStepCatalog.ACCEPT_ORDER;
import static io.github.parkkevinsb.flower.sample.cafe.workflow.factory.CafeOrderStepCatalog.BREW;
import static io.github.parkkevinsb.flower.sample.cafe.workflow.factory.CafeOrderStepCatalog.COMPLETE_ORDER;
import static io.github.parkkevinsb.flower.sample.cafe.workflow.factory.CafeOrderStepCatalog.PAYMENT;
import static io.github.parkkevinsb.flower.sample.cafe.workflow.factory.CafeOrderStepCatalog.PREPARE_CUP;

/**
 * Builds the standard cafe order Flow.
 *
 * <p>The factory owns the Flow composition. Step construction and dependency
 * wiring stay in {@link CafeOrderStepFactory}.
 */
@Component
public final class CafeOrderFlowFactory {

    private static final String FLOW_TYPE = "cafe-order";

    private final CafeOrderStepFactory steps;

    public CafeOrderFlowFactory(CafeOrderStepFactory steps) {
        this.steps = steps;
    }

    public Flow create(String orderId) {
        return Flow.builder(FLOW_TYPE, orderId)
                .step(ACCEPT_ORDER, steps.acceptOrder())
                .step(PREPARE_CUP, steps.prepareCup())
                .step(PAYMENT, steps.payment())
                .step(BREW, steps.brew())
                .step(COMPLETE_ORDER, steps.completeOrder())
                .build();
    }
}
