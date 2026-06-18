package io.github.parkkevinsb.flower.sample.basic.finishfail;

import io.github.parkkevinsb.flower.core.step.Step;
import io.github.parkkevinsb.flower.core.step.StepContext;
import io.github.parkkevinsb.flower.core.step.StepResult;

final class ShouldNotRunStep extends Step {

    @Override
    protected StepResult onTick(StepContext ctx) {
        return StepResult.fail(new IllegalStateException("this step should not run"));
    }
}
