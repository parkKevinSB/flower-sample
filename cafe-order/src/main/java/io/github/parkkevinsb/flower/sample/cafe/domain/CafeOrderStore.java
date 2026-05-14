package io.github.parkkevinsb.flower.sample.cafe.domain;

import java.util.Collection;

public interface CafeOrderStore {

    CafeOrder accept(String orderId);

    CafeOrder cupPrepared(String orderId);

    CafeOrder paymentRequested(String orderId);

    CafeOrder paymentApproved(String orderId);

    CafeOrder brewRequested(String orderId);

    CafeOrder ready(String orderId);

    CafeOrder complete(String orderId);

    CafeOrder fail(String orderId);

    CafeOrder find(String orderId);

    Collection<CafeOrder> findAll();
}
