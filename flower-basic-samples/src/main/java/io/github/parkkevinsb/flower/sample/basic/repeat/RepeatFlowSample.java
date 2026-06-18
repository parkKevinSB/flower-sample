package io.github.parkkevinsb.flower.sample.basic.repeat;

import io.github.parkkevinsb.flower.core.flow.Flow;
import io.github.parkkevinsb.flower.sample.basic.support.SampleRuntime;

public final class RepeatFlowSample {

    private RepeatFlowSample() {
    }

    public static Flow createFlow(String flowKey, int targetAttempts) {
        return Flow.builder("basic-repeat", flowKey)
                .step("repeat-step", new RepeatUntilReadyStep(targetAttempts))
                .step("after-repeat", new AfterRepeatStep())
                .build();
    }

    public static void main(String[] args) throws Exception {
        SampleRuntime.runToTerminal(createFlow("console", 3), 10L);
    }
}
