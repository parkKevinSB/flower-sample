package io.github.parkkevinsb.flower.sample.logistics.workflow.step;

import io.github.parkkevinsb.flower.core.step.Step;
import io.github.parkkevinsb.flower.core.step.StepContext;
import io.github.parkkevinsb.flower.core.step.StepResult;
import io.github.parkkevinsb.flower.observability.logging.StepLogger;

public final class GripRackBoxStep extends Step {

    private enum Phase {
        START(0),
        GRIPPING(10),
        UNKNOWN(-1);

        private final int stepNo;

        Phase(int stepNo) {
            this.stepNo = stepNo;
        }

        int stepNo() {
            return stepNo;
        }

        static Phase from(int stepNo) {
            for (Phase phase : values()) {
                if (phase.stepNo == stepNo) {
                    return phase;
                }
            }
            return UNKNOWN;
        }
    }

    private static final long GRIP_MILLIS = 800L;

    private final ZoneCycleState cycle;
    private final String awaitStepId;

    public GripRackBoxStep(ZoneCycleState cycle, String awaitStepId) {
        this.cycle = cycle;
        this.awaitStepId = awaitStepId;
    }

    @Override
    protected StepResult onTick(StepContext ctx) {
        if (cycle.workOrderId() == null) {
            return StepResult.goTo(awaitStepId);
        }

        Phase phase = Phase.from(ctx.stepNo());
        switch (phase) {
            case START:
                StepLogger.of(GripRackBoxStep.class, ctx).info(
                        "Rack robot gripping Goods rack box " + cycle.workOrderId());
                ctx.startTimeout(GRIP_MILLIS);
                ctx.setStepNo(Phase.GRIPPING.stepNo());
                break;

            case GRIPPING:
                if (ctx.timedOut()) {
                    return StepResult.done();
                }
                break;

            default:
                return StepResult.fail(new IllegalStateException("unknown rack grip stepNo: " + ctx.stepNo()));
        }

        return StepResult.stay();
    }
}
