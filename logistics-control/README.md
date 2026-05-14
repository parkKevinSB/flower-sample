# logistics-control

Spring Boot 3.3 + Java 21 sample showing a self-driving warehouse conveyor.
Users only create work orders; the app starts one long-running Flower Flow per
warehouse Zone, and those Zone Flows repeatedly admit, process, and release
boxes.

This sample is part of the [`flower-sample`](../README.md) collection. It runs
as a standalone Spring Boot app from the multi-project root.

## Flow Model

An order does not create a Flow. It creates a box on the shared conveyor:

```text
POST /work-orders/{id}
  -> WarehouseConveyor.submitOrder(id)
  -> box appears in the Goods rack queue
```

At application startup, `LogisticsControlSampleApplication` calls
`WarehouseZoneWorker.submitZoneFlows()` to submit these always-on Flows:

```text
rack-robot-zone -> Flow warehouse-zone/rack-robot
inspection-zone -> Flow warehouse-zone/inspection
packing-zone    -> Flow warehouse-zone/packing
sortation-zone  -> Flow warehouse-zone/sortation
loading-zone    -> Flow warehouse-zone/loading
```

The Rack robot Flow is intentionally different because it owns the Goods rack.
The rack can hold up to 8 waiting boxes, and the robot always takes the oldest
waiting box first:

```text
Flow: warehouse-zone/rack-robot
  -> await-rack-box        poll the Goods rack FIFO queue; stay if empty
  -> grip-rack-box         grip the first waiting box
  -> place-on-conveyor     move it onto the belt and queue Inspection
  -> goTo(await-rack-box)
```

The downstream Zone Flows share the queue/admit/release mechanics, but each Zone
has its own work Step:

```text
Flow: warehouse-zone/inspection
  -> await-box -> inspect-box -> release-box -> goTo(await-box)

Flow: warehouse-zone/packing
  -> await-box -> pack-box -> release-box -> goTo(await-box)

Flow: warehouse-zone/sortation
  -> await-box -> sort-box -> release-box -> goTo(await-box)

Flow: warehouse-zone/loading
  -> await-box -> load-truck -> release-box -> goTo(await-box)
```

That means the Zone is the active process. Boxes are data moving through queues.
If a Zone is busy, new boxes remain in that Zone's `WAITING_*` status until the
Zone Flow loops back and admits the next box.

Each downstream Zone also holds the admitted box for its own processing time, so
the UI can show the box being worked instead of instantly hopping to the next
queue:

```text
Inspection: 3.5s
Packing:    4.2s
Sortation:  3.2s
Loading:    3.8s
```

## Zone Route

```text
Rack robot -> picks a box from Goods rack and places it on the conveyor
Inspection -> weight and vision check
Packing    -> dunnage, seal, and shipping label
Sortation  -> route gate sends the box to outbound dock
Loading    -> lift loads the box into the truck
```

The Loading Zone also checks the truck dock before it admits a waiting box. A
truck holds 6 boxes. When it departs, the Loading Zone leaves incoming boxes in
`WAITING_LOADING` until the dock reports that the next truck has arrived.

## Package Map

```text
api         REST endpoints: create a box and read status
domain      WorkOrder, WarehouseZone, WarehouseConveyor, in-memory store
config      Bloom <-> Flower adapter and observability listeners
workflow.factory  Zone Flow composition
workflow.step     rack-specific Steps, Zone-specific work Steps, queue Steps
workflow.worker   application handle that submits Zone Flows to Zone workers
```

## Running

Install the upstream snapshots first if they are not already in your local
Maven repository:

```bash
cd ../bloom && mvn install
cd ../flower && mvn install
```

Then run the sample from the `flower-sample` root:

```bash
./gradlew :logistics-control:bootRun
```

Open [http://localhost:8081](http://localhost:8081). Create orders or use the
burst button; boxes will move through the D-shaped conveyor automatically.

## Driving The Conveyor With Curl

```bash
# Create boxes. No body is required.
curl -X POST http://localhost:8081/work-orders/WO-1
curl -X POST http://localhost:8081/work-orders/WO-2
curl -X POST http://localhost:8081/work-orders/WO-3

# Inspect status.
curl http://localhost:8081/work-orders/WO-1
curl http://localhost:8081/work-orders
curl http://localhost:8081/actuator/metrics/flower.flow.finished

# Inspect and depart the docked truck.
curl http://localhost:8081/truck-dock
curl -X POST http://localhost:8081/truck-dock/depart
```

Status progression visible from `GET /work-orders/{id}`:

```text
ACCEPTED
WAITING_RACK_ROBOT
ROBOT_GRIPPING
ROBOT_PLACING
ON_CONVEYOR
WAITING_INSPECTION
INSPECTING
INSPECTED
WAITING_PACKING
PACKING
PACKED
WAITING_SORTATION
SORTING
SORTED
WAITING_LOADING
LOADING_TRUCK
LOADED
COMPLETED
SHIPPED
```

Some statuses are short-lived because the Zone workers tick independently.

## Tests

From the `flower-sample` root:

```bash
./gradlew :logistics-control:test
```

`LogisticsControlSampleApplicationTest` boots the Spring context, verifies the
Zone Flows are running, creates orders, confirms queue back pressure, and waits
until the boxes complete automatically.
