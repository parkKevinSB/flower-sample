package io.github.parkkevinsb.flower.sample.basic.support;

import io.github.parkkevinsb.flower.core.engine.Engine;
import io.github.parkkevinsb.flower.core.event.EventBus;
import io.github.parkkevinsb.flower.core.event.InMemoryEventBus;
import io.github.parkkevinsb.flower.core.flow.Flow;
import io.github.parkkevinsb.flower.core.listener.FlowerListener;
import io.github.parkkevinsb.flower.core.worker.Worker;

import java.util.concurrent.TimeUnit;

public final class SampleRuntime {

    private SampleRuntime() {
    }

    public static void runToTerminal(Flow flow, long timeoutSeconds) throws Exception {
        runToTerminal(flow, InMemoryEventBus.create(), timeoutSeconds);
    }

    public static void runToTerminal(Flow flow, EventBus eventBus, long timeoutSeconds) throws Exception {
        ConsoleFlowerListener listener = new ConsoleFlowerListener();
        Worker worker = Worker.builder("basic-worker")
                .intervalMillis(100L)
                .build();
        Engine engine = Engine.builder()
                .eventBus(eventBus)
                .listener(listener)
                .worker(worker)
                .build();

        engine.start();
        try {
            worker.submit(flow);
            if (!listener.awaitTerminal(timeoutSeconds, TimeUnit.SECONDS)) {
                throw new IllegalStateException("sample did not finish within " + timeoutSeconds + " seconds");
            }
        } finally {
            engine.stop();
        }
    }

    public static Engine engine(Worker worker, EventBus eventBus, FlowerListener listener) {
        return Engine.builder()
                .eventBus(eventBus)
                .listener(listener)
                .worker(worker)
                .build();
    }
}
