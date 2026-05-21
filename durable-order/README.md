# durable-order

Spring Boot 3.3 + Java 21 sample showing Flower checkpoint persistence in a
small order workflow.

The sample intentionally runs two kinds of Flow in the same app:

```text
durable order flow
  validate-order
  wait-payment       stores currentStepId + stepNo while waiting
  reserve-inventory
  ship-order
  complete-order

transient audit flow
  write-audit-start
  pause
  write-audit-finish
```

The order flow is durable. Its checkpoint is stored in H2 through
`flower-persistence-jdbc`. The audit flow is transient. It may run beside the
durable order, but it does not write a checkpoint and disappears when the app
process is stopped.

Every visible order step uses Flower's `StepContext.startTimeout(...)` and
`timedOut()` helpers to stay active for about five seconds. The `wait-payment`
step waits indefinitely until the domain table says the order is paid, then
shows a five-second confirmation before moving on.

The domain state also lives in H2. This is the important Flower pattern:

```text
Flower checkpoint = where to resume
Order table       = what is true about the business
```

## Running

Install the upstream snapshot first if needed:

```bash
cd ../flower && mvn install
```

Then run the sample from the `flower-sample` root:

```bash
./gradlew :durable-order:bootRun
```

Open:

```text
http://localhost:8083
```

## What To Try

1. Start a durable order.
2. Wait until the checkpoint shows `wait-payment` and a growing `stepNo`.
3. Start a transient audit.
4. Stop the sample app and start `./gradlew :durable-order:bootRun` again.
5. The durable order is recovered at `wait-payment`; the transient audit has no
   checkpoint.
6. Click `Mark Paid`; the order continues to inventory, shipping, and complete.

`schema.sql` is part of this sample app. Flower itself still does not create
tables automatically.
