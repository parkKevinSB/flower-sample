package io.github.parkkevinsb.flower.sample.basic.finishfail;

import io.github.parkkevinsb.flower.core.step.Step;
import io.github.parkkevinsb.flower.core.step.StepContext;
import io.github.parkkevinsb.flower.core.step.StepResult;
import io.github.parkkevinsb.flower.sample.basic.support.ConsoleFlowerListener;

final class FinishNowStep extends Step {

    @Override
    protected StepResult onTick(StepContext ctx) {
        ConsoleFlowerListener.log("finish-now returns FINISH");
        return StepResult.finish();
    }
}
