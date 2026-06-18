package io.github.parkkevinsb.flower.sample.basic.event;

import io.github.parkkevinsb.flower.core.event.EventHandler;
import io.github.parkkevinsb.flower.core.step.Step;
import io.github.parkkevinsb.flower.core.step.StepContext;
import io.github.parkkevinsb.flower.core.step.StepResult;
import io.github.parkkevinsb.flower.sample.basic.support.ConsoleFlowerListener;

public final class WaitForEventStep extends Step {

    private static final String CONTINUE_SIGNAL = "continue";

    @Override
    protected void onEnter(StepContext ctx) {
        ConsoleFlowerListener.log("WaitForEventStep subscribed and waiting");
        ctx.subscribe(ContinueEvent.class, new ContinueEventHandler(ctx));
    }

    @Override
    protected StepResult onTick(StepContext ctx) {
        String message = ctx.consumeSignal(CONTINUE_SIGNAL, String.class);
        if (message == null) {
            return StepResult.stay();
        }
        ConsoleFlowerListener.log("WaitForEventStep Done! message=" + message);
        return StepResult.done();
    }

    private static final class ContinueEventHandler implements EventHandler<ContinueEvent> {

        private final StepContext ctx;

        private ContinueEventHandler(StepContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void handle(ContinueEvent event) {
            ConsoleFlowerListener.log("WaitForEventStep received event: " + event.message());
            ctx.signal(CONTINUE_SIGNAL, event.message());
        }
    }
}
