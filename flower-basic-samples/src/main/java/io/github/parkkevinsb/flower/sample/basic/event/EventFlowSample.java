package io.github.parkkevinsb.flower.sample.basic.event;

import io.github.parkkevinsb.flower.core.engine.Engine;
import io.github.parkkevinsb.flower.core.event.InMemoryEventBus;
import io.github.parkkevinsb.flower.core.flow.Flow;
import io.github.parkkevinsb.flower.core.worker.Worker;
import io.github.parkkevinsb.flower.sample.basic.support.ConsoleFlowerListener;

import java.util.concurrent.TimeUnit;

public final class EventFlowSample {

    private EventFlowSample() {
    }

    public static Flow createFlow(String flowKey) {
        return Flow.builder("basic-event", flowKey)
                .step("wait-for-event", new WaitForEventStep())
                .step("event-finished", new EventFinishedStep())
                .build();
    }

    public static void main(String[] args) throws Exception {
        InMemoryEventBus eventBus = InMemoryEventBus.create();
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
            worker.submit(createFlow("console"));
            Thread.sleep(1_000L);
            ConsoleFlowerListener.log("publishing ContinueEvent");
            eventBus.publish(new ContinueEvent("console event"));
            if (!listener.awaitTerminal(10L, TimeUnit.SECONDS)) {
                throw new IllegalStateException("sample did not finish");
            }
        } finally {
            engine.stop();
        }
    }
}
