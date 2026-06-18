package io.github.parkkevinsb.flower.sample.basic.finishfail;

import io.github.parkkevinsb.flower.core.flow.Flow;
import io.github.parkkevinsb.flower.sample.basic.support.SampleRuntime;

public final class FailFlowSample {

    private FailFlowSample() {
    }

    public static Flow createFlow(String flowKey) {
        return Flow.builder("basic-fail", flowKey)
                .step("fail-now", new FailNowStep())
                .step("should-not-run", new ShouldNotRunStep())
                .build();
    }

    public static void main(String[] args) throws Exception {
        SampleRuntime.runToTerminal(createFlow("console"), 10L);
    }
}
