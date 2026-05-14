package io.github.parkkevinsb.flower.sample.cafe.partner;

import io.github.parkkevinsb.bloom.EventBus;
import io.github.parkkevinsb.flower.sample.cafe.event.PaymentApprovedEvent;
import io.github.parkkevinsb.flower.sample.cafe.event.PaymentRequestedEvent;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Sample-only payment partner.
 *
 * <p>In a real service this class could send an HTTP request and a webhook
 * handler would publish {@link PaymentApprovedEvent}. The sample keeps it
 * in-process so the Bloom publish/subscribe shape is easy to see.
 */
@Component
public final class PaymentGateway {

    private static final long APPROVAL_DELAY_MS = 3_000L;

    private final EventBus events;
    private final TaskScheduler scheduler;

    public PaymentGateway(EventBus events, TaskScheduler scheduler) {
        this.events = events;
        this.scheduler = scheduler;
        events.subscribe(PaymentRequestedEvent.class, this::approve);
    }

    private void approve(PaymentRequestedEvent event) {
        scheduler.schedule(
                () -> events.publish(new PaymentApprovedEvent(event.getOrderId())),
                Instant.now().plusMillis(APPROVAL_DELAY_MS));
    }
}
