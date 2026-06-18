package io.github.parkkevinsb.flower.sample.basic.repeat;

import io.github.parkkevinsb.flower.sample.basic.support.RecordingFlowerListener;
import io.github.parkkevinsb.flower.sample.basic.support.RuntimeTestSupport;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RepeatFlowSampleTest {

    @Test
    void repeatRunsSameStepAgainBeforeContinuing() throws Exception {
        RecordingFlowerListener listener = RuntimeTestSupport.runFlow(
                RepeatFlowSample.createFlow("test", 3));

        assertThat(listener.events()).containsExactly(
                "submitted basic-repeat/test",
                "entered repeat-step",
                "entered repeat-step",
                "entered repeat-step",
                "exited repeat-step",
                "entered after-repeat",
                "exited after-repeat",
                "finished basic-repeat/test");
    }
}
