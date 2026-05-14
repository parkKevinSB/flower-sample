package io.github.parkkevinsb.flower.sample.logistics.workflow.step;

import io.github.parkkevinsb.flower.sample.logistics.domain.WarehouseZone;

public final class PackBoxStep extends TimedZoneWorkStep {

    public PackBoxStep(ZoneCycleState cycle, String awaitStepId) {
        super(WarehouseZone.PACKING, cycle, awaitStepId, "adds dunnage, seals the box, and prints label");
    }
}
