package io.github.parkkevinsb.flower.sample.basic.guard;

import io.github.parkkevinsb.flower.core.step.Step;
import io.github.parkkevinsb.flower.core.step.StepContext;
import io.github.parkkevinsb.flower.core.step.StepResult;
import io.github.parkkevinsb.flower.sample.basic.support.ConsoleFlowerListener;

final class AfterGuardStep extends Step {

    @Override
    protected StepResult onTick(StepContext ctx) {
        ConsoleFlowerListener.log("after-guard done");
        return StepResult.done();
    }
}
