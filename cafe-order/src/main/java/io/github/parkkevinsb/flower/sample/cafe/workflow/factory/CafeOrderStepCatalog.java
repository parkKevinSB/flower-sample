package io.github.parkkevinsb.flower.sample.cafe.workflow.factory;

import io.github.parkkevinsb.flower.core.step.Step;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Catalog of Steps that a user may compose into a Flow.
 *
 * <p>This is application code, not Flower core. Flower executes the Flow after
 * it is built; this catalog decides which domain Steps are exposed to users
 * and which combinations are accepted.
 */
@Component
public final class CafeOrderStepCatalog {

    public static final String ACCEPT_ORDER = "accept-order";
    public static final String PREPARE_CUP = "prepare-cup";
    public static final String PAYMENT = "payment";
    public static final String BREW = "brew";
    public static final String COMPLETE_ORDER = "complete-order";

    private final CafeOrderStepFactory steps;
    private final List<CafeOrderStepDescriptor> descriptors;

    public CafeOrderStepCatalog(CafeOrderStepFactory steps) {
        this.steps = steps;
        this.descriptors = List.of(
                new CafeOrderStepDescriptor(
                        ACCEPT_ORDER,
                        "Accept order",
                        "Create or reset the order in the in-memory store.",
                        true),
                new CafeOrderStepDescriptor(
                        PREPARE_CUP,
                        "Prepare cup",
                        "A stepNo-only Step: pick a cup, then label it.",
                        false),
                new CafeOrderStepDescriptor(
                        PAYMENT,
                        "Payment",
                        "Publish PaymentRequestedEvent and wait for PaymentApprovedEvent.",
                        false),
                new CafeOrderStepDescriptor(
                        BREW,
                        "Brew",
                        "Send a brew ticket and wait for CoffeeReadyEvent.",
                        false),
                new CafeOrderStepDescriptor(
                        COMPLETE_ORDER,
                        "Complete order",
                        "Mark the order completed and finish the Flow.",
                        true));
    }

    public List<CafeOrderStepDescriptor> descriptors() {
        return descriptors;
    }

    public Step create(String stepId) {
        switch (stepId) {
            case ACCEPT_ORDER:
                return steps.acceptOrder();
            case PREPARE_CUP:
                return steps.prepareCup();
            case PAYMENT:
                return steps.payment();
            case BREW:
                return steps.brew();
            case COMPLETE_ORDER:
                return steps.completeOrder();
            default:
                throw new IllegalArgumentException("unknown step: " + stepId);
        }
    }

    public List<String> validate(List<String> requestedSteps) {
        if (requestedSteps == null || requestedSteps.isEmpty()) {
            throw new IllegalArgumentException("steps must not be empty");
        }

        List<String> stepIds = new ArrayList<>(requestedSteps);
        Set<String> seen = new HashSet<>();
        for (String stepId : stepIds) {
            if (!known(stepId)) {
                throw new IllegalArgumentException("unknown step: " + stepId);
            }
            if (!seen.add(stepId)) {
                throw new IllegalArgumentException("duplicate step: " + stepId);
            }
        }

        if (!ACCEPT_ORDER.equals(stepIds.get(0))) {
            throw new IllegalArgumentException("custom flow must start with " + ACCEPT_ORDER);
        }
        if (!COMPLETE_ORDER.equals(stepIds.get(stepIds.size() - 1))) {
            throw new IllegalArgumentException("custom flow must end with " + COMPLETE_ORDER);
        }
        if (containsBoth(stepIds, PAYMENT, BREW) && indexOf(stepIds, PAYMENT) > indexOf(stepIds, BREW)) {
            throw new IllegalArgumentException(PAYMENT + " must run before " + BREW);
        }
        return stepIds;
    }

    private boolean known(String stepId) {
        for (CafeOrderStepDescriptor descriptor : descriptors) {
            if (descriptor.getId().equals(stepId)) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsBoth(List<String> stepIds, String first, String second) {
        return stepIds.contains(first) && stepIds.contains(second);
    }

    private static int indexOf(List<String> stepIds, String stepId) {
        return stepIds.indexOf(stepId);
    }
}
