package io.github.parkkevinsb.flower.sample.cafe.domain;

import java.time.Instant;

public record CafeOrder(String orderId, CafeOrderStatus status, Instant updatedAt) {

    public CafeOrder withStatus(CafeOrderStatus next) {
        return new CafeOrder(orderId, next, Instant.now());
    }
}
