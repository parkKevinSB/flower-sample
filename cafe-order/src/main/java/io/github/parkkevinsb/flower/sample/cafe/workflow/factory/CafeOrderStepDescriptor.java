package io.github.parkkevinsb.flower.sample.cafe.workflow.factory;

/**
 * User-facing metadata for a Step that can be composed into a custom Flow.
 */
public final class CafeOrderStepDescriptor {

    private final String id;
    private final String label;
    private final String description;
    private final boolean required;

    public CafeOrderStepDescriptor(String id, String label, String description, boolean required) {
        this.id = id;
        this.label = label;
        this.description = description;
        this.required = required;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRequired() {
        return required;
    }
}
