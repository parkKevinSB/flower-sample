package io.github.parkkevinsb.flower.sample.basic.repeat;

import io.github.parkkevinsb.flower.core.step.Step;
import io.github.parkkevinsb.flower.core.step.StepContext;
import io.github.parkkevinsb.flower.core.step.StepResult;
import io.github.parkkevinsb.flower.sample.basic.support.ConsoleFlowerListener;

final class RepeatUntilReadyStep extends Step {

    private final int targetAttempts;
    private int attempts;

    RepeatUntilReadyStep(int targetAttempts) {
        if (targetAttempts < 1) {
            throw new IllegalArgumentException("targetAttempts must be positive");
        }
        this.targetAttempts = targetAttempts;
    }

    @Override
    protected StepResult onTick(StepContext ctx) {
        attempts++;
        ConsoleFlowerListener.log("repeat-step attempt " + attempts + "/" + targetAttempts);
        if (attempts < targetAttempts) {
            return StepResult.repeat();
        }
        return StepResult.done();
    }

    @Override
    protected void onReset(StepContext ctx) {
        ConsoleFlowerListener.log("repeat-step reset");
    }
}
