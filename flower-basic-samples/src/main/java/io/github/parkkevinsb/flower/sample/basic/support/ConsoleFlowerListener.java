package io.github.parkkevinsb.flower.sample.basic.support;

import io.github.parkkevinsb.flower.core.flow.FlowSnapshot;
import io.github.parkkevinsb.flower.core.listener.FlowerListener;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public final class ConsoleFlowerListener implements FlowerListener {

    private final CountDownLatch terminal = new CountDownLatch(1);

    @Override
    public void onFlowSubmitted(FlowSnapshot flow) {
        log("flow submitted: " + flow.flowId());
    }

    @Override
    public void onStepEntered(FlowSnapshot flow, String stepId) {
        log("step entered: " + flow.flowId() + " -> " + stepId);
    }

    @Override
    public void onStepExited(FlowSnapshot flow, String stepId) {
        log("step exited: " + flow.flowId() + " -> " + stepId);
    }

    @Override
    public void onFlowFinished(FlowSnapshot flow) {
        log("flow finished: " + flow.flowId());
        terminal.countDown();
    }

    @Override
    public void onFlowFailed(FlowSnapshot flow, Throwable cause) {
        log("flow failed: " + flow.flowId() + " cause=" + cause);
        terminal.countDown();
    }

    @Override
    public void onFlowCancelled(FlowSnapshot flow) {
        log("flow cancelled: " + flow.flowId());
        terminal.countDown();
    }

    public boolean awaitTerminal(long timeout, TimeUnit unit) throws InterruptedException {
        return terminal.await(timeout, unit);
    }

    public static void log(String message) {
        System.out.println("[flower-basic] " + message);
    }
}
