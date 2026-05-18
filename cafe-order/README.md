# cafe-order

Spring Boot 3.3 + Java 21 sample showing how **Flower** (orchestration) and
**Bloom** (events) compose in a small cafe order flow.

This is the entry-point sample of the [`flower-sample`](../README.md)
collection. It runs as a standalone Spring Boot app from the multi-project
root.

Standard order flow:

```text
POST /orders/{id}
   -> accept-order      records the order
   -> prepare-cup       uses stepNo only: pick cup, then label cup
   -> payment           publishes PaymentRequestedEvent, waits for PaymentApprovedEvent
   -> brew              publishes BrewRequestedEvent, waits for CoffeeReadyEvent
   -> complete-order    marks the order COMPLETED
```

Prepaid order flow, built from the same Step classes:

```text
POST /orders/{id}/prepaid
   -> accept-order
   -> prepare-cup
   -> brew
   -> complete-order
```

Custom order flow, built from user-selected Step ids:

```text
POST /orders/{id}/custom
body: {"steps":["accept-order","prepare-cup","brew","complete-order"]}
```

## Mental model

Flower is not the event system. Bloom is.

```text
Bloom EventBus
  - publishes plain event objects
  - delivers them to subscribers

Flower Flow / Step
  - owns the order workflow
  - converts received events into Step-local signals
  - advances only from the Worker tick
```

The important Flower pattern is visible in `PaymentStep` and `BrewStep`:

```text
onEnter: subscribe to the reply event
onTick stepNo 0: publish the request event, start timeout, move to stepNo 10
event callback: set a small signal only
onTick stepNo 10: read signal/timeout and return StepResult
```

`PrepareCupStep` shows the simpler stepNo-only shape:

```text
onTick stepNo 0: do the first small action, set stepNo 10, stay
onTick stepNo 10: do the second small action, return done()
```

That sample intentionally leaves `0` and `10` inline instead of hiding them
behind constants, so the `stepNo` cursor is visible at the call site.

`BrewStep` shows the other style: it still stores numbers in Flower's
`stepNo`, but maps them to an internal enum so the domain meaning is explicit.

## Package Map

```text
api         HTTP endpoints that submit and read orders
domain      order state and in-memory store
workflow.factory  Flow factories and Step factory
workflow.step     Flower Step classes
workflow.worker   Application handle to the Flower Worker named "orders"
event       plain Bloom event objects
partner     sample in-process event subscribers that publish replies
config      Bloom -> Flower adapter wiring and observability listeners
```

`CafeOrderFlowFactory` owns the standard Flow composition.
`PrepaidCafeOrderFlowFactory` owns the prepaid Flow composition.
`CafeOrderStepFactory` owns Step construction and dependency wiring. The split
is deliberate: a new Flow must get new Step instances because Steps hold
per-run state such as subscriptions, stepNo, signals, and timeouts.

The event classes are intentionally plain immutable Java objects instead of
records. Bloom only needs `publish(new SomeEvent(...))` and
`subscribe(SomeEvent.class, handler)`, so a normal class with `getOrderId()` is
the clearest shape for a sample.

`PaymentGateway` and `BaristaStation` are sample-only partner components. They
show the simplest Bloom interaction: subscribe to a request event and publish a
reply event. Real services could replace them with HTTP clients, message
brokers, or webhook handlers without changing the Flower Steps.

## Wiring

`FlowerSampleConfig` creates two event bus beans:

```text
io.github.parkkevinsb.bloom.EventBus
  -> native Bloom bus used by sample partner classes

io.github.parkkevinsb.flower.core.event.EventBus
  -> BloomEventBus.wrap(bloom), used by Flower
```

The Spring Boot starter auto-creates the Flower `Engine` and `orders` Worker
from `application.yml`. `CafeOrderWorker` is the application-side handle that
submits Flows to that named Worker. The observability module is wired through
`LoggingFlowerListener`, `MicrometerFlowerListener`, and `StepLogger.of(...)`
inside each Step.

## Running

Install the upstream snapshots first if they are not already in your local
Maven repository:

```bash
cd ../bloom && mvn install
cd ../flower && mvn install
```

Then run the sample from the `flower-sample` root:

```bash
./gradlew :cafe-order:bootRun
```

Open the local page:

```text
http://localhost:8080
```

Try an order:

```bash
curl -X POST http://localhost:8080/orders/ORDER-1
curl -X POST http://localhost:8080/orders/PREPAID-1/prepaid
curl.exe -X POST http://localhost:8080/orders/CUSTOM-1/custom -H "Content-Type: application/json" -d '{"steps":["accept-order","prepare-cup","brew","complete-order"]}'
curl http://localhost:8080/orders/ORDER-1
curl http://localhost:8080/orders/PREPAID-1
curl http://localhost:8080/orders/CUSTOM-1
curl http://localhost:8080/actuator/metrics/flower.flow.finished
```

The web page loads the composable Step catalog from `GET /orders/steps` and
submits custom flows to `POST /orders/{id}/custom`. The validation lives in the
sample app: custom flows must start with `accept-order`, end with
`complete-order`, use known Steps once, and run `payment` before `brew` when
both are selected.

The order should reach `COMPLETED` within about a second. The log pattern shows
the Flower MDC values added by `StepLogger`:

```text
flow=cafe-order/ORDER-1 step=prepare-cup no=0  PrepareCupStep - pick cup
flow=cafe-order/ORDER-1 step=prepare-cup no=10 PrepareCupStep - cup prepared
flow=cafe-order/ORDER-1 step=payment no=0  PaymentStep - request payment
flow=cafe-order/ORDER-1 step=payment no=10 PaymentStep - payment approved
flow=cafe-order/ORDER-1 step=brew    no=0  BrewStep    - send brew ticket
flow=cafe-order/ORDER-1 step=brew    no=10 BrewStep    - coffee ready
flow=cafe-order/ORDER-1 step=complete-order no=0 CompleteOrderStep - order completed
```

## Tests

From the `flower-sample` root:

```bash
./gradlew :cafe-order:test
```

`CafeOrderSampleApplicationTest` boots the whole Spring context, posts an
order, and waits until the Flower/Bloom flow marks it `COMPLETED`.
