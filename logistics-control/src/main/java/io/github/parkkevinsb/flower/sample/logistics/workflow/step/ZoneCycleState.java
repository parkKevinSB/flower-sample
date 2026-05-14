package io.github.parkkevinsb.flower.sample.logistics.workflow.step;

public final class ZoneCycleState {

    private String workOrderId;

    public String workOrderId() {
        return workOrderId;
    }

    public void admit(String workOrderId) {
        this.workOrderId = workOrderId;
    }

    public void clear() {
        this.workOrderId = null;
    }
}
