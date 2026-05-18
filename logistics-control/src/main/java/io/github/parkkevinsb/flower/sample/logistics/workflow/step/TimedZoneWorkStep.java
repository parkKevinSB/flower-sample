package io.github.parkkevinsb.flower.sample.logistics.workflow.step;

import io.github.parkkevinsb.flower.core.step.Step;
import io.github.parkkevinsb.flower.core.step.StepContext;
import io.github.parkkevinsb.flower.core.step.StepResult;
import io.github.parkkevinsb.flower.observability.logging.StepLogger;
import io.github.parkkevinsb.flower.sample.logistics.domain.WarehouseZone;

public abstract class TimedZoneWorkStep extends Step {

    private enum Phase {
        START(0),
        WORKING(10),
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

    private final WarehouseZone zone;
    private final ZoneCycleState cycle;
    private final String awaitStepId;
    private final String action;

    protected TimedZoneWorkStep(
            WarehouseZone zone,
            ZoneCycleState cycle,
            String awaitStepId,
            String action
    ) {
        this.zone = zone;
        this.cycle = cycle;
        this.awaitStepId = awaitStepId;
        this.action = action;
    }

    @Override
    protected StepResult onTick(StepContext ctx) {
        if (cycle.workOrderId() == null) {
            return StepResult.goTo(awaitStepId);
        }

        Phase phase = Phase.from(ctx.stepNo());
        switch (phase) {
            case START:
                StepLogger.of(getClass(), ctx).info(
                        zone.displayName() + " " + action + " for " + cycle.workOrderId());
                ctx.startTimeout(zone.processingMillis());
                ctx.setStepNo(Phase.WORKING.stepNo());
                return StepResult.stay();

            case WORKING:
                return ctx.timedOut() ? StepResult.done() : StepResult.stay();

            default:
                return StepResult.fail(new IllegalStateException("unknown timed Zone stepNo: " + ctx.stepNo()));
        }
    }
}
