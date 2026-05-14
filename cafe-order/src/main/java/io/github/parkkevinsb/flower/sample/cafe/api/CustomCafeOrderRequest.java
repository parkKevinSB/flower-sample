package io.github.parkkevinsb.flower.sample.cafe.api;

import java.util.ArrayList;
import java.util.List;

public final class CustomCafeOrderRequest {

    private List<String> steps = new ArrayList<>();

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps != null ? steps : new ArrayList<>();
    }
}
