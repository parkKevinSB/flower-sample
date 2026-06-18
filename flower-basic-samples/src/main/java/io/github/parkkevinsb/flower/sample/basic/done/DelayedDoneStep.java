package io.github.parkkevinsb.flower.sample.basic.done;

import io.github.parkkevinsb.flower.core.step.Step;
import io.github.parkkevinsb.flower.core.step.StepContext;
import io.github.parkkevinsb.flower.core.step.StepResult;
import io.github.parkkevinsb.flower.sample.basic.support.ConsoleFlowerListener;

public abstract class DelayedDoneStep extends Step {

    private static final int START = 0;
    private static final int WAITING = 10;

    private final String label;
    private final long delayMillis;

    protected DelayedDoneStep(String label, long delayMillis) {
        this.label = label;
        this.delayMillis = delayMillis;
    }

    @Override
    protected StepResult onTick(StepContext ctx) {
        switch (ctx.stepNo()) {
            case START:
                ConsoleFlowerListener.log(label + " Start!");
                ctx.startTimeout(delayMillis);
                ctx.setStepNo(WAITING);
                return StepResult.stay();

            case WAITING:
                if (ctx.timedOut()) {
                    ConsoleFlowerListener.log(label + " Done!");
                    return StepResult.done();
                }
                return StepResult.stay();

            default:
                return StepResult.fail(new IllegalStateException("unknown stepNo: " + ctx.stepNo()));
        }
    }
}
