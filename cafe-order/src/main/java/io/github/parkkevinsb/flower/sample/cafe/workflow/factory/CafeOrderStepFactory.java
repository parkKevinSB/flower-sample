package io.github.parkkevinsb.flower.sample.cafe.workflow.factory;

import io.github.parkkevinsb.flower.sample.cafe.domain.CafeOrderStore;
import io.github.parkkevinsb.flower.sample.cafe.workflow.step.AcceptOrderStep;
import io.github.parkkevinsb.flower.sample.cafe.workflow.step.BrewStep;
import io.github.parkkevinsb.flower.sample.cafe.workflow.step.CompleteOrderStep;
import io.github.parkkevinsb.flower.sample.cafe.workflow.step.PaymentStep;
import io.github.parkkevinsb.flower.sample.cafe.workflow.step.PrepareCupStep;
import org.springframework.stereotype.Component;

/**
 * Creates fresh Step instances with their dependencies.
 *
 * <p>Steps are per-Flow state holders. They can contain subscriptions, signals,
 * timeouts, and stepNo, so they should not be singleton Spring components.
 */
@Component
public final class CafeOrderStepFactory {

    private final CafeOrderStore store;

    public CafeOrderStepFactory(CafeOrderStore store) {
        this.store = store;
    }

    public AcceptOrderStep acceptOrder() {
        return new AcceptOrderStep(store);
    }

    public PrepareCupStep prepareCup() {
        return new PrepareCupStep(store);
    }

    public PaymentStep payment() {
        return new PaymentStep(store);
    }

    public BrewStep brew() {
        return new BrewStep(store);
    }

    public CompleteOrderStep completeOrder() {
        return new CompleteOrderStep(store);
    }
}
