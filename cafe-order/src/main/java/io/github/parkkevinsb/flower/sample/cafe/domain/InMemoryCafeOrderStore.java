package io.github.parkkevinsb.flower.sample.cafe.domain;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class InMemoryCafeOrderStore implements CafeOrderStore {

    private final ConcurrentMap<String, CafeOrder> orders = new ConcurrentHashMap<>();

    @Override
    public CafeOrder accept(String orderId) {
        return orders.compute(orderId, (id, existing) ->
                existing == null
                        ? new CafeOrder(id, CafeOrderStatus.ACCEPTED, Instant.now())
                        : existing.withStatus(CafeOrderStatus.ACCEPTED));
    }

    @Override
    public CafeOrder cupPrepared(String orderId) {
        return transition(orderId, CafeOrderStatus.CUP_PREPARED);
    }

    @Override
    public CafeOrder paymentRequested(String orderId) {
        return transition(orderId, CafeOrderStatus.PAYMENT_REQUESTED);
    }

    @Override
    public CafeOrder paymentApproved(String orderId) {
        return transition(orderId, CafeOrderStatus.PAYMENT_APPROVED);
    }

    @Override
    public CafeOrder brewRequested(String orderId) {
        return transition(orderId, CafeOrderStatus.BREW_REQUESTED);
    }

    @Override
    public CafeOrder ready(String orderId) {
        return transition(orderId, CafeOrderStatus.READY);
    }

    @Override
    public CafeOrder complete(String orderId) {
        return transition(orderId, CafeOrderStatus.COMPLETED);
    }

    @Override
    public CafeOrder fail(String orderId) {
        return transition(orderId, CafeOrderStatus.FAILED);
    }

    @Override
    public CafeOrder find(String orderId) {
        return orders.get(orderId);
    }

    @Override
    public Collection<CafeOrder> findAll() {
        return Collections.unmodifiableCollection(orders.values());
    }

    private CafeOrder transition(String orderId, CafeOrderStatus next) {
        return orders.compute(orderId, (id, existing) -> {
            if (existing == null) {
                throw new IllegalStateException("unknown orderId: " + id);
            }
            return existing.withStatus(next);
        });
    }
}
