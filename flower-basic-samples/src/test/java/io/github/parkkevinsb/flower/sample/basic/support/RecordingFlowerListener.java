package io.github.parkkevinsb.flower.sample.basic.support;

import io.github.parkkevinsb.flower.core.flow.FlowSnapshot;
import io.github.parkkevinsb.flower.core.listener.FlowerListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public final class RecordingFlowerListener implements FlowerListener {

    private final List<String> events = Collections.synchronizedList(new ArrayList<>());
    private final CountDownLatch terminal = new CountDownLatch(1);

    @Override
    public void onFlowSubmitted(FlowSnapshot flow) {
        events.add("submitted " + flow.flowId());
    }

    @Override
    public void onStepEntered(FlowSnapshot flow, String stepId) {
        events.add("entered " + stepId);
    }

    @Override
    public void onStepExited(FlowSnapshot flow, String stepId) {
        events.add("exited " + stepId);
    }

    @Override
    public void onFlowFinished(FlowSnapshot flow) {
        events.add("finished " + flow.flowId());
        terminal.countDown();
    }

    @Override
    public void onFlowFailed(FlowSnapshot flow, Throwable cause) {
        events.add("failed " + flow.flowId());
        terminal.countDown();
    }

    @Override
    public void onFlowCancelled(FlowSnapshot flow) {
        events.add("cancelled " + flow.flowId());
        terminal.countDown();
    }

    public void awaitTerminal() throws InterruptedException {
        if (!terminal.await(5L, TimeUnit.SECONDS)) {
            throw new AssertionError("flow did not terminate");
        }
    }

    public List<String> events() {
        synchronized (events) {
            return new ArrayList<>(events);
        }
    }
}
