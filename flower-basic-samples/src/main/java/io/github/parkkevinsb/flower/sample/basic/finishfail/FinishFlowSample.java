package io.github.parkkevinsb.flower.sample.basic.finishfail;

import io.github.parkkevinsb.flower.core.flow.Flow;
import io.github.parkkevinsb.flower.sample.basic.support.SampleRuntime;

public final class FinishFlowSample {

    private FinishFlowSample() {
    }

    public static Flow createFlow(String flowKey) {
        return Flow.builder("basic-finish", flowKey)
                .step("finish-now", new FinishNowStep())
                .step("should-not-run", new ShouldNotRunStep())
                .build();
    }

    public static void main(String[] args) throws Exception {
        SampleRuntime.runToTerminal(createFlow("console"), 10L);
    }
}
