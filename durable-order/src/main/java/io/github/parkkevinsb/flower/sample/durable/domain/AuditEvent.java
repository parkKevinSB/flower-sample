package io.github.parkkevinsb.flower.sample.durable.domain;

import java.time.Instant;

public final class AuditEvent {
    private final long id;
    private final String auditType;
    private final String message;
    private final Instant createdAt;

    public AuditEvent(long id, String auditType, String message, Instant createdAt) {
        this.id = id;
        this.auditType = auditType;
        this.message = message;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public String getAuditType() {
        return auditType;
    }

    public String getMessage() {
        return message;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
