package io.github.parkkevinsb.flower.sample.logistics;

import io.github.parkkevinsb.flower.core.engine.Engine;
import io.github.parkkevinsb.flower.sample.logistics.api.WorkOrderResponse;
import io.github.parkkevinsb.flower.sample.logistics.domain.InMemoryWorkOrderStore;
import io.github.parkkevinsb.flower.sample.logistics.domain.TruckDockStatus;
import io.github.parkkevinsb.flower.sample.logistics.domain.WarehouseConveyor;
import io.github.parkkevinsb.flower.sample.logistics.domain.WarehouseZone;
import io.github.parkkevinsb.flower.sample.logistics.domain.WorkOrder;
import io.github.parkkevinsb.flower.sample.logistics.domain.WorkOrderStatus;
import io.github.parkkevinsb.flower.sample.logistics.domain.WorkOrderStore;
import io.github.parkkevinsb.flower.sample.logistics.workflow.factory.WarehouseZoneFlowFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.Arrays;
import java.time.Duration;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LogisticsControlSampleApplicationTest {

    @Autowired
    TestRestTemplate rest;

    @Autowired
    WorkOrderStore store;

    @Autowired
    Engine engine;

    @Test
    void startsOneLongRunningFlowPerWarehouseZone() throws Exception {
        awaitCondition("zone flows to be active", 2_000L, () ->
                Arrays.stream(WarehouseZone.values()).allMatch(zone ->
                        engine.worker(zone.workerName()).snapshot().stream().anyMatch(flow ->
                                flow.flowId().flowType().equals(WarehouseZoneFlowFactory.FLOW_TYPE)
                                        && flow.flowId().flowKey().equals(zone.id()))));
    }

    @Test
    void workOrderAdvancesThroughAutomatedZones() throws Exception {
        String id = "WO-AUTO-1";

        WorkOrderResponse response = rest.postForEntity(
                "/work-orders/" + id,
                null,
                WorkOrderResponse.class).getBody();

        assertThat(response).isNotNull();
        assertThat(response.getWorkOrderId()).isEqualTo(id);

        awaitStatus(id, WorkOrderStatus.COMPLETED, 24_000L);

        WorkOrder workOrder = store.find(id);
        assertThat(workOrder).isNotNull();
        assertThat(workOrder.getStatus()).isEqualTo(WorkOrderStatus.COMPLETED);
    }

    @Test
    void multipleWorkOrdersQueueBehindBusyZones() throws Exception {
        String[] ids = {"WO-QUEUE-1", "WO-QUEUE-2", "WO-QUEUE-3"};

        for (String id : ids) {
            rest.postForEntity("/work-orders/" + id, null, WorkOrderResponse.class);
        }

        awaitCondition("orders to queue at the rack robot", 2_000L, () -> {
            Set<WorkOrderStatus> statuses = statuses(ids);
            return (statuses.contains(WorkOrderStatus.ROBOT_GRIPPING)
                            || statuses.contains(WorkOrderStatus.ROBOT_PLACING))
                    && statuses.contains(WorkOrderStatus.WAITING_RACK_ROBOT);
        });

        for (String id : ids) {
            awaitStatus(id, WorkOrderStatus.COMPLETED, 45_000L);
        }
    }

    @Test
    void postOnlyNeedsAWorkOrderId() throws Exception {
        String id = "WO-MINIMAL";

        WorkOrderResponse response = rest.postForEntity(
                "/work-orders/" + id,
                null,
                WorkOrderResponse.class).getBody();

        assertThat(response).isNotNull();
        assertThat(response.getWorkOrderId()).isEqualTo(id);

        awaitCondition("work order to be stored", 2_000L, () -> store.find(id) != null);
        WorkOrder workOrder = store.find(id);
        assertThat(workOrder.getWorkOrderId()).isEqualTo(id);
    }

    @Test
    void goodsRackRejectsNewOrdersWhenEightBoxesAreWaiting() {
        WarehouseConveyor conveyor = new WarehouseConveyor(new InMemoryWorkOrderStore());

        for (int index = 1; index <= WarehouseConveyor.GOODS_RACK_CAPACITY; index += 1) {
            conveyor.submitOrder("WO-RACK-" + index);
        }

        assertThatThrownBy(() -> conveyor.submitOrder("WO-RACK-9"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Goods rack is full");
    }

    @Test
    void loadingWaitsWhenTruckIsFullAndUntilNextTruckArrives() throws Exception {
        WarehouseConveyor conveyor = new WarehouseConveyor(
                new InMemoryWorkOrderStore(),
                Duration.ofMillis(150));

        for (int index = 1; index <= WarehouseConveyor.TRUCK_CAPACITY; index += 1) {
            completeIntoCurrentTruck(conveyor, "WO-TRUCK-" + index);
        }
        moveToLoadingQueue(conveyor, "WO-TRUCK-WAIT");

        assertThat(conveyor.admit(WarehouseZone.LOADING, WorkOrderStatus.LOADING_TRUCK)).isNull();
        assertThat(conveyor.departTruck().getStatus()).isEqualTo(TruckDockStatus.AWAITING_TRUCK);
        assertThat(conveyor.admit(WarehouseZone.LOADING, WorkOrderStatus.LOADING_TRUCK)).isNull();

        Thread.sleep(200L);

        assertThat(conveyor.truckDock().getStatus()).isEqualTo(TruckDockStatus.DOCKED);
        assertThat(conveyor.admit(WarehouseZone.LOADING, WorkOrderStatus.LOADING_TRUCK))
                .isEqualTo("WO-TRUCK-WAIT");
    }

    @Test
    void getReturns404ForUnknownWorkOrder() {
        assertThat(rest.getForEntity("/work-orders/NOPE", WorkOrderResponse.class)
                .getStatusCode()
                .value())
                .isEqualTo(404);
    }

    private void awaitStatus(String workOrderId, WorkOrderStatus expected, long timeoutMillis)
            throws InterruptedException {
        awaitCondition(
                "work order " + workOrderId + " to reach " + expected,
                timeoutMillis,
                () -> {
                    WorkOrder workOrder = store.find(workOrderId);
                    return workOrder != null && workOrder.getStatus() == expected;
                });
    }

    private void awaitCondition(String label, long timeoutMillis, Condition condition) throws InterruptedException {
        long deadline = System.currentTimeMillis() + timeoutMillis;
        while (System.currentTimeMillis() < deadline) {
            if (condition.matches()) {
                return;
            }
            Thread.sleep(50L);
        }
        throw new AssertionError("timed out waiting for " + label);
    }

    private Set<WorkOrderStatus> statuses(String[] workOrderIds) {
        return Arrays.stream(workOrderIds)
                .map(store::find)
                .filter(order -> order != null)
                .map(WorkOrder::getStatus)
                .collect(java.util.stream.Collectors.toSet());
    }

    private void completeIntoCurrentTruck(WarehouseConveyor conveyor, String workOrderId) {
        moveToLoadingQueue(conveyor, workOrderId);
        assertThat(conveyor.admit(WarehouseZone.LOADING, WorkOrderStatus.LOADING_TRUCK))
                .isEqualTo(workOrderId);
        conveyor.release(WarehouseZone.LOADING, workOrderId);
    }

    private void moveToLoadingQueue(WarehouseConveyor conveyor, String workOrderId) {
        conveyor.submitOrder(workOrderId);
        admitAndRelease(conveyor, WarehouseZone.RACK_ROBOT, workOrderId);
        admitAndRelease(conveyor, WarehouseZone.INSPECTION, workOrderId);
        admitAndRelease(conveyor, WarehouseZone.PACKING, workOrderId);
        admitAndRelease(conveyor, WarehouseZone.SORTATION, workOrderId);
    }

    private void admitAndRelease(WarehouseConveyor conveyor, WarehouseZone zone, String workOrderId) {
        assertThat(conveyor.admit(zone, zone.processingStatus())).isEqualTo(workOrderId);
        conveyor.release(zone, workOrderId);
    }

    @FunctionalInterface
    private interface Condition {
        boolean matches();
    }
}
