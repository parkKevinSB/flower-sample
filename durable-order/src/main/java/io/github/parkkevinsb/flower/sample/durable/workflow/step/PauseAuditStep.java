package io.github.parkkevinsb.flower.sample.durable.workflow.step;

import io.github.parkkevinsb.flower.core.step.Step;
import io.github.parkkevinsb.flower.core.step.StepContext;
import io.github.parkkevinsb.flower.core.step.StepResult;

public final class PauseAuditStep extends Step {

    private static final int START = 0;
    private static final int WAITING = 10;
    private static final long PAUSE_MILLIS = 5_000L;

    @Override
    protected StepResult onTick(StepContext ctx) {
        if (ctx.stepNo() == START) {
            ctx.startTimeout(PAUSE_MILLIS);
            ctx.setStepNo(WAITING);
            return StepResult.stay();
        }
        if (ctx.stepNo() == WAITING) {
            return ctx.timedOut() ? StepResult.done() : StepResult.stay();
        }
        return StepResult.fail(new IllegalStateException("unknown audit pause stepNo: " + ctx.stepNo()));
    }
}
