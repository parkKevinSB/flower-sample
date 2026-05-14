# flower-sample

Sample collection for **Flower** (orchestration) + **Bloom** (events).

Each sample is its own Spring Boot 3.3 / Java 21 app and lives in its own
Gradle subproject. The root only manages shared versions, repositories, the
Java toolchain, and the test runner. There is one Gradle wrapper at the root
shared across all samples.

```text
flower-sample/
  README.md                 (this file)
  settings.gradle.kts
  build.gradle.kts          common config for every sample
  gradle/, gradlew, gradlew.bat

  cafe-order/               Flower entry-point sample (implemented)
  logistics-control/        automated warehouse Zone sample (implemented)
  game-turn-battle/         turn-based battle sample (implemented)
```

## Samples

| Module              | Status      | What it shows                                                           |
|---------------------|-------------|-------------------------------------------------------------------------|
| `cafe-order`        | implemented | Spring Boot wiring, Worker submit, Step composition, Bloom request/reply |
| `logistics-control` | implemented | self-driving warehouse Zones, shared robot capacity, queue back pressure |
| `game-turn-battle`  | implemented | goTo turn loop, signal-only Bloom callbacks, persistent game state + web UI |

See `SAMPLE_PROJECT_PLAN.md` for the design notes behind this layout and what
each future sample is meant to demonstrate.

## Prerequisites

The samples depend on local snapshot builds of Flower and Bloom. Install them
into your local Maven repository before building:

```bash
cd ../bloom && mvn install
cd ../flower && mvn install
```

## Run a sample

From the `flower-sample` root:

```bash
./gradlew :cafe-order:bootRun           # port 8080
./gradlew :logistics-control:bootRun    # port 8081
./gradlew :game-turn-battle:bootRun     # port 8082
```

Each sample exposes its own `static/index.html` and curl examples in its own
README.

## Test a sample

```bash
./gradlew :cafe-order:test
```

Or run every sample's tests:

```bash
./gradlew test
```

## Adding a new sample

1. Create a subproject folder (e.g. `my-sample/`) with a `build.gradle.kts`
   that applies `org.springframework.boot` and `io.spring.dependency-management`.
2. Add `include("my-sample")` to `settings.gradle.kts`.
3. Add a `README.md` and a `static/index.html` in the new module.
4. Each sample is a standalone app — do not share domain code across samples.
   Slight repetition is intentional; the goal is learnability, not reuse.
