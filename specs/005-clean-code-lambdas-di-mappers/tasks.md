---

description: "Task list for Clean Code Lambdas, Constructor Injection, Static Mappers, and @ToString Compliance"
---

# Tasks: Clean Code Lambdas, Constructor Injection, Static Mappers, and `@ToString` Compliance

**Input**: Design documents from `/specs/005-clean-code-lambdas-di-mappers/`

**Prerequisites**: `plan.md`, `spec.md`, `research.md`, `data-model.md`, `quickstart.md`

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., `US1`, `US2`, `US3`, `US4`)
- Exact file paths are included in every task description.

---

## Phase 1: Setup (Baseline Verification)

**Purpose**: Verify baseline compilation and test suite pass before refactoring

- [X] T001 Verify baseline compilation and tests pass via `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./mvnw clean compile && JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./mvnw test`

---

## Phase 2: User Story 1 - Enforce Constructor Injection Across Adapters (Priority: P1) 🎯 MVP

**Goal**: Eliminate `@Inject` and `@ConfigProperty` field injections in `BiologicalObjectRepositoryAdapter` and `PdbAdapter` in favor of `private final` constructor-injected fields.

**Independent Test**: Verify zero `@Inject` field annotations remain in `BiologicalObjectRepositoryAdapter` and `PdbAdapter` and compile cleanly.

### Implementation for User Story 1

- [X] T002 [P] [US1] Convert `@Inject` fields (`biologicalObjectRepositoryDB`, `userRepository`) to `private final` constructor-injected fields via `@RequiredArgsConstructor` in `src/main/java/com/biopatternsg/infrastructure/adapters/out/repositories/BiologicalObjectRepositoryAdapter.java`
- [X] T003 [P] [US1] Convert `@ConfigProperty` `targetOrganism` field to `private final String targetOrganism` injected via `@Inject` constructor in `src/main/java/com/biopatternsg/infrastructure/adapters/out/external_repositories/PdbAdapter.java`

**Checkpoint**: User Story 1 is complete with constructor-only injection.

---

## Phase 3: User Story 2 - Null-Safe Stateless Pure Static Mappers (Priority: P1)

**Goal**: Standardize `MinedObjectMapper` and `BiologicalObjectMapper` as `public final class` with explicit `private` constructors, null-safe static methods, and resolve `//TODO chequearID`.

**Independent Test**: Run `PersistenceMappersTest` verifying null single inputs, null lists, and null IDs are handled without `NullPointerException`.

### Implementation & Tests for User Story 2

- [X] T004 [P] [US2] Make `MinedObjectMapper` `public final` with private constructor, add null guards on `toMinedObject`, `toMinedObjectCollection`, `toMinedObjects`, and `id != null`, and resolve `//TODO chequearID` in `src/main/java/com/biopatternsg/infrastructure/mongo_db/mappers/MinedObjectMapper.java`
- [X] T005 [P] [US2] Make `BiologicalObjectMapper` `public final` with private constructor and add null guards on `id`, `toBiologicalObjectCollection`, and `toBiologicalObjects` in `src/main/java/com/biopatternsg/infrastructure/mongo_db/mappers/BiologicalObjectMapper.java`
- [X] T006 [P] [US2] Create unit tests verifying null-safety and mapping correctness for `MinedObjectMapper` and `BiologicalObjectMapper` in `src/test/java/com/biopatternsg/infrastructure/mongo_db/mappers/PersistenceMappersTest.java` (depends on T004, T005)

**Checkpoint**: User Story 2 is independently functional and tested.

---

## Phase 4: User Story 3 - Single-Expression Lambdas, Clean Loops, and Modern Stream Collectors (Priority: P2)

**Goal**: Extract multi-line `.map(...)` / `.forEach(...)` lambdas into cohesive private helper methods, remove duplicate setter calls in `MinedObjectRepositoryAdapter`, and replace `.collect(Collectors.toList())` with `.toList()` in `PubmedIntegrationAdapter`.

**Independent Test**: Inspect refactored classes for single-expression lambdas/method references and run full unit test suite.

### Implementation for User Story 3

- [X] T007 [P] [US3] Extract multi-line `.forEach` lambda into single-responsibility private helper methods in `src/main/java/com/biopatternsg/application/services/impl/ExpertObjectServicesImpl.java`
- [X] T008 [P] [US3] Extract multi-line `.forEach` lambda into single-responsibility private helper methods in `src/main/java/com/biopatternsg/application/services/impl/TranscriptionFactorsServiceImpl.java`
- [X] T009 [US3] Extract multi-line `.map` lambda in `updateAll` into a private helper method in `src/main/java/com/biopatternsg/infrastructure/adapters/out/repositories/BiologicalObjectRepositoryAdapter.java` (depends on T002)
- [X] T010 [P] [US3] Extract inline `.map` builder into private helper method and remove duplicate `mongoObject.setBiologicalObjectId(...)` in `saveMinedCollection` in `src/main/java/com/biopatternsg/infrastructure/adapters/out/repositories/MinedObjectRepositoryAdapter.java`
- [X] T011 [US3] Extract multi-line `.map` lambdas in `getComplexes` and `getComplexesDeprecated` into a private `toComplex` helper method in `src/main/java/com/biopatternsg/infrastructure/adapters/out/external_repositories/PdbAdapter.java` (depends on T003)
- [X] T012 [P] [US3] Extract nested multi-line `.forEach` lambdas (`setCodesGO`, `setSynonyms`) into small private helper methods in `src/main/java/com/biopatternsg/infrastructure/adapters/out/external_repositories/UniprotAdapter.java`
- [X] T013 [P] [US3] Replace `.collect(Collectors.toList())` with `.toList()` in `src/main/java/com/biopatternsg/infrastructure/adapters/out/external_repositories/PubmedIntegrationAdapter.java`

**Checkpoint**: User Story 3 is complete with clean single-expression lambdas and modern collectors.

---

## Phase 5: User Story 4 - Explicit `toString()` Across Domain Models and DTOs (Priority: P3)

**Goal**: Annotate all 11 domain model classes and `JasparRegion` with `@ToString`.

**Independent Test**: Compile and verify `@ToString` is present on all 12 classes.

### Implementation for User Story 4

- [X] T014 [P] [US4] Add `@ToString` to `BiologicalObject`, `Complex`, `GeneOntology`, and `MinedObject` in `src/main/java/com/biopatternsg/domain/models/`
- [X] T015 [P] [US4] Add `@ToString` to `HGNCResponse` and `UniprotResponse` in `src/main/java/com/biopatternsg/domain/models/external_entities/`
- [X] T016 [P] [US4] Add `@ToString` to `BiologicalObjectConfig`, `DiscoveryObjectConfig`, `MinedObjectConfig`, `PipelineConfig`, and `TranscriptionFactorConfig` in `src/main/java/com/biopatternsg/domain/models/pipeline_config/`
- [X] T017 [P] [US4] Add `@ToString` to `JasparRegion` in `src/main/java/com/biopatternsg/infrastructure/dtos/JasparRegion.java`

**Checkpoint**: User Story 4 is complete.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Run full build, test suite, and static hygiene checks

- [X] T018 Run full quickstart validation: `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./mvnw clean compile && JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./mvnw test` and verify zero field `@Inject` and zero `Collectors.toList()` in `PubmedIntegrationAdapter`

---

## Dependencies & Execution Order

- **Setup (Phase 1)**: Starts immediately (`T001`).
- **US1–US4 (Phases 2–5)**: Independent across distinct files except `BiologicalObjectRepositoryAdapter.java` (`T002` before `T009`) and `PdbAdapter.java` (`T003` before `T011`).
- **Polish (Phase 6)**: `T018` runs after all story phases complete.
