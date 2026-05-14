package io.github.parkkevinsb.flower.sample.logistics.workflow.step;

import io.github.parkkevinsb.flower.core.step.Step;
import io.github.parkkevinsb.flower.core.step.StepContext;
import io.github.parkkevinsb.flower.core.step.StepResult;
import io.github.parkkevinsb.flower.observability.logging.StepLogger;
import io.github.parkkevinsb.flower.sample.logistics.domain.WarehouseConveyor;
import io.github.parkkevinsb.flower.sample.logistics.domain.WarehouseZone;
import io.github.parkkevinsb.flower.sample.logistics.domain.WorkOrderStatus;

public final class PlaceRackBoxOnConveyorStep extends Step {

    private enum Phase {
        START(0),
        PLACING(10),
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

    private static final long PLACE_MILLIS = 900L;

    private final WarehouseConveyor conveyor;
    private final ZoneCycleState cycle;
    private final String awaitStepId;

    public PlaceRackBoxOnConveyorStep(WarehouseConveyor conveyor, ZoneCycleState cycle, String awaitStepId) {
        this.conveyor = conveyor;
        this.cycle = cycle;
        this.awaitStepId = awaitStepId;
    }

    @Override
    protected StepResult onTick(StepContext ctx) {
        String workOrderId = cycle.workOrderId();
        if (workOrderId == null) {
            return StepResult.goTo(awaitStepId);
        }

        Phase phase = Phase.from(ctx.stepNo());
        switch (phase) {
            case START:
                conveyor.transition(workOrderId, WorkOrderStatus.ROBOT_PLACING);
                StepLogger.of(PlaceRackBoxOnConveyorStep.class, ctx).info(
                        "Rack robot placing " + workOrderId + " on the conveyor");
                ctx.startTimeout(PLACE_MILLIS);
                ctx.setStepNo(Phase.PLACING.stepNo());
                return StepResult.stay();

            case PLACING:
                if (!ctx.timedOut()) {
                    return StepResult.stay();
                }
                conveyor.release(WarehouseZone.RACK_ROBOT, workOrderId);
                cycle.clear();
                StepLogger.of(PlaceRackBoxOnConveyorStep.class, ctx).info(
                        "Rack robot released " + workOrderId + " to Inspection queue");
                return StepResult.goTo(awaitStepId);

            default:
                return StepResult.fail(new IllegalStateException("unknown rack place stepNo: " + ctx.stepNo()));
        }
    }
}
