---

description: "Task list for REST Controllers Purity and Delivery Layer Compliance"
---

# Tasks: REST Controllers Purity and Delivery Layer Compliance

**Input**: Design documents from `/specs/004-rest-controllers-purity-and-delivery-layer-compliance/`

**Prerequisites**: `plan.md`, `spec.md`, `research.md`, `data-model.md`, `contracts/rest-delivery-contracts.md`, `quickstart.md`

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., `US1`, `US2`, `US3`, `US4`)
- Exact file paths are included in every task description.

---

## Phase 1: Setup (Baseline Verification)

**Purpose**: Verify baseline compilation and ArchUnit rules before refactoring

- [X] T001 Verify baseline compilation and architectural rules pass via `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./mvnw clean compile && JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./mvnw test -Dtest=ArchitectureLayerTest`

---

## Phase 2: User Story 1 - Thin Delivery Controllers with Centralized Exception Mapping (Priority: P1) 🎯 MVP

**Goal**: Remove unused injected fields from `BiologicalObjectController` and `PipelineController`, and remove manual `try/catch` from `BiologicalObjectController#updateMeshId` so `ApiException` propagates to `ApiExceptionMapper`.

**Independent Test**: Inspect controller fields for zero unused dependencies and run `BiologicalObjectControllerTest` verifying `updateMeshId` propagates `ApiException(GeneralError.NOT_FOUND)`.

### Implementation & Tests for User Story 1

- [X] T002 [P] [US1] Remove unused `SessionUtil` field/import and remove the `try/catch` block in `updateMeshId` in `src/main/java/com/biopatternsg/infrastructure/adapters/in/restcontrollers/BiologicalObjectController.java`
- [X] T003 [P] [US1] Remove unused `ManagedExecutor` field/import in `src/main/java/com/biopatternsg/infrastructure/adapters/in/restcontrollers/PipelineController.java`
- [X] T004 [P] [US1] Create unit tests verifying `BiologicalObjectController#updateMeshId` returns `200 OK` on success and propagates `ApiException` when missing in `src/test/java/com/biopatternsg/infrastructure/adapters/in/restcontrollers/BiologicalObjectControllerTest.java`

**Checkpoint**: User Story 1 is independently functional and tested.

---

## Phase 3: User Story 2 - Clean Asynchronous Pipeline Dispatch and Typed Responses (Priority: P1)

**Goal**: Extract ArC `ManagedContext` lifecycle management, `SessionUtil` context propagation, and `CompletableFuture.runAsync` from `PipelineController#experiment` into `AsyncPipelineDispatcher`, and return typed `LaunchPipelineResponse`.

**Independent Test**: Run `PipelineControllerTest` verifying `experiment` delegates to `AsyncPipelineDispatcher` and returns `202 Accepted` with `LaunchPipelineResponse("successful launch pipeline <id>")`.

### Implementation & Tests for User Story 2

- [X] T005 [P] [US2] Create `LaunchPipelineResponse` record (`String message`) in `src/main/java/com/biopatternsg/infrastructure/adapters/dtos/LaunchPipelineResponse.java`
- [X] T006 [P] [US2] Create `AsyncPipelineDispatcher` (`@ApplicationScoped`) encapsulating `SessionUtil` context snapshot, `CompletableFuture.runAsync`, `Arc.container().requestContext()` activation/termination, and `LaunchPipeline` execution in `src/main/java/com/biopatternsg/infrastructure/adapters/in/AsyncPipelineDispatcher.java`
- [X] T007 [US2] Refactor `PipelineController#experiment` to call `AsyncPipelineDispatcher.dispatch(...)` and return `Response.accepted(new LaunchPipelineResponse(...)).build()` in `src/main/java/com/biopatternsg/infrastructure/adapters/in/restcontrollers/PipelineController.java` (depends on T005, T006)
- [X] T008 [P] [US2] Create unit tests verifying `PipelineController#experiment` returns `202 Accepted` with `LaunchPipelineResponse` and delegates to `AsyncPipelineDispatcher` in `src/test/java/com/biopatternsg/infrastructure/adapters/in/restcontrollers/PipelineControllerTest.java`

**Checkpoint**: User Story 2 is independently functional and tested.

---

## Phase 4: User Story 3 - Isolate Domain Models Behind Delivery DTOs and Stateless Mappers (Priority: P2)

**Goal**: Introduce delivery DTO records in `infrastructure.adapters.dtos.*` and stateless `public final` static mappers in `infrastructure.adapters.mappers.*` so all 5 controllers accept/return DTOs and never instantiate multi-argument domain models inline.

**Independent Test**: Run `DeliveryMappersTest` verifying null-safe static mappings and backward-compatible fields, and inspect all 5 controllers for zero direct domain model exposure.

### Implementation & Tests for User Story 3

- [X] T009 [P] [US3] Create `TranscriptionFactorDTO` record in `src/main/java/com/biopatternsg/infrastructure/adapters/dtos/TranscriptionFactorDTO.java`
- [X] T010 [P] [US3] Create `BiologicalObjectDetailDTO` record in `src/main/java/com/biopatternsg/infrastructure/adapters/dtos/BiologicalObjectDetailDTO.java` (depends on T009)
- [X] T011 [P] [US3] Create `BiologicalObjectConfigDTO`, `TranscriptionFactorConfigRequest`, and `LaunchPipelineRequest` records in `src/main/java/com/biopatternsg/infrastructure/adapters/dtos/`
- [X] T012 [P] [US3] Create `ComplexDTO` and `BlatSearchOptionDTO` records in `src/main/java/com/biopatternsg/infrastructure/adapters/dtos/`
- [X] T013 [P] [US3] Create stateless `public final` static mappers (`BiologicalObjectDtoMapper`, `PipelineDtoMapper`, `TranscriptionFactorMapper`, `ComplexDtoMapper`, `BlatDtoMapper`) with private constructors and null-safe static methods in `src/main/java/com/biopatternsg/infrastructure/adapters/mappers/` (depends on T009–T012)
- [X] T014 [US3] Update `BiologicalObjectController#find` to return `BiologicalObjectDetailDTO` via `BiologicalObjectDtoMapper.toDetailDto` in `src/main/java/com/biopatternsg/infrastructure/adapters/in/restcontrollers/BiologicalObjectController.java` (depends on T010, T013)
- [X] T015 [US3] Update `PipelineController#experiment` to accept `@RequestBody LaunchPipelineRequest` mapped via `PipelineDtoMapper.toDomain` in `src/main/java/com/biopatternsg/infrastructure/adapters/in/restcontrollers/PipelineController.java` (depends on T011, T013)
- [X] T016 [US3] Update `TranscriptionFactorController` to use `TranscriptionFactorMapper.requestToModel`, `TranscriptionFactorConfigRequest`, and `List<TranscriptionFactorDTO>` in `src/main/java/com/biopatternsg/infrastructure/adapters/in/restcontrollers/TranscriptionFactorController.java` (depends on T009, T011, T013)
- [X] T017 [P] [US3] Update `ComplexController` and `BlatController` to return `List<ComplexDTO>` and `List<BlatSearchOptionDTO>` via `ComplexDtoMapper` and `BlatDtoMapper` in `src/main/java/com/biopatternsg/infrastructure/adapters/in/restcontrollers/ComplexController.java` and `src/main/java/com/biopatternsg/infrastructure/adapters/in/restcontrollers/BlatController.java` (depends on T012, T013)
- [X] T018 [P] [US3] Create unit tests for all static delivery mappers (including null-safety) in `src/test/java/com/biopatternsg/infrastructure/adapters/mappers/DeliveryMappersTest.java` (depends on T013)

**Checkpoint**: User Story 3 is independently functional and tested.

---

## Phase 5: User Story 4 - Consolidate Delivery DTO Packages and Documentation (Priority: P3)

**Goal**: Document the consolidated REST delivery DTO package (`infrastructure.adapters.dtos`), static mapper package (`infrastructure.adapters.mappers`), and `AsyncPipelineDispatcher` in `.agents/architecture.md` and `.agents/architecture-rules.md`.

**Independent Test**: Verify `.agents/architecture.md` and `.agents/architecture-rules.md` accurately reflect the package structure.

### Implementation for User Story 4

- [X] T019 [P] [US4] Update `.agents/architecture.md` and `.agents/architecture-rules.md` documenting `infrastructure.adapters.dtos.*`, `infrastructure.adapters.mappers.*`, `AsyncPipelineDispatcher`, and outbound `infrastructure.dtos.*`

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Run full quickstart validation gates

- [X] T020 Run full validation gates: `./mvnw clean compile`, `./mvnw test -Dtest=ArchitectureLayerTest,BiologicalObjectControllerTest,PipelineControllerTest,DeliveryMappersTest`, and `git grep` check for `Arc.container` / `new JasparQuery` in `restcontrollers/`

---

## Dependencies & Execution Order

- **Setup (Phase 1)**: Starts immediately (`T001`).
- **US1 (Phase 2)** and **US2 (Phase 3)**: Can proceed after `T001` (`T003` and `T007` touch `PipelineController.java` sequentially).
- **US3 (Phase 4)**: DTOs (`T009`–`T012`) can be created in parallel; mappers (`T013`) depend on DTOs; controller updates (`T014`–`T017`) and mapper tests (`T018`) depend on `T013`.
- **US4 (Phase 5)** and **Polish (Phase 6)**: `T019` can run in parallel with `US3` tests; `T020` runs last.

---

## Implementation Strategy

### MVP First (User Story 1)

1. Complete Phase 1 (`T001`).
2. Complete Phase 2 (`T002`–`T004`) to remove unused fields and restore centralized exception mapping in `BiologicalObjectController#updateMeshId`.
3. Proceed incrementally through `US2` (Async dispatch), `US3` (DTOs & static mappers), `US4` (Docs), and `Phase 6` (Validation).
