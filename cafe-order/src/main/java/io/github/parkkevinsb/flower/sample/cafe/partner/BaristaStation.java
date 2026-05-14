package io.github.parkkevinsb.flower.sample.cafe.partner;

import io.github.parkkevinsb.bloom.EventBus;
import io.github.parkkevinsb.flower.sample.cafe.event.BrewRequestedEvent;
import io.github.parkkevinsb.flower.sample.cafe.event.CoffeeReadyEvent;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Sample-only barista station that replies to brew requests.
 */
@Component
public final class BaristaStation {

    private static final long BREW_DELAY_MS = 5_000L;

    private final EventBus events;
    private final TaskScheduler scheduler;

    public BaristaStation(EventBus events, TaskScheduler scheduler) {
        this.events = events;
        this.scheduler = scheduler;
        events.subscribe(BrewRequestedEvent.class, this::finishCoffee);
    }

    private void finishCoffee(BrewRequestedEvent event) {
        scheduler.schedule(
                () -> events.publish(new CoffeeReadyEvent(event.getOrderId())),
                Instant.now().plusMillis(BREW_DELAY_MS));
    }
}
