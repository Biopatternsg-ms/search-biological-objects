---

description: "Task list for Use Cases Hygiene, Defensive Domain Validation, and Parameter Encapsulation"
---

# Tasks: Use Cases Hygiene, Defensive Domain Validation, and Parameter Encapsulation

**Input**: Design documents from `/specs/003-use-cases-hygiene-and-parameter-encapsulation/`

**Prerequisites**: `plan.md`, `spec.md`, `research.md`, `data-model.md`, `contracts/internal-ports-and-usecases.md`, `quickstart.md`

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., `US1`, `US2`, `US3`, `US4`)
- Exact file paths are included in every task description.

---

## Phase 1: Setup (Baseline Verification)

**Purpose**: Verify current compilation and ArchUnit baseline before refactoring

- [X] T001 Verify baseline compilation and architectural rules pass via `./mvnw clean compile && ./mvnw test -Dtest=ArchitectureLayerTest`

---

## Phase 2: User Story 1 - Defensive Lineage Resolution and Null-Safe Hierarchy Navigation (Priority: P1) 🎯 MVP

**Goal**: Prevent `NullPointerException` for missing mined objects and root-level nodes (`biologicalObjectParentId == null`) in `FindBiologicalObjectFatherBrothersAndSonsUseCase`, and decompose lineage aggregation into clean private helper methods.

**Independent Test**: Run `FindBiologicalObjectFatherBrothersAndSonsUseCaseTest` covering missing mined object (returns `List.of()`), root-level node (`biologicalObjectParentId == null`, skips father/brothers), and full lineage (father + brothers + sons).

### Implementation & Tests for User Story 1

- [X] T002 [US1] Add null-safe guards for missing `currentMinedObject` / `currentBiologicalObject` (returning `List.of()`) and null `biologicalObjectParentId` (skipping father and sibling lookups) in `src/main/java/com/biopatternsg/application/usecase/FindBiologicalObjectFatherBrothersAndSonsUseCase.java`
- [X] T003 [US1] Extract father, sibling, and son retrieval and map aggregation into single-responsibility private helper methods in `src/main/java/com/biopatternsg/application/usecase/FindBiologicalObjectFatherBrothersAndSonsUseCase.java` (depends on T002)
- [X] T004 [P] [US1] Create unit tests covering missing mined object, root node (`biologicalObjectParentId == null`), and full lineage in `src/test/java/com/biopatternsg/application/usecase/FindBiologicalObjectFatherBrothersAndSonsUseCaseTest.java`

**Checkpoint**: User Story 1 is independently functional and tested.

---

## Phase 3: User Story 2 - Encapsulate Method Signatures to Satisfy Clean Code Limits (Priority: P1)

**Goal**: Eliminate all 4-parameter method signatures in `ConfigAndControlRepository`, `ConfigAndControlAdapter`, and `PipelineServiceImpl` by introducing `PipelineStepUpdate` and `MinedObjectCreationContext` records.

**Independent Test**: Verify zero methods in `domain.port.*`, `infrastructure.adapters.*`, and `application.services.*` accept more than 3 individual parameters, and verify `./mvnw clean compile` succeeds.

### Implementation for User Story 2

- [X] T005 [P] [US2] Create `PipelineStepUpdate` domain record with fields `(String pipelineId, PipelineSteps step, Status status, Map<String, String> metrics)` (where `pipelineId`, `step`, `status` are non-null and `metrics` is nullable) plus a 3-argument convenience constructor in `src/main/java/com/biopatternsg/domain/models/PipelineStepUpdate.java`
- [X] T006 [US2] Update `ConfigAndControlRepository` outbound port to accept `PipelineStepUpdate` as a single parameter in `src/main/java/com/biopatternsg/domain/port/out/repositories/ConfigAndControlRepository.java` (depends on T005)
- [X] T007 [US2] Update `ConfigAndControlAdapter` to implement `updatePipelineStep(PipelineStepUpdate update)` and map fields to `PipelineStepInternalRequest` in `src/main/java/com/biopatternsg/infrastructure/adapters/out/repositories/ConfigAndControlAdapter.java` (depends on T006)
- [X] T008 [US2] Add internal `private record MinedObjectCreationContext(String pipelineId, String parentId, int level)` (where `parentId` is nullable for level 1) and refactor `newObjects`, `buildMinedObject`, and `updatePipelineStep` to <= 2 parameters in `src/main/java/com/biopatternsg/application/services/impl/PipelineServiceImpl.java` (depends on T006)

**Checkpoint**: User Story 2 is complete with all method signatures strictly `<= 3` parameters.

---

## Phase 4: User Story 3 - Clean Up Unused Dependencies and Standardize Logging in Use Cases (Priority: P2)

**Goal**: Remove the unused `DiscoveryObjectService` injection from `LaunchPipelineUseCase` and replace `System.out.println` with structured `@Slf4j` `log.info`.

**Independent Test**: Verify `LaunchPipelineUseCase` has zero unused fields/imports and zero `System.out` statements, and run `LaunchPipelineUseCaseTest`.

### Implementation & Tests for User Story 3

- [X] T009 [P] [US3] Remove unused `DiscoveryObjectService` field and import, add `@Slf4j`, and replace `System.out.println` with structured `log.info` in `src/main/java/com/biopatternsg/application/usecase/LaunchPipelineUseCase.java`
- [X] T010 [P] [US3] Create unit test verifying `LaunchPipelineUseCase` delegates to `PipelineService` in `src/test/java/com/biopatternsg/application/usecase/LaunchPipelineUseCaseTest.java`

**Checkpoint**: User Story 3 is independently functional and tested.

---

## Phase 5: User Story 4 - Explicit Domain Exception on Missing Entity Update (Priority: P2)

**Goal**: Throw `ApiException(GeneralError.NOT_FOUND, ...)` in `UpdateBiologicalObjectMeshIdUseCase` when the target `BiologicalObject` is not found.

**Independent Test**: Run `UpdateBiologicalObjectMeshIdUseCaseTest` verifying `ApiException(GeneralError.NOT_FOUND)` on missing ID and persistence update on valid ID.

### Implementation & Tests for User Story 4

- [X] T011 [P] [US4] Validate non-null `biologicalObject` and throw `ApiException(GeneralError.NOT_FOUND, "Biological object not found with id: " + biologicalObjectId)` when missing in `src/main/java/com/biopatternsg/application/usecase/UpdateBiologicalObjectMeshIdUseCase.java`
- [X] T012 [P] [US4] Create unit tests verifying successful MeSH ID update and `ApiException(GeneralError.NOT_FOUND)` on missing object in `src/test/java/com/biopatternsg/application/usecase/UpdateBiologicalObjectMeshIdUseCaseTest.java`

**Checkpoint**: User Story 4 is independently functional and tested.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Synchronize `.agents/use_cases/` documentation and run full validation suite

- [X] T013 [P] Synchronize `.agents/use_cases/FindBiologicalObjectFatherBrothersAndSonsUseCase.md` with updated null-safe root/missing node behavior and helper breakdown
- [X] T014 [P] Synchronize `.agents/use_cases/LaunchPipelineUseCase.md` removing `DiscoveryObjectService` reference and documenting `@Slf4j` logging
- [X] T015 [P] Synchronize `.agents/use_cases/UpdateBiologicalObjectMeshIdUseCase.md` documenting `ApiException(GeneralError.NOT_FOUND)` failure scenario
- [X] T016 Run full quickstart validation: `./mvnw clean compile`, `./mvnw test -Dtest=ArchitectureLayerTest,LaunchPipelineUseCaseTest,FindBiologicalObjectFatherBrothersAndSonsUseCaseTest,UpdateBiologicalObjectMeshIdUseCaseTest`, and verify zero `System.out` in `LaunchPipelineUseCase.java`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies — starts immediately.
- **User Stories (Phases 2–5)**: Independent of one another; can execute in parallel or sequentially in priority order (`US1` → `US2` → `US3` → `US4`).
- **Polish (Phase 6)**: Depends on Phases 2–5 completing (`T013`–`T015` can run in parallel as soon as their corresponding use case is updated; `T016` runs last).

### Parallel Opportunities

- `US1`, `US2`, `US3`, and `US4` modify completely disjoint files and can all proceed in parallel after `T001`.
- Within `US3` (`T009`, `T010`), `US4` (`T011`, `T012`), and `Phase 6` (`T013`, `T014`, `T015`), all `[P]` tasks can be executed simultaneously.

---

## Parallel Example: Across User Stories

```bash
# After T001, these can run in parallel:
# US1: FindBiologicalObjectFatherBrothersAndSonsUseCase.java + test
# US2: PipelineStepUpdate.java -> ConfigAndControlRepository.java -> ConfigAndControlAdapter.java & PipelineServiceImpl.java
# US3: LaunchPipelineUseCase.java + test
# US4: UpdateBiologicalObjectMeshIdUseCase.java + test
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1 (`T001`).
2. Complete Phase 2 (`T002`–`T004` for `FindBiologicalObjectFatherBrothersAndSonsUseCase`).
3. **STOP and VALIDATE**: Run `FindBiologicalObjectFatherBrothersAndSonsUseCaseTest` and `ArchitectureLayerTest`.

### Incremental Delivery

1. `US1` (Null-safe lineage resolution) → Tested independently.
2. `US2` (3-parameter encapsulation) → Compiled & verified across ports/adapters/services.
3. `US3` (`LaunchPipelineUseCase` hygiene) → Tested independently.
4. `US4` (`UpdateBiologicalObjectMeshIdUseCase` `NOT_FOUND` exception) → Tested independently.
5. `Phase 6` (Documentation sync & final validation gate).
