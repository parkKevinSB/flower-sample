package io.github.parkkevinsb.flower.sample.basic.done;

import io.github.parkkevinsb.flower.sample.basic.support.RecordingFlowerListener;
import io.github.parkkevinsb.flower.sample.basic.support.RuntimeTestSupport;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DoneFlowSampleTest {

    @Test
    void doneFlowRunsStepOneTwoThreeAndFinishes() throws Exception {
        RecordingFlowerListener listener = RuntimeTestSupport.runFlow(
                DoneFlowSample.createFlow("test", 25L));

        assertThat(listener.events()).containsExactly(
                "submitted basic-done/test",
                "entered step-1",
                "exited step-1",
                "entered step-2",
                "exited step-2",
                "entered step-3",
                "exited step-3",
                "finished basic-done/test");
    }
}
