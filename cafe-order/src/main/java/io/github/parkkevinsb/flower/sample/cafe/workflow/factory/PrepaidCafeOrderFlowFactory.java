package io.github.parkkevinsb.flower.sample.cafe.workflow.factory;

import io.github.parkkevinsb.flower.core.flow.Flow;
import org.springframework.stereotype.Component;

import static io.github.parkkevinsb.flower.sample.cafe.workflow.factory.CafeOrderStepCatalog.ACCEPT_ORDER;
import static io.github.parkkevinsb.flower.sample.cafe.workflow.factory.CafeOrderStepCatalog.BREW;
import static io.github.parkkevinsb.flower.sample.cafe.workflow.factory.CafeOrderStepCatalog.COMPLETE_ORDER;
import static io.github.parkkevinsb.flower.sample.cafe.workflow.factory.CafeOrderStepCatalog.PREPARE_CUP;

/**
 * Builds a prepaid order Flow from the same Step set.
 *
 * <p>This flow skips {@code payment} because payment has already happened at
 * another boundary, then reuses the cup preparation, brew, and completion
 * steps from the standard order Flow.
 */
@Component
public final class PrepaidCafeOrderFlowFactory {

    private static final String FLOW_TYPE = "prepaid-cafe-order";

    private final CafeOrderStepFactory steps;

    public PrepaidCafeOrderFlowFactory(CafeOrderStepFactory steps) {
        this.steps = steps;
    }

    public Flow create(String orderId) {
        return Flow.builder(FLOW_TYPE, orderId)
                .step(ACCEPT_ORDER, steps.acceptOrder())
                .step(PREPARE_CUP, steps.prepareCup())
                .step(BREW, steps.brew())
                .step(COMPLETE_ORDER, steps.completeOrder())
                .build();
    }
}
