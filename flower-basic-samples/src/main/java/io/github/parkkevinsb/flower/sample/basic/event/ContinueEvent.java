package io.github.parkkevinsb.flower.sample.basic.event;

public final class ContinueEvent {

    private final String message;

    public ContinueEvent(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
