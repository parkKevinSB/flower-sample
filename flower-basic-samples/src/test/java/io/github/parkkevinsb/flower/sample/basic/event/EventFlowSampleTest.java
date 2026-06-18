package io.github.parkkevinsb.flower.sample.basic.event;

import io.github.parkkevinsb.flower.core.event.InMemoryEventBus;
import io.github.parkkevinsb.flower.sample.basic.support.RecordingFlowerListener;
import io.github.parkkevinsb.flower.sample.basic.support.RuntimeTestSupport;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventFlowSampleTest {

    @Test
    void eventFlowWaitsForPublishedEvent() throws Exception {
        InMemoryEventBus eventBus = InMemoryEventBus.create();

        RecordingFlowerListener listener = RuntimeTestSupport.runFlowAndPublish(
                EventFlowSample.createFlow("test"),
                eventBus,
                new ContinueEvent("test event"),
                100L);

        assertThat(listener.events()).containsExactly(
                "submitted basic-event/test",
                "entered wait-for-event",
                "exited wait-for-event",
                "entered event-finished",
                "exited event-finished",
                "finished basic-event/test");
    }
}
