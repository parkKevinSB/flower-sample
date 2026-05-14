# Flower Sample Project Plan

## Rename From `sample` To `flower-sample`

현재 `sample`은 하나의 독립 Spring Boot Gradle 프로젝트입니다. 그래서 단순히 폴더 이름을 `sample`에서 `flower-sample`로 바꾸는 것만으로도 대부분은 동작합니다.

그래도 이름 변경 시 같이 정리하면 좋은 항목은 있습니다.

```text
sample/
  -> flower-sample/
```

확인할 것:

- `settings.gradle.kts`의 `rootProject.name`
- README의 경로 예시
- 실행 문서의 `cd sample` 문구
- IDE import 설정
- CI가 생긴다면 checkout/build path

현재 `settings.gradle.kts`는 이미 `flower-cafe-order-sample`이라 폴더명과 강하게 묶여 있지는 않습니다. 단일 프로젝트로 유지한다면 폴더명만 바꿔도 괜찮습니다.

## 여러 샘플을 추가할 때 권장 구조

샘플을 여러 개 만들 계획이면 `flower-sample`을 “샘플 모음 저장소”로 두고, 각 샘플을 하위 앱으로 나누는 편이 좋습니다.

권장 구조:

```text
flower-sample/
  README.md
  settings.gradle.kts
  build.gradle.kts
  gradlew
  gradlew.bat
  gradle/

  cafe-order/
    README.md
    build.gradle.kts
    src/main/java/...
    src/main/resources/application.yml
    src/main/resources/static/index.html
    src/test/java/...

  logistics-control/
    README.md
    build.gradle.kts
    src/main/java/...
    src/main/resources/application.yml
    src/main/resources/static/index.html
    src/test/java/...

  turn-battle/
    README.md
    build.gradle.kts
    src/main/java/...
    src/main/resources/application.yml
    src/main/resources/static/index.html
    src/test/java/...
```

루트 Gradle은 공통 버전, 공통 repository, 공통 테스트 설정만 관리합니다. 각 샘플 프로젝트는 자체 Spring Boot main class, 자체 `application.yml`, 자체 웹 페이지, 자체 테스트를 가져야 합니다.

실행 예:

```bash
./gradlew :cafe-order:bootRun
./gradlew :logistics-control:bootRun
./gradlew :turn-battle:bootRun
```

각 샘플 폴더 README에는 해당 샘플만 실행하는 명령을 별도로 둡니다. “단독 실행 가능”의 기준은 샘플마다 독립 앱으로 뜨고, 다른 샘플 모듈에 의존하지 않는다는 뜻으로 잡는 게 좋습니다.

정말로 각 샘플을 폴더째 떼어내도 `./gradlew bootRun`이 되게 만들고 싶다면 샘플마다 Gradle wrapper를 복제해야 합니다. 하지만 유지보수 비용이 커집니다. 저는 루트 wrapper 하나를 두고, 각 샘플은 독립 Spring Boot 앱으로 구성하는 방식을 추천합니다.

## Parent가 필요한가?

샘플이 하나라면 parent는 필요 없습니다.

샘플이 세 개 이상으로 늘어난다면 parent가 있는 편이 낫습니다.

이유:

- Spring Boot 버전 통일
- Flower/Bloom snapshot 의존성 통일
- Java toolchain 통일
- 테스트 설정 통일
- 루트에서 전체 샘플 검증 가능

단, parent가 샘플 간 도메인 코드를 공유하게 만들면 안 됩니다. 샘플은 서로 복붙처럼 보여도 됩니다. 샘플의 목적은 재사용성이 아니라 학습성입니다.

좋은 parent의 책임:

```text
공통 plugin 버전
공통 repositories
공통 Java version
공통 Flower/Bloom dependency version
공통 test 설정
```

나쁜 parent의 책임:

```text
샘플 간 domain model 공유
샘플 공통 service 추상화
샘플 전용 framework 만들기
```

## 샘플별 목표

각 샘플은 Flower의 서로 다른 핵심을 보여줘야 합니다.

### 1. Cafe Order Sample

현재 샘플의 역할:

```text
HTTP 요청
  -> Flow 생성
  -> Worker submit
  -> Step 진행
  -> Bloom event publish/subscribe
  -> StepLogger
  -> 웹에서 custom Step 조합
```

보여주는 핵심:

- Spring Boot에서 Flower를 시작하는 방법
- Worker가 어디서 생기고 어디로 submit하는지
- Step이 상태를 갖기 때문에 매 Flow마다 새 Step이 필요하다는 점
- Bloom은 이벤트 시스템이고 Flower는 orchestration이라는 점
- 정적 Flow와 사용자 조합 Flow가 둘 다 가능하다는 점

이 샘플은 “Flower 입문 샘플”로 유지하면 좋습니다.

### 2. Logistics Control Sample

목표 도메인:

```text
작은 물류 작업 하나를 여러 설비/외부 이벤트에 맞춰 진행한다.
```

너무 복잡한 항만/창고 전체 제어가 아니라, 하나의 작업 지시를 처리하는 정도가 좋습니다.

추천 시나리오:

```text
POST /work-orders/{id}

Flow: warehouse-fulfillment
  -> accept-work-order
  -> wait-picker-assigned
  -> wait-items-picked
  -> wait-packing-completed
  -> wait-shipping-dock-ready
  -> load-truck
  -> complete-work-order
```

샘플용 이벤트:

```text
PickerAssignedEvent
ItemsPickedEvent
PackingCompletedEvent
ShippingDockReadyEvent
TruckLoadedEvent
```

웹 페이지:

```text
작업 생성 버튼
피커 배정 이벤트 발행 버튼
상품 피킹 완료 버튼
포장 완료 버튼
출하 도크 준비 버튼
트럭 상차 완료 버튼
현재 Flow 상태 표시
```

보여주는 Flower 핵심:

- 외부 이벤트가 사람이 누르는 버튼이나 설비 이벤트처럼 늦게 들어오는 흐름
- Step이 event callback에서 바로 전이하지 않고 signal만 남기는 패턴
- timeout/retry/repeat 예시
- `goTo` 또는 `repeat`가 자연스럽게 쓰일 수 있음

주의:

- 자동화 장비나 항만 용어를 너무 많이 늘리지 않습니다.
- DB, 지도, 좌표, 최적화는 넣지 않습니다.
- “한 작업 지시가 이벤트를 기다리며 전진한다”에 집중합니다.

### 3. Turn Battle Sample

목표 도메인:

```text
게임 서버에서 한 전투 턴을 단계별로 진행한다.
```

추천 시나리오:

```text
POST /battles/{id}

Flow: turn-battle
  -> start-turn
  -> wait-player-action
  -> resolve-action
  -> enemy-action
  -> check-battle-end
  -> finish-turn
```

샘플용 이벤트:

```text
PlayerActionSelectedEvent
AnimationFinishedEvent
EnemyActionReadyEvent
```

웹 페이지:

```text
전투 시작
플레이어 행동 선택 버튼: Attack / Guard / Skill
애니메이션 완료 이벤트 버튼
적 행동 준비 이벤트 버튼
전투 로그 표시
HP 표시
```

보여주는 Flower 핵심:

- 플레이어 입력을 기다리는 Step
- 이벤트로 들어온 payload를 Step 내부 판단에 쓰는 패턴
- stepNo로 한 Step 내부의 작은 단계 나누기
- `repeat`로 다음 턴 반복
- `done`으로 전투 종료

주의:

- 실제 게임 밸런스, 스킬 시스템, AI는 단순하게 둡니다.
- 턴 하나의 진행과 반복 구조가 핵심입니다.
- 그래픽 게임을 만들 필요는 없습니다. 상태 패널과 버튼이면 충분합니다.

## 어떤 샘플을 먼저 만들까?

추천 순서:

```text
1. cafe-order
2. logistics-control
3. turn-battle
```

이유:

- cafe는 가장 친숙한 입문 샘플입니다.
- logistics는 Flower가 원래 잘 어울리는 운영/제어 도메인을 보여줍니다.
- turn-battle은 Flower가 게임 서버 같은 비업무 도메인에도 적용될 수 있음을 보여줍니다.

## 각 샘플의 공통 규칙

각 샘플은 다음을 지킵니다.

- 브라우저에서 바로 테스트 가능한 `static/index.html` 제공
- `README.md`에 실행 명령과 curl 예시 제공
- `./gradlew :sample-name:test`로 통합 테스트 통과
- `application.yml`에 Worker 이름이 명확히 보이게 작성
- `workflow.worker`에 Worker submit 위치를 노출
- `workflow.factory`에 Flow 조합과 Step 생성 위치를 분리
- `workflow.step`에는 Step 구현만 둠
- event는 plain Java object로 작성
- Step 안의 로그는 `StepLogger` 사용

각 샘플은 너무 많이 추상화하지 않습니다. 샘플은 처음 보는 사람이 읽는 코드라서, 약간의 반복이 추상화보다 낫습니다.
