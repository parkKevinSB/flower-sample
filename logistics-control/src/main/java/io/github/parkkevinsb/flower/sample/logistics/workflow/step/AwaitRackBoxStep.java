package io.github.parkkevinsb.flower.sample.logistics.workflow.step;

import io.github.parkkevinsb.flower.core.step.Step;
import io.github.parkkevinsb.flower.core.step.StepContext;
import io.github.parkkevinsb.flower.core.step.StepResult;
import io.github.parkkevinsb.flower.observability.logging.StepLogger;
import io.github.parkkevinsb.flower.sample.logistics.domain.WarehouseConveyor;
import io.github.parkkevinsb.flower.sample.logistics.domain.WarehouseZone;
import io.github.parkkevinsb.flower.sample.logistics.domain.WorkOrderStatus;

public final class AwaitRackBoxStep extends Step {

    private final WarehouseConveyor conveyor;
    private final ZoneCycleState cycle;

    public AwaitRackBoxStep(WarehouseConveyor conveyor, ZoneCycleState cycle) {
        this.conveyor = conveyor;
        this.cycle = cycle;
    }

    @Override
    protected StepResult onTick(StepContext ctx) {
        if (cycle.workOrderId() != null) {
            return StepResult.done();
        }

        String workOrderId = conveyor.admit(WarehouseZone.RACK_ROBOT, WorkOrderStatus.ROBOT_GRIPPING);
        if (workOrderId == null) {
            return StepResult.stay();
        }

        cycle.admit(workOrderId);
        StepLogger.of(AwaitRackBoxStep.class, ctx).info(
                "Rack robot selected first waiting Goods rack box " + workOrderId);
        return StepResult.done();
    }
}
