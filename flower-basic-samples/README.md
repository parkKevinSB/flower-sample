# flower-basic-samples

Plain Java samples for Flower core runtime behavior.

These samples intentionally avoid domain concepts, Spring Boot, web UIs, and
manual `tickOnce()` calls. Each sample starts a real `Engine`, submits a real
`Flow` to a real `Worker`, prints the Step lifecycle to the console, waits for
the Flow to terminate, and then stops the Engine.

## Run

From the `flower-sample` root:

```bash
./gradlew :flower-basic-samples:runDoneFlowSample
./gradlew :flower-basic-samples:runStayFlowSample
./gradlew :flower-basic-samples:runGoToFlowSample
./gradlew :flower-basic-samples:runEventFlowSample
./gradlew :flower-basic-samples:runRepeatFlowSample
./gradlew :flower-basic-samples:runFinishFlowSample
./gradlew :flower-basic-samples:runFailFlowSample
./gradlew :flower-basic-samples:runGuardFlowSample
```

## Test

```bash
./gradlew :flower-basic-samples:test
```

## Packages

```text
basic.done
  step-1 -> step-2 -> step-3, each returning DONE after a small delay

basic.stay
  a Step uses stepNo, startTimeout, timedOut, and STAY before DONE

basic.gotoexample
  a Step jumps from step-1 to step-3, skipping step-2

basic.event
  a Step subscribes to an event, waits, receives the event, and then finishes

basic.repeat
  a Step returns REPEAT and starts again before the Flow continues

basic.finishfail
  a Step returns FINISH or FAIL and prevents later Steps from running

basic.guard
  a Guard holds a Step for a few ticks before allowing it to run
```
