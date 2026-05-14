package io.github.parkkevinsb.flower.sample.cafe;

import io.github.parkkevinsb.flower.core.step.StepDefinition;
import io.github.parkkevinsb.flower.sample.cafe.api.CafeOrderResponse;
import io.github.parkkevinsb.flower.sample.cafe.api.CustomCafeOrderRequest;
import io.github.parkkevinsb.flower.sample.cafe.domain.CafeOrder;
import io.github.parkkevinsb.flower.sample.cafe.domain.CafeOrderStatus;
import io.github.parkkevinsb.flower.sample.cafe.domain.CafeOrderStore;
import io.github.parkkevinsb.flower.sample.cafe.workflow.factory.CafeOrderFlowFactory;
import io.github.parkkevinsb.flower.sample.cafe.workflow.factory.CafeOrderStepCatalog;
import io.github.parkkevinsb.flower.sample.cafe.workflow.factory.CustomCafeOrderFlowFactory;
import io.github.parkkevinsb.flower.sample.cafe.workflow.factory.PrepaidCafeOrderFlowFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static io.github.parkkevinsb.flower.sample.cafe.workflow.factory.CafeOrderStepCatalog.ACCEPT_ORDER;
import static io.github.parkkevinsb.flower.sample.cafe.workflow.factory.CafeOrderStepCatalog.BREW;
import static io.github.parkkevinsb.flower.sample.cafe.workflow.factory.CafeOrderStepCatalog.COMPLETE_ORDER;
import static io.github.parkkevinsb.flower.sample.cafe.workflow.factory.CafeOrderStepCatalog.PAYMENT;
import static io.github.parkkevinsb.flower.sample.cafe.workflow.factory.CafeOrderStepCatalog.PREPARE_CUP;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CafeOrderSampleApplicationTest {

    @Autowired
    TestRestTemplate rest;

    @Autowired
    CafeOrderStore store;

    @Autowired
    CafeOrderFlowFactory standardFlowFactory;

    @Autowired
    PrepaidCafeOrderFlowFactory prepaidFlowFactory;

    @Autowired
    CustomCafeOrderFlowFactory customFlowFactory;

    @Autowired
    CafeOrderStepCatalog stepCatalog;

    @Test
    void orderCompletesThroughBloomEvents() throws Exception {
        rest.postForEntity("/orders/ORDER-1", null, CafeOrderResponse.class);

        awaitUntilCompleted("ORDER-1");

        CafeOrder order = store.find("ORDER-1");
        assertThat(order).isNotNull();
        assertThat(order.status()).isEqualTo(CafeOrderStatus.COMPLETED);
    }

    @Test
    void prepaidOrderCompletesWithDifferentFlowComposition() throws Exception {
        rest.postForEntity("/orders/PREPAID-1/prepaid", null, CafeOrderResponse.class);

        awaitUntilCompleted("PREPAID-1");

        CafeOrder order = store.find("PREPAID-1");
        assertThat(order).isNotNull();
        assertThat(order.status()).isEqualTo(CafeOrderStatus.COMPLETED);
    }

    @Test
    void customOrderCompletesWithUserSelectedSteps() throws Exception {
        CustomCafeOrderRequest request = new CustomCafeOrderRequest();
        request.setSteps(List.of(ACCEPT_ORDER, PREPARE_CUP, BREW, COMPLETE_ORDER));

        rest.postForEntity("/orders/CUSTOM-1/custom", request, CafeOrderResponse.class);

        awaitUntilCompleted("CUSTOM-1");

        CafeOrder order = store.find("CUSTOM-1");
        assertThat(order).isNotNull();
        assertThat(order.status()).isEqualTo(CafeOrderStatus.COMPLETED);
    }

    @Test
    void factoriesCanComposeDifferentFlowsFromTheSameSteps() {
        assertThat(standardFlowFactory.create("STANDARD-SHAPE").steps())
                .extracting(StepDefinition::stepId)
                .containsExactly("accept-order", "prepare-cup", "payment", "brew", "complete-order");

        assertThat(prepaidFlowFactory.create("PREPAID-SHAPE").steps())
                .extracting(StepDefinition::stepId)
                .containsExactly("accept-order", "prepare-cup", "brew", "complete-order");

        assertThat(customFlowFactory.create("CUSTOM-SHAPE", List.of(ACCEPT_ORDER, PAYMENT, BREW, COMPLETE_ORDER)).steps())
                .extracting(StepDefinition::stepId)
                .containsExactly("accept-order", "payment", "brew", "complete-order");
    }

    @Test
    void customFlowRejectsUnsafeComposition() {
        CustomCafeOrderRequest request = new CustomCafeOrderRequest();
        request.setSteps(List.of(BREW, COMPLETE_ORDER));

        ResponseEntity<String> response = rest.postForEntity(
                "/orders/BAD-CUSTOM/custom",
                request,
                String.class);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void stepCatalogExposesUserComposableSteps() {
        assertThat(stepCatalog.descriptors())
                .extracting("id")
                .containsExactly(ACCEPT_ORDER, PREPARE_CUP, PAYMENT, BREW, COMPLETE_ORDER);
    }

    private void awaitUntilCompleted(String orderId) throws InterruptedException {
        long deadline = System.currentTimeMillis() + 45_000L;
        while (System.currentTimeMillis() < deadline) {
            CafeOrder order = store.find(orderId);
            if (order != null && order.status() == CafeOrderStatus.COMPLETED) {
                return;
            }
            Thread.sleep(50L);
        }
        throw new AssertionError("order did not complete: " + orderId);
    }
}
