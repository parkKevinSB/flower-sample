package io.github.parkkevinsb.flower.sample.cafe.workflow.step;

import io.github.parkkevinsb.flower.core.step.Step;
import io.github.parkkevinsb.flower.core.step.StepContext;
import io.github.parkkevinsb.flower.core.step.StepResult;
import io.github.parkkevinsb.flower.observability.logging.StepLogger;
import io.github.parkkevinsb.flower.sample.cafe.domain.CafeOrderStore;
import io.github.parkkevinsb.flower.sample.cafe.event.PaymentApprovedEvent;
import io.github.parkkevinsb.flower.sample.cafe.event.PaymentRequestedEvent;

/**
 * Publishes a {@link PaymentRequestedEvent} and waits for a matching
 * {@link PaymentApprovedEvent} signal.
 *
 * <p>This is the canonical Flower pattern: the event callback only sets a
 * signal, the Worker tick then reads the signal in {@link #onTick(StepContext)}
 * and converts it into a {@link StepResult}.
 */
public final class PaymentStep extends Step {

    private static final String SIGNAL_PAYMENT_APPROVED = "payment-approved";
    private static final int SEND_PAYMENT_REQUEST = 0;
    private static final int WAIT_PAYMENT_APPROVAL = 10;
    private static final int SHOW_PAYMENT_APPROVED = 20;
    private static final long PAYMENT_TIMEOUT_MS = 10_000L;
    private static final long PAYMENT_APPROVED_VISIBLE_MS = 1_700L;

    private final CafeOrderStore store;

    public PaymentStep(CafeOrderStore store) {
        this.store = store;
    }

    @Override
    protected void onEnter(StepContext ctx) {
        ctx.subscribe(PaymentApprovedEvent.class, event -> onPaymentApproved(ctx, event));
    }

    private void onPaymentApproved(StepContext ctx, PaymentApprovedEvent event) {
        String orderId = ctx.flowId().flowKey();
        if (orderId.equals(event.getOrderId())) {
            ctx.signal(SIGNAL_PAYMENT_APPROVED);
        }
    }

    @Override
    protected StepResult onTick(StepContext ctx) {
        String orderId = ctx.flowId().flowKey();

        if (ctx.stepNo() == SEND_PAYMENT_REQUEST) {
            store.paymentRequested(orderId);
            StepLogger.of(PaymentStep.class, ctx).info("request payment");
            ctx.startTimeout(PAYMENT_TIMEOUT_MS);
            ctx.eventBus().publish(new PaymentRequestedEvent(orderId));
            ctx.setStepNo(WAIT_PAYMENT_APPROVAL);
            return StepResult.stay();
        }

        if (ctx.stepNo() == WAIT_PAYMENT_APPROVAL) {
            if (ctx.hasSignal(SIGNAL_PAYMENT_APPROVED)) {
                store.paymentApproved(orderId);
                StepLogger.of(PaymentStep.class, ctx).info("payment approved");
                ctx.startTimeout(PAYMENT_APPROVED_VISIBLE_MS);
                ctx.setStepNo(SHOW_PAYMENT_APPROVED);
                return StepResult.stay();
            }
            if (ctx.timedOut()) {
                store.fail(orderId);
                return StepResult.fail(new IllegalStateException("payment timeout: " + orderId));
            }
            return StepResult.stay();
        }

        if (ctx.stepNo() == SHOW_PAYMENT_APPROVED) {
            return ctx.timedOut() ? StepResult.advance() : StepResult.stay();
        }

        return StepResult.fail(new IllegalStateException("unknown payment stepNo: " + ctx.stepNo()));
    }
}
