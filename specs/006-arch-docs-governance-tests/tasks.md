---

description: "Task list for Architecture & Documentation Governance Automated Tests"
---

# Tasks: Architecture & Documentation Governance Automated Tests

**Input**: Design documents from `/specs/006-arch-docs-governance-tests/`

**Prerequisites**: `plan.md`, `spec.md`, `research.md`, `data-model.md`, `quickstart.md`

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., `US1`, `US2`, `US3`, `US4`)
- Exact file paths are included in every task description.

---

## Phase 1: Setup (Baseline Verification)

**Purpose**: Verify baseline compilation and `ArchitectureLayerTest` pass before adding new governance rules

- [X] T001 Verify baseline compilation and existing `ArchitectureLayerTest` pass via `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./mvnw clean test -Dtest=ArchitectureLayerTest`

---

## Phase 2: User Story 1 - Automated Framework & Layer Isolation Enforcement (Priority: P1) 🎯 MVP

**Goal**: Expand `ArchitectureLayerTest` to forbid CDI/MicroProfile (`jakarta.enterprise..`, `jakarta.inject..`, `org.eclipse.microprofile..`) in `domain..`, forbid persistence (`io.quarkus.mongodb..`, `org.bson..`) in `application..`, and keep `application.usecase..` isolated from `application.services.impl..`.

**Independent Test**: Run `ArchitectureLayerTest` and verify all framework and use-case isolation rules pass.

### Implementation for User Story 1

- [X] T002 [US1] Expand `domainLayerFrameworkIsolationTest` in `src/test/java/archtest/ArchitectureLayerTest.java` to forbid `jakarta.enterprise..`, `jakarta.inject..`, and `org.eclipse.microprofile..` alongside `jakarta.ws.rs..`, `io.quarkus..`, and `org.bson..`
- [X] T003 [US1] Expand `applicationLayerFrameworkIsolationTest` in `src/test/java/archtest/ArchitectureLayerTest.java` to forbid `io.quarkus.mongodb..` and `org.bson..` alongside `jakarta.ws.rs..` (depends on T002)

**Checkpoint**: User Story 1 is independently functional and tested.

---

## Phase 3: User Story 2 - Automated Static Mapper Purity Verification (Priority: P1)

**Goal**: Add `staticMappersShouldBeStatelessFinalUtilityClassesTest` to `ArchitectureLayerTest` ensuring all mapper classes under `..mappers..` (excluding `infrastructure.exceptions..`) are `final`, have `private` constructors, and carry no CDI scope annotations.

**Independent Test**: Run `ArchitectureLayerTest` and verify `staticMappersShouldBeStatelessFinalUtilityClassesTest` passes across all persistence and delivery mappers.

### Implementation for User Story 2

- [X] T004 [US2] Add `staticMappersShouldBeStatelessFinalUtilityClassesTest` verifying `FINAL` modifier, `PRIVATE` constructor(s), and absence of `@ApplicationScoped`/`@Singleton`/`@RequestScoped` for top-level classes in `..mappers..` in `src/test/java/archtest/ArchitectureLayerTest.java` (depends on T003)

**Checkpoint**: User Story 2 is independently functional and tested.

---

## Phase 4: User Story 3 - Automated Use Case Documentation Governance (Priority: P2)

**Goal**: Create `DocumentationGovernanceTest` verifying 1:1 correspondence between `*UseCase.java` classes and `.agents/use_cases/<UseCaseName>.md`, presence of Mermaid `sequenceDiagram`, indexing in `.agents/use_cases/README.md` and Section 5 of `.agents/architecture.md`, and zero orphan `.md` files.

**Independent Test**: Run `./mvnw test -Dtest=DocumentationGovernanceTest` and verify all 10 Use Cases pass.

### Implementation for User Story 3

- [X] T005 [P] [US3] Create `DocumentationGovernanceTest` validating `*UseCase.java` <-> `.agents/use_cases/*.md`, Mermaid `sequenceDiagram`, `.agents/use_cases/README.md`, Section 5 of `.agents/architecture.md`, and zero orphan `.md` files in `src/test/java/archtest/DocumentationGovernanceTest.java`

**Checkpoint**: User Story 3 is independently functional and tested.

---

## Phase 5: User Story 4 - Update Project Quality Gates Documentation (Priority: P3)

**Goal**: Add `DocumentationGovernanceTest` to the mandatory verification gates in `.agents/architecture-rules.md`, `.agents/architecture.md`, and `.specify/memory/constitution.md`.

**Independent Test**: Inspect all 3 governance files for `./mvnw test -Dtest=DocumentationGovernanceTest`.

### Implementation for User Story 4

- [X] T006 [P] [US4] Add `DocumentationGovernanceTest` to verification gates in `.agents/architecture-rules.md`
- [X] T007 [P] [US4] Add `DocumentationGovernanceTest` to verification gates in `.agents/architecture.md`
- [X] T008 [P] [US4] Add `DocumentationGovernanceTest` to `Development Workflow & Quality Gates` in `.specify/memory/constitution.md`

**Checkpoint**: User Story 4 is complete.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Run full governance and repository test suites

- [X] T009 Run `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./mvnw clean test -Dtest=ArchitectureLayerTest,DocumentationGovernanceTest` and `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./mvnw test`

---

## Dependencies & Execution Order

- **Setup (Phase 1)**: `T001` starts immediately.
- **US1 (Phase 2)** & **US2 (Phase 3)**: Sequential edits to `ArchitectureLayerTest.java` (`T002` → `T003` → `T004`).
- **US3 (Phase 4)** & **US4 (Phase 5)**: `T005`, `T006`, `T007`, `T008` touch disjoint files and can all run in parallel with `T002`–`T004`.
- **Polish (Phase 6)**: `T009` runs after all tasks complete.
