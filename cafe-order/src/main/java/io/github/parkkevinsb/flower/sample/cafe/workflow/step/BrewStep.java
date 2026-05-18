package io.github.parkkevinsb.flower.sample.cafe.workflow.step;

import io.github.parkkevinsb.flower.core.step.Step;
import io.github.parkkevinsb.flower.core.step.StepContext;
import io.github.parkkevinsb.flower.core.step.StepResult;
import io.github.parkkevinsb.flower.observability.logging.StepLogger;
import io.github.parkkevinsb.flower.sample.cafe.domain.CafeOrderStore;
import io.github.parkkevinsb.flower.sample.cafe.event.BrewRequestedEvent;
import io.github.parkkevinsb.flower.sample.cafe.event.CoffeeReadyEvent;

/**
 * Sends a brew ticket to the barista station and waits until the coffee is
 * ready.
 *
 * <p>This Step does not brew coffee itself. It owns the orchestration around
 * that work: publish the request, wait for the matching reply event, and decide
 * whether to complete the Step or fail on timeout.
 */
public final class BrewStep extends Step {

    private static final String SIGNAL_COFFEE_READY = "coffee-ready";
    private static final long BREW_TIMEOUT_MS = 20_000L;
    private static final long READY_VISIBLE_MS = 1_700L;

    private enum Phase {
        SEND_BREW_TICKET(0),
        WAIT_COFFEE_READY(10),
        SHOW_READY(20),
        UNKNOWN(-1);

        private final int stepNo;

        Phase(int stepNo) {
            this.stepNo = stepNo;
        }

        int stepNo() {
            return stepNo;
        }

        static Phase from(int stepNo) {
            for (Phase phase : values()) {
                if (phase.stepNo == stepNo) {
                    return phase;
                }
            }
            return UNKNOWN;
        }
    }

    private final CafeOrderStore store;

    public BrewStep(CafeOrderStore store) {
        this.store = store;
    }

    @Override
    protected void onEnter(StepContext ctx) {
        ctx.subscribe(CoffeeReadyEvent.class, event -> onCoffeeReady(ctx, event));
    }

    private void onCoffeeReady(StepContext ctx, CoffeeReadyEvent event) {
        String orderId = ctx.flowId().flowKey();
        if (orderId.equals(event.getOrderId())) {
            ctx.signal(SIGNAL_COFFEE_READY);
        }
    }

    @Override
    protected StepResult onTick(StepContext ctx) {
        String orderId = ctx.flowId().flowKey();
        Phase phase = Phase.from(ctx.stepNo());

        switch (phase) {
            case SEND_BREW_TICKET:
                return sendBrewTicket(ctx, orderId);

            case WAIT_COFFEE_READY:
                return waitCoffeeReady(ctx, orderId);

            case SHOW_READY:
                return showReady(ctx);

            default:
                return StepResult.fail(new IllegalStateException("unknown brew stepNo: " + ctx.stepNo()));
        }
    }

    private StepResult sendBrewTicket(StepContext ctx, String orderId) {
        store.brewRequested(orderId);
        StepLogger.of(BrewStep.class, ctx).info("send brew ticket");
        ctx.startTimeout(BREW_TIMEOUT_MS);
        ctx.eventBus().publish(new BrewRequestedEvent(orderId));
        ctx.setStepNo(Phase.WAIT_COFFEE_READY.stepNo());
        return StepResult.stay();
    }

    private StepResult waitCoffeeReady(StepContext ctx, String orderId) {
        if (ctx.hasSignal(SIGNAL_COFFEE_READY)) {
            store.ready(orderId);
            StepLogger.of(BrewStep.class, ctx).info("coffee ready");
            ctx.startTimeout(READY_VISIBLE_MS);
            ctx.setStepNo(Phase.SHOW_READY.stepNo());
            return StepResult.stay();
        }
        if (ctx.timedOut()) {
            store.fail(orderId);
            return StepResult.fail(new IllegalStateException("brew timeout: " + orderId));
        }
        return StepResult.stay();
    }

    private StepResult showReady(StepContext ctx) {
        return ctx.timedOut() ? StepResult.done() : StepResult.stay();
    }
}
