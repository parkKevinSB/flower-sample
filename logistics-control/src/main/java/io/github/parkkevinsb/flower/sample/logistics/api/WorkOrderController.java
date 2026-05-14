package io.github.parkkevinsb.flower.sample.logistics.api;

import io.github.parkkevinsb.flower.sample.logistics.domain.WorkOrder;
import io.github.parkkevinsb.flower.sample.logistics.domain.WarehouseConveyor;
import io.github.parkkevinsb.flower.sample.logistics.domain.WorkOrderStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Entry point for the logistics-control sample.
 *
 * <p>The controller only accepts new work orders and reads their state. It
 * does not create a Flow per order; instead it creates a box on the shared
 * conveyor. The always-running Zone Flows will pick it up.
 */
@RestController
@RequestMapping("/work-orders")
public class WorkOrderController {

    private final WarehouseConveyor conveyor;
    private final WorkOrderStore store;

    public WorkOrderController(
            WarehouseConveyor conveyor,
            WorkOrderStore store
    ) {
        this.conveyor = conveyor;
        this.store = store;
    }

    @PostMapping("/{workOrderId}")
    public ResponseEntity<WorkOrderResponse> submit(@PathVariable String workOrderId) {
        try {
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(WorkOrderResponse.of(conveyor.submitOrder(workOrderId)));
        } catch (IllegalStateException error) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, error.getMessage(), error);
        }
    }

    @GetMapping("/{workOrderId}")
    public ResponseEntity<WorkOrderResponse> get(@PathVariable String workOrderId) {
        WorkOrder workOrder = store.find(workOrderId);
        if (workOrder == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(WorkOrderResponse.of(workOrder));
    }

    @GetMapping
    public List<WorkOrderResponse> list() {
        return store.findAll().stream()
                .map(WorkOrderResponse::of)
                .toList();
    }
}
