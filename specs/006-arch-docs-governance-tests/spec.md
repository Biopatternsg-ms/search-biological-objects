# Feature Specification: Architecture & Documentation Governance Automated Tests

**Feature Branch**: `feature/06-architecture-and-documentation-governance-tests`

**Created**: 2026-11-22

**Status**: Draft

**Input**: User description: "/speckit-specify basado en /home/yacson/Proyectos/biopatternsg-ms/SDD/biological-objects/06-architecture-and-documentation-governance-tests"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Automated Framework & Layer Isolation Enforcement (Priority: P1) 🎯 MVP

As a software architect and CI/CD maintainer,
I want automated architectural fitness tests to block delivery, persistence, and container framework leakage into the Domain and Application layers, as well as direct dependencies from Use Cases on concrete service implementations,
So that architectural drift (such as HTTP responses in domain ports or concrete strategy imports in use cases) fails the build immediately rather than passing unnoticed.

**Why this priority**: Without automated verification, framework dependencies (`jakarta.ws.rs`, `io.quarkus`, `org.bson`) and concrete service implementation imports (`application.services.impl`) silently creep back into pure business layers over time.

**Independent Test**: Can be independently tested by running `ArchitectureLayerTest` against the compiled codebase and verifying all domain/application framework isolation and use-case-to-impl rules pass.

**Acceptance Scenarios**:

1. **Given** compiled classes in `com.biopatternsg.domain..`, **When** architectural tests run, **Then** any dependency on web, CDI, Quarkus, MongoDB/BSON, or MicroProfile framework packages fails the test suite.
2. **Given** compiled classes in `com.biopatternsg.application..`, **When** architectural tests run, **Then** any dependency on delivery (`jakarta.ws.rs..`) or persistence (`io.quarkus.mongodb..`, `org.bson..`) frameworks fails the test suite.
3. **Given** compiled classes in `com.biopatternsg.application.usecase..`, **When** architectural tests run, **Then** any dependency on `com.biopatternsg.application.services.impl..` fails the test suite.

---

### User Story 2 - Automated Static Mapper Purity Verification (Priority: P1)

As a maintainer enforcing Clean Code and stateless mapping standards,
I want automated architectural checks verifying that all mapper classes are `final`, non-instantiable via private constructors, and free of CDI scope annotations,
So that stateless pure mapper conventions are guaranteed across both persistence and delivery mappers.

**Why this priority**: Ensuring mappers remain stateless utility classes prevents accidental CDI bean injection or mutable state inside mapping layers.

**Independent Test**: Can be independently tested by running `ArchitectureLayerTest` and confirming all classes under `..mappers..` pass finality, private constructor, and non-CDI-bean checks.

**Acceptance Scenarios**:

1. **Given** any class under `..mappers..` (excluding exception mappers in `infrastructure.exceptions`), **When** architectural tests run, **Then** the test verifies the class is `final`, has a `private` constructor, and carries no CDI scope annotations (`@ApplicationScoped`, `@Singleton`, `@RequestScoped`).

---

### User Story 3 - Automated Use Case Documentation Governance (Priority: P2)

As a team lead and developer onboarding onto the service,
I want an automated `DocumentationGovernanceTest` verifying that every `*UseCase` class has an up-to-date `.agents/use_cases/<UseCaseName>.md` document with a Mermaid sequence diagram, is indexed in `.agents/use_cases/README.md` and Section 5 of `.agents/architecture.md`, and that no orphan use case docs exist,
So that architectural documentation never drifts out of sync with the actual use cases in production.

**Why this priority**: Undocumented or orphaned use case markdown files erode trust in `.agents/` documentation and slow down incident response and feature planning.

**Independent Test**: Can be independently tested by running `DocumentationGovernanceTest` and verifying it validates all existing Use Cases against `.agents/use_cases/`, `README.md`, and `.agents/architecture.md`.

**Acceptance Scenarios**:

1. **Given** all `*UseCase.java` files in `application/usecase/`, **When** `DocumentationGovernanceTest` runs, **Then** every use case has a matching `.agents/use_cases/<UseCaseName>.md` file containing a Mermaid `sequenceDiagram` block.
2. **Given** each `<UseCaseName>.md` file, **When** `DocumentationGovernanceTest` runs, **Then** it is referenced in both `.agents/use_cases/README.md` and Section 5 of `.agents/architecture.md`.
3. **Given** all `.md` files in `.agents/use_cases/` (excluding `README.md`), **When** `DocumentationGovernanceTest` runs, **Then** zero orphan markdown files exist without a corresponding `*UseCase.java` class.

---

### User Story 4 - Update Project Quality Gates Documentation (Priority: P3)

As a developer following project governance guidelines,
I want `DocumentationGovernanceTest` added to the mandatory verification gates in `.agents/architecture-rules.md`, `.agents/architecture.md`, and `.specify/memory/constitution.md`,
So that both human developers and AI agents run both architectural and documentation governance checks before completing any change.

**Why this priority**: Aligning governance documentation with the new automated test ensures consistent execution across all workflows.

**Independent Test**: Can be independently verified by inspecting `.agents/architecture-rules.md`, `.agents/architecture.md`, and `.specify/memory/constitution.md` for `DocumentationGovernanceTest` in their verification commands.

**Acceptance Scenarios**:

1. **Given** `.agents/architecture-rules.md`, `.agents/architecture.md`, and `.specify/memory/constitution.md`, **When** reviewed, **Then** all three list `DocumentationGovernanceTest` alongside `ArchitectureLayerTest` in their verification gates.

---

### Edge Cases

- **Exception Mappers in `infrastructure.exceptions`:** JAX-RS `ExceptionMapper` classes (such as `ApiExceptionMapper`) are excluded from the static mapper utility rule so valid CDI/JAX-RS exception mappers are not flagged as false positives.
- **`README.md` in `.agents/use_cases/`:** `DocumentationGovernanceTest` excludes `README.md` when checking for orphan `<UseCaseName>.md` files.
- **Inner or nested classes in `..mappers..`:** Synthetic/anonymous or non-top-level classes (if any) are handled cleanly by the ArchUnit mapper rule.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST extend `ArchitectureLayerTest` with `domainShouldNotDependOnFrameworksTest` forbidding `com.biopatternsg.domain..` from depending on `jakarta.ws.rs..`, `jakarta.enterprise..`, `jakarta.inject..`, `io.quarkus..`, `org.bson..`, or `org.eclipse.microprofile..`.
- **FR-002**: System MUST extend `ArchitectureLayerTest` with `applicationShouldNotDependOnDeliveryOrPersistenceFrameworksTest` forbidding `com.biopatternsg.application..` from depending on `jakarta.ws.rs..`, `io.quarkus.mongodb..`, or `org.bson..`.
- **FR-003**: System MUST extend `ArchitectureLayerTest` with `useCasesShouldNotDependOnServiceImplementationsTest` forbidding `com.biopatternsg.application.usecase..` from depending on `com.biopatternsg.application.services.impl..`.
- **FR-004**: System MUST extend `ArchitectureLayerTest` with `staticMappersShouldBeStatelessFinalUtilityClassesTest` verifying classes in `..mappers..` (excluding `infrastructure.exceptions..`) are `final`, have private constructors, and are not annotated with `@ApplicationScoped`, `@Singleton`, or `@RequestScoped`.
- **FR-005**: System MUST create `src/test/java/archtest/DocumentationGovernanceTest.java` verifying that every `*UseCase.java` has a corresponding `.agents/use_cases/<UseCaseName>.md` containing a `sequenceDiagram` block, is referenced in `.agents/use_cases/README.md` and Section 5 of `.agents/architecture.md`, and has zero orphan `.md` files in `.agents/use_cases/`.
- **FR-006**: System MUST update `.agents/architecture-rules.md`, `.agents/architecture.md`, and `.specify/memory/constitution.md` to include `./mvnw test -Dtest=DocumentationGovernanceTest` (or combined with `ArchitectureLayerTest`) in the mandatory verification gates.
- **FR-007**: System MUST pass `./mvnw test -Dtest=ArchitectureLayerTest,DocumentationGovernanceTest` and `./mvnw clean test` with zero errors.

### Key Entities *(include if feature involves data)*

- **ArchitectureLayerTest**: ArchUnit test suite enforcing hexagonal isolation, framework boundaries, naming conventions, and static mapper purity.
- **DocumentationGovernanceTest**: Automated governance test validating 1:1 synchronization between `*UseCase.java` classes and `.agents/use_cases/*.md`, `README.md`, and `.agents/architecture.md`.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: `ArchitectureLayerTest` enforces 100% of the 4 new architectural rules in addition to existing layer/naming rules with 0 failures.
- **SC-002**: `DocumentationGovernanceTest` validates 100% of existing `*UseCase.java` classes (all 10 use cases) with 0 missing docs, 0 missing Mermaid diagrams, and 0 orphan files.
- **SC-003**: All 3 governance documents (`.agents/architecture-rules.md`, `.agents/architecture.md`, `.specify/memory/constitution.md`) reference `DocumentationGovernanceTest` in their verification gates.
- **SC-004**: Full `./mvnw test` suite passes with 0 failures and 0 errors.

## Assumptions

- Requirements `REQ-BO-ARCH-01` through `REQ-BO-ARCH-05` have already been implemented on the codebase, so all new governance rules are expected to pass immediately against the current code and documentation.
- Updating `.specify/memory/constitution.md` Section "Development Workflow & Quality Gates" is a `PATCH` / `MINOR` documentation alignment requested explicitly by `REQ-BO-ARCH-06` (incrementing `Last Amended` date and Sync Impact Report if applicable).
