package io.github.parkkevinsb.flower.sample.basic.gotoexample;

import io.github.parkkevinsb.flower.core.flow.Flow;
import io.github.parkkevinsb.flower.sample.basic.support.SampleRuntime;

public final class GoToFlowSample {

    private GoToFlowSample() {
    }

    public static Flow createFlow(String flowKey, long delayMillis) {
        return Flow.builder("basic-goto", flowKey)
                .step("step-1", new GoToStep("step-3", delayMillis))
                .step("step-2", new SkippedStep())
                .step("step-3", new TargetStep())
                .build();
    }

    public static void main(String[] args) throws Exception {
        SampleRuntime.runToTerminal(createFlow("console", 900L), 10L);
    }
}
