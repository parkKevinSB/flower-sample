package io.github.parkkevinsb.flower.sample.battle.config;

import io.github.parkkevinsb.bloom.LocalEventBus;
import io.github.parkkevinsb.flower.bloom.BloomEventBus;
import io.github.parkkevinsb.flower.core.event.EventBus;
import io.github.parkkevinsb.flower.core.listener.FlowerListener;
import io.github.parkkevinsb.flower.observability.logging.LoggingFlowerListener;
import io.github.parkkevinsb.flower.observability.metrics.MicrometerFlowerListener;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wires Bloom and Flower together for the game-turn-battle sample.
 *
 * <p>Same shape as the other samples: two {@code EventBus} beans, one for
 * the Bloom side that REST endpoints publish into, one wrapping it for
 * Flower so Step subscriptions see the same events.
 */
@Configuration
public class FlowerBattleConfig {

    @Bean
    public io.github.parkkevinsb.bloom.EventBus bloomEventBus() {
        return LocalEventBus.create();
    }

    @Bean
    public EventBus flowerEventBus(io.github.parkkevinsb.bloom.EventBus bloom) {
        return BloomEventBus.wrap(bloom);
    }

    @Bean
    public FlowerListener flowerLoggingListener() {
        return LoggingFlowerListener.builder().build();
    }

    @Bean
    public FlowerListener flowerMetricsListener(MeterRegistry registry) {
        return new MicrometerFlowerListener(registry);
    }
}
