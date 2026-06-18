package io.github.parkkevinsb.flower.sample.basic.done;

import io.github.parkkevinsb.flower.core.flow.Flow;
import io.github.parkkevinsb.flower.sample.basic.support.SampleRuntime;

public final class DoneFlowSample {

    private DoneFlowSample() {
    }

    public static Flow createFlow(String flowKey, long delayMillis) {
        return Flow.builder("basic-done", flowKey)
                .step("step-1", new StepOne(delayMillis))
                .step("step-2", new StepTwo(delayMillis))
                .step("step-3", new StepThree(delayMillis))
                .build();
    }

    public static void main(String[] args) throws Exception {
        SampleRuntime.runToTerminal(createFlow("console", 700L), 10L);
    }
}
