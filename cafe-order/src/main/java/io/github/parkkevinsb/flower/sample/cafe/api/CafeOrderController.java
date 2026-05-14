package io.github.parkkevinsb.flower.sample.cafe.api;

import io.github.parkkevinsb.flower.core.flow.Flow;
import io.github.parkkevinsb.flower.sample.cafe.domain.CafeOrder;
import io.github.parkkevinsb.flower.sample.cafe.domain.CafeOrderStore;
import io.github.parkkevinsb.flower.sample.cafe.workflow.factory.CafeOrderStepCatalog;
import io.github.parkkevinsb.flower.sample.cafe.workflow.factory.CafeOrderStepDescriptor;
import io.github.parkkevinsb.flower.sample.cafe.workflow.factory.CafeOrderFlowFactory;
import io.github.parkkevinsb.flower.sample.cafe.workflow.factory.CustomCafeOrderFlowFactory;
import io.github.parkkevinsb.flower.sample.cafe.workflow.factory.PrepaidCafeOrderFlowFactory;
import io.github.parkkevinsb.flower.sample.cafe.workflow.worker.CafeOrderWorker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Entry point users see first. Demonstrates how a controller hands work off
 * to Flower:
 *
 * <ol>
 *   <li>Build a Flow via the matching Flow factory.</li>
 *   <li>Submit it to a named Worker on the Engine.</li>
 *   <li>Return immediately - the Flow runs on the Worker thread.</li>
 * </ol>
 *
 * The controller never touches Bloom or Flower internals directly.
 */
@RestController
@RequestMapping("/orders")
public class CafeOrderController {

    private final CafeOrderWorker worker;
    private final CafeOrderFlowFactory standardFlowFactory;
    private final PrepaidCafeOrderFlowFactory prepaidFlowFactory;
    private final CustomCafeOrderFlowFactory customFlowFactory;
    private final CafeOrderStepCatalog stepCatalog;
    private final CafeOrderStore store;

    public CafeOrderController(
            CafeOrderWorker worker,
            CafeOrderFlowFactory standardFlowFactory,
            PrepaidCafeOrderFlowFactory prepaidFlowFactory,
            CustomCafeOrderFlowFactory customFlowFactory,
            CafeOrderStepCatalog stepCatalog,
            CafeOrderStore store
    ) {
        this.worker = worker;
        this.standardFlowFactory = standardFlowFactory;
        this.prepaidFlowFactory = prepaidFlowFactory;
        this.customFlowFactory = customFlowFactory;
        this.stepCatalog = stepCatalog;
        this.store = store;
    }

    @GetMapping("/steps")
    public List<CafeOrderStepDescriptor> steps() {
        return stepCatalog.descriptors();
    }

    @PostMapping("/{orderId}")
    public ResponseEntity<CafeOrderResponse> submit(@PathVariable String orderId) {
        return submit(orderId, standardFlowFactory.create(orderId));
    }

    @PostMapping("/{orderId}/prepaid")
    public ResponseEntity<CafeOrderResponse> submitPrepaid(@PathVariable String orderId) {
        return submit(orderId, prepaidFlowFactory.create(orderId));
    }

    @PostMapping("/{orderId}/custom")
    public ResponseEntity<CafeOrderResponse> submitCustom(
            @PathVariable String orderId,
            @RequestBody CustomCafeOrderRequest request
    ) {
        try {
            return submit(orderId, customFlowFactory.create(orderId, request.getSteps()));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    private ResponseEntity<CafeOrderResponse> submit(String orderId, Flow flow) {
        worker.submit(flow);
        // Status here will be the snapshot right after submit - usually "ACCEPTED"
        // by the time AcceptOrderStep has run, sometimes still null on a very fast call.
        CafeOrder order = store.find(orderId);
        CafeOrderResponse body = order != null
                ? CafeOrderResponse.of(order)
                : new CafeOrderResponse(orderId, null, null);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(body);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<CafeOrderResponse> get(@PathVariable String orderId) {
        CafeOrder order = store.find(orderId);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(CafeOrderResponse.of(order));
    }

    @GetMapping
    public List<CafeOrderResponse> list() {
        return store.findAll().stream()
                .map(CafeOrderResponse::of)
                .toList();
    }
}
