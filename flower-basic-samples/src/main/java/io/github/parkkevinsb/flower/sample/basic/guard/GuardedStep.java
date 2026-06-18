package io.github.parkkevinsb.flower.sample.basic.guard;

import io.github.parkkevinsb.flower.core.step.Step;
import io.github.parkkevinsb.flower.core.step.StepContext;
import io.github.parkkevinsb.flower.core.step.StepResult;
import io.github.parkkevinsb.flower.sample.basic.support.ConsoleFlowerListener;

final class GuardedStep extends Step {

    @Override
    protected StepResult onTick(StepContext ctx) {
        ConsoleFlowerListener.log("guarded-step runs after guard pass");
        return StepResult.done();
    }
}
