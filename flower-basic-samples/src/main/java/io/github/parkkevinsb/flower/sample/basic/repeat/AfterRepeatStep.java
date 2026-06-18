package io.github.parkkevinsb.flower.sample.basic.repeat;

import io.github.parkkevinsb.flower.core.step.Step;
import io.github.parkkevinsb.flower.core.step.StepContext;
import io.github.parkkevinsb.flower.core.step.StepResult;
import io.github.parkkevinsb.flower.sample.basic.support.ConsoleFlowerListener;

final class AfterRepeatStep extends Step {

    @Override
    protected StepResult onTick(StepContext ctx) {
        ConsoleFlowerListener.log("after-repeat done");
        return StepResult.done();
    }
}
