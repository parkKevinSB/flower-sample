package io.github.parkkevinsb.flower.sample.basic.guard;

import io.github.parkkevinsb.flower.sample.basic.support.RecordingFlowerListener;
import io.github.parkkevinsb.flower.sample.basic.support.RuntimeTestSupport;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GuardFlowSampleTest {

    @Test
    void guardCanHoldStepUntilItPasses() throws Exception {
        CountingGuard guard = new CountingGuard(3);

        RecordingFlowerListener listener = RuntimeTestSupport.runFlow(
                GuardFlowSample.createFlow("test", guard));

        assertThat(guard.checks()).isGreaterThanOrEqualTo(3);
        assertThat(listener.events()).containsExactly(
                "submitted basic-guard/test",
                "entered guarded-step",
                "exited guarded-step",
                "entered after-guard",
                "exited after-guard",
                "finished basic-guard/test");
    }
}
