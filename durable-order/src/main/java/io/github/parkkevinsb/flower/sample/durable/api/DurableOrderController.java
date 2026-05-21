package io.github.parkkevinsb.flower.sample.durable.api;

import io.github.parkkevinsb.flower.core.engine.EngineDump;
import io.github.parkkevinsb.flower.core.engine.Engine;
import io.github.parkkevinsb.flower.core.engine.EngineState;
import io.github.parkkevinsb.flower.core.flow.FlowSnapshot;
import io.github.parkkevinsb.flower.core.persistence.FlowCheckpoint;
import io.github.parkkevinsb.flower.core.persistence.FlowCheckpointStore;
import io.github.parkkevinsb.flower.sample.durable.domain.AuditEvent;
import io.github.parkkevinsb.flower.sample.durable.domain.OrderRecord;
import io.github.parkkevinsb.flower.sample.durable.domain.OrderRepository;
import io.github.parkkevinsb.flower.sample.durable.workflow.DurableOrderWorker;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class DurableOrderController {

    private final OrderRepository orders;
    private final Engine engine;
    private final DurableOrderWorker worker;
    private final FlowCheckpointStore checkpointStore;

    public DurableOrderController(
            OrderRepository orders,
            Engine engine,
            DurableOrderWorker worker,
            FlowCheckpointStore checkpointStore) {
        this.orders = orders;
        this.engine = engine;
        this.worker = worker;
        this.checkpointStore = checkpointStore;
    }

    @GetMapping("/state")
    public DashboardResponse state() {
        EngineDump dump = engine.dump();
        return new DashboardResponse(
                engine.state() == EngineState.RUNNING,
                dump.engineState().name(),
                activeFlows(dump),
                orders.findAll(),
                orders.auditEvents(),
                checkpoints());
    }

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public DashboardResponse startOrder(@RequestBody(required = false) StartOrderRequest request) {
        String orderId = request != null && request.getOrderId() != null && !request.getOrderId().isBlank()
                ? request.getOrderId().trim()
                : "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        orders.createOrder(orderId);
        worker.submitOrder(orderId);
        return state();
    }

    @PostMapping("/orders/{orderId}/pay")
    public DashboardResponse pay(@PathVariable String orderId) {
        requireOrder(orderId);
        orders.markPaid(orderId);
        return state();
    }

    @PostMapping("/audits")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public DashboardResponse startAudit(@RequestBody(required = false) StartAuditRequest request) {
        String message = request != null && request.getMessage() != null && !request.getMessage().isBlank()
                ? request.getMessage().trim()
                : "operator checked durable order board";
        worker.submitAudit(message);
        return state();
    }

    @PostMapping("/reset")
    public DashboardResponse reset() {
        worker.cancelAll();
        orders.resetAll();
        return state();
    }

    private void requireOrder(String orderId) {
        if (orders.find(orderId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "unknown order: " + orderId);
        }
    }

    private List<ActiveFlowResponse> activeFlows(EngineDump dump) {
        List<ActiveFlowResponse> out = new ArrayList<>();
        if (dump == null) {
            return out;
        }
        for (EngineDump.WorkerDump worker : dump.workers()) {
            for (FlowSnapshot flow : worker.flows()) {
                out.add(ActiveFlowResponse.of(worker.name(), flow));
            }
        }
        return out;
    }

    private List<CheckpointResponse> checkpoints() {
        List<CheckpointResponse> out = new ArrayList<>();
        for (FlowCheckpoint checkpoint : checkpointStore.findActive()) {
            out.add(CheckpointResponse.of(checkpoint));
        }
        return out;
    }

    public static final class DashboardResponse {
        private final boolean runtimeRunning;
        private final String engineState;
        private final List<ActiveFlowResponse> activeFlows;
        private final List<OrderRecord> orders;
        private final List<AuditEvent> auditEvents;
        private final List<CheckpointResponse> checkpoints;

        DashboardResponse(
                boolean runtimeRunning,
                String engineState,
                List<ActiveFlowResponse> activeFlows,
                List<OrderRecord> orders,
                List<AuditEvent> auditEvents,
                List<CheckpointResponse> checkpoints) {
            this.runtimeRunning = runtimeRunning;
            this.engineState = engineState;
            this.activeFlows = activeFlows;
            this.orders = orders;
            this.auditEvents = auditEvents;
            this.checkpoints = checkpoints;
        }

        public boolean isRuntimeRunning() {
            return runtimeRunning;
        }

        public String getEngineState() {
            return engineState;
        }

        public List<ActiveFlowResponse> getActiveFlows() {
            return activeFlows;
        }

        public List<OrderRecord> getOrders() {
            return orders;
        }

        public List<AuditEvent> getAuditEvents() {
            return auditEvents;
        }

        public List<CheckpointResponse> getCheckpoints() {
            return checkpoints;
        }
    }

    public static final class ActiveFlowResponse {
        private final String workerName;
        private final String flowType;
        private final String flowKey;
        private final String state;
        private final String currentStepId;
        private final int currentStepNo;

        private ActiveFlowResponse(
                String workerName,
                String flowType,
                String flowKey,
                String state,
                String currentStepId,
                int currentStepNo) {
            this.workerName = workerName;
            this.flowType = flowType;
            this.flowKey = flowKey;
            this.state = state;
            this.currentStepId = currentStepId;
            this.currentStepNo = currentStepNo;
        }

        static ActiveFlowResponse of(String workerName, FlowSnapshot flow) {
            return new ActiveFlowResponse(
                    workerName,
                    flow.flowId().flowType(),
                    flow.flowId().flowKey(),
                    flow.state().name(),
                    flow.currentStepId(),
                    flow.currentStepNo());
        }

        public String getWorkerName() {
            return workerName;
        }

        public String getFlowType() {
            return flowType;
        }

        public String getFlowKey() {
            return flowKey;
        }

        public String getState() {
            return state;
        }

        public String getCurrentStepId() {
            return currentStepId;
        }

        public int getCurrentStepNo() {
            return currentStepNo;
        }
    }

    public static final class CheckpointResponse {
        private final String flowType;
        private final String flowKey;
        private final String state;
        private final String currentStepId;
        private final int currentStepNo;
        private final boolean currentStepEntered;
        private final String workerName;
        private final String definitionVersion;

        private CheckpointResponse(
                String flowType,
                String flowKey,
                String state,
                String currentStepId,
                int currentStepNo,
                boolean currentStepEntered,
                String workerName,
                String definitionVersion) {
            this.flowType = flowType;
            this.flowKey = flowKey;
            this.state = state;
            this.currentStepId = currentStepId;
            this.currentStepNo = currentStepNo;
            this.currentStepEntered = currentStepEntered;
            this.workerName = workerName;
            this.definitionVersion = definitionVersion;
        }

        static CheckpointResponse of(FlowCheckpoint checkpoint) {
            return new CheckpointResponse(
                    checkpoint.flowId().flowType(),
                    checkpoint.flowId().flowKey(),
                    checkpoint.state().name(),
                    checkpoint.currentStepId(),
                    checkpoint.currentStepNo(),
                    checkpoint.currentStepEntered(),
                    checkpoint.workerName(),
                    checkpoint.definitionVersion());
        }

        public String getFlowType() {
            return flowType;
        }

        public String getFlowKey() {
            return flowKey;
        }

        public String getState() {
            return state;
        }

        public String getCurrentStepId() {
            return currentStepId;
        }

        public int getCurrentStepNo() {
            return currentStepNo;
        }

        public boolean isCurrentStepEntered() {
            return currentStepEntered;
        }

        public String getWorkerName() {
            return workerName;
        }

        public String getDefinitionVersion() {
            return definitionVersion;
        }
    }

    public static final class StartOrderRequest {
        private String orderId;

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }
    }

    public static final class StartAuditRequest {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
