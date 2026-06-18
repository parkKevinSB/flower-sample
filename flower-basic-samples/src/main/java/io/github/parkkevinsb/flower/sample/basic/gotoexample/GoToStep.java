package io.github.parkkevinsb.flower.sample.basic.gotoexample;

import io.github.parkkevinsb.flower.core.step.Step;
import io.github.parkkevinsb.flower.core.step.StepContext;
import io.github.parkkevinsb.flower.core.step.StepResult;
import io.github.parkkevinsb.flower.sample.basic.support.ConsoleFlowerListener;

public final class GoToStep extends Step {

    private static final int START = 0;
    private static final int WAITING = 10;

    private final String targetStepId;
    private final long delayMillis;

    public GoToStep(String targetStepId, long delayMillis) {
        this.targetStepId = targetStepId;
        this.delayMillis = delayMillis;
    }

    @Override
    protected StepResult onTick(StepContext ctx) {
        switch (ctx.stepNo()) {
            case START:
                ConsoleFlowerListener.log("GoToStep Start! target=" + targetStepId);
                ctx.startTimeout(delayMillis);
                ctx.setStepNo(WAITING);
                return StepResult.stay();

            case WAITING:
                if (ctx.timedOut()) {
                    ConsoleFlowerListener.log("GoToStep jumping to " + targetStepId);
                    return StepResult.goTo(targetStepId);
                }
                return StepResult.stay();

            default:
                return StepResult.fail(new IllegalStateException("unknown stepNo: " + ctx.stepNo()));
        }
    }
}
