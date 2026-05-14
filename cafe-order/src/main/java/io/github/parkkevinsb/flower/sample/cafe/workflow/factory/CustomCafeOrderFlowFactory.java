package io.github.parkkevinsb.flower.sample.cafe.workflow.factory;

import io.github.parkkevinsb.flower.core.flow.Flow;
import io.github.parkkevinsb.flower.core.flow.FlowBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Builds a Flow from the Step ids selected by the user.
 */
@Component
public final class CustomCafeOrderFlowFactory {

    private static final String FLOW_TYPE = "custom-cafe-order";

    private final CafeOrderStepCatalog catalog;

    public CustomCafeOrderFlowFactory(CafeOrderStepCatalog catalog) {
        this.catalog = catalog;
    }

    public Flow create(String orderId, List<String> requestedSteps) {
        List<String> stepIds = catalog.validate(requestedSteps);
        FlowBuilder builder = Flow.builder(FLOW_TYPE, orderId);
        for (String stepId : stepIds) {
            builder.step(stepId, catalog.create(stepId));
        }
        return builder.build();
    }
}
