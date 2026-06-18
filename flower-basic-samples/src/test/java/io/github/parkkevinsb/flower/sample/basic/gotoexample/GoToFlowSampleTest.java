package io.github.parkkevinsb.flower.sample.basic.gotoexample;

import io.github.parkkevinsb.flower.sample.basic.support.RecordingFlowerListener;
import io.github.parkkevinsb.flower.sample.basic.support.RuntimeTestSupport;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GoToFlowSampleTest {

    @Test
    void goToJumpsOverStepTwo() throws Exception {
        RecordingFlowerListener listener = RuntimeTestSupport.runFlow(
                GoToFlowSample.createFlow("test", 25L));

        assertThat(listener.events()).containsExactly(
                "submitted basic-goto/test",
                "entered step-1",
                "exited step-1",
                "entered step-3",
                "exited step-3",
                "finished basic-goto/test");
    }
}
