package io.github.parkkevinsb.flower.sample.basic.stay;

import io.github.parkkevinsb.flower.core.flow.Flow;
import io.github.parkkevinsb.flower.sample.basic.support.SampleRuntime;

public final class StayFlowSample {

    private StayFlowSample() {
    }

    public static Flow createFlow(String flowKey, long delayMillis) {
        return Flow.builder("basic-stay", flowKey)
                .step("stay-then-done", new StayThenDoneStep(delayMillis))
                .step("final-step", new FinalStep())
                .build();
    }

    public static void main(String[] args) throws Exception {
        SampleRuntime.runToTerminal(createFlow("console", 1_500L), 10L);
    }
}
