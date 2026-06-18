package io.github.parkkevinsb.flower.sample.basic.gotoexample;

import io.github.parkkevinsb.flower.core.step.Step;
import io.github.parkkevinsb.flower.core.step.StepContext;
import io.github.parkkevinsb.flower.core.step.StepResult;
import io.github.parkkevinsb.flower.sample.basic.support.ConsoleFlowerListener;

public final class TargetStep extends Step {

    @Override
    protected StepResult onTick(StepContext ctx) {
        ConsoleFlowerListener.log("TargetStep Done!");
        return StepResult.done();
    }
}
