package io.github.parkkevinsb.flower.sample.logistics.config;

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
 * Wires Bloom and Flower together for the logistics-control sample.
 *
 * <p>This sample is now self-driving: once a work order enters Flower, the
 * Zone Steps use shared robot capacity and timeouts instead of external
 * button events. The Bloom adapter is still wired so the sample collection
 * keeps the same Flower event-bus integration shape.
 */
@Configuration
public class FlowerLogisticsConfig {

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
