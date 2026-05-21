package io.github.parkkevinsb.flower.sample.logistics.workflow.step;

import io.github.parkkevinsb.flower.core.step.Step;
import io.github.parkkevinsb.flower.core.step.StepContext;
import io.github.parkkevinsb.flower.core.step.StepResult;
import io.github.parkkevinsb.flower.observability.logging.StepLogger;
import io.github.parkkevinsb.flower.sample.logistics.domain.WarehouseConveyor;
import io.github.parkkevinsb.flower.sample.logistics.domain.WarehouseZone;

public final class AwaitZoneBoxStep extends Step {

    private final WarehouseConveyor conveyor;
    private final WarehouseZone zone;
    private final ZoneCycleState cycle;

    public AwaitZoneBoxStep(WarehouseConveyor conveyor, WarehouseZone zone, ZoneCycleState cycle) {
        this.conveyor = conveyor;
        this.zone = zone;
        this.cycle = cycle;
    }

    @Override
    protected StepResult onTick(StepContext ctx) {
        if (cycle.workOrderId() != null) {
            return StepResult.done();
        }

        String workOrderId = conveyor.admit(zone, zone.processingStatus());
        if (workOrderId != null) {
            cycle.admit(workOrderId);
            StepLogger.of(AwaitZoneBoxStep.class, ctx).info(
                    zone.displayName() + " admitted " + workOrderId);
            return StepResult.done();
        }

        return StepResult.stay();
    }
}
