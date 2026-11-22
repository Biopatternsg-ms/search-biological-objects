# Implementation Plan: Architecture & Documentation Governance Automated Tests

**Branch**: `feature/06-architecture-and-documentation-governance-tests` | **Date**: 2026-11-22 | **Spec**: [spec.md](file:///home/yacson/Proyectos/biopatternsg-ms/services/search-biological-objects/specs/006-arch-docs-governance-tests/spec.md)

**Input**: Feature specification from `/specs/006-arch-docs-governance-tests/spec.md`

## Summary

Establish automated architectural and documentation governance gates in CI/CD:
1. Expand `src/test/java/archtest/ArchitectureLayerTest.java` to forbid `jakarta.enterprise..`, `jakarta.inject..`, and `org.eclipse.microprofile..` in `domain..`, forbid `io.quarkus.mongodb..` and `org.bson..` in `application..`, retain `useCaseIsolationFromServiceImplTest`, and add `staticMappersShouldBeStatelessFinalUtilityClassesTest` verifying `..mappers..` classes are `final`, have private constructors, and carry no CDI scope annotations.
2. Create `src/test/java/archtest/DocumentationGovernanceTest.java` verifying 1:1 correspondence between `*UseCase.java` classes and `.agents/use_cases/<UseCaseName>.md`, presence of a Mermaid `sequenceDiagram` block, indexing in `.agents/use_cases/README.md` and Section 5 of `.agents/architecture.md`, and zero orphan `.md` files.
3. Update `.agents/architecture-rules.md`, `.agents/architecture.md`, and `.specify/memory/constitution.md` to include `DocumentationGovernanceTest` in the mandatory verification gates.

## Technical Context

**Language/Version**: Java 21 (`maven.compiler.release` 21)

**Primary Dependencies**: JUnit 5 (`org.junit.jupiter.*`), ArchUnit (`com.tngtech.archunit.*`), standard Java NIO (`java.nio.file.*`)

**Storage**: N/A (build-time test suite reading `target/classes`, `src/main/java`, and `.agents/`)

**Testing**: JUnit 5 + ArchUnit (`./mvnw test -Dtest=ArchitectureLayerTest,DocumentationGovernanceTest`)

**Target Platform**: Linux CI/CD and local Maven build

**Project Type**: Hexagonal microservice (`search-biological-objects`)

**Performance Goals**: Governance tests execute in < 5 seconds total

**Constraints**: Must pass against the current codebase (Requirements 01–05 already implemented); zero production source modifications required

**Scale/Scope**: 1 updated ArchUnit test class (`ArchitectureLayerTest.java`), 1 new governance test class (`DocumentationGovernanceTest.java`), and 3 governance markdown files

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **I. Hexagonal Layer Isolation (NON-NEGOTIABLE)**: PASS. Directly automates enforcement of Principle I in `ArchitectureLayerTest`.
- **II. Use Cases & Application Services Encapsulation**: PASS. Enforces `application.usecase..` isolation from `application.services.impl..`.
- **III. Zero Business Logic in Delivery & Outbound Adapters**: PASS. Enforces framework isolation across layers.
- **IV. Java 21 Clean Code & Method Discipline**: PASS. Uses clean Java 21 `Files` / `Path` APIs and small helper methods in `DocumentationGovernanceTest`.
- **V. Stateless Pure Mappers & Naming Conventions**: PASS. Directly automates verification of `..mappers..` finality, private constructors, and absence of CDI scope annotations.

## Project Structure

### Documentation (this feature)

```text
specs/006-arch-docs-governance-tests/
├── plan.md              # This file (/speckit-plan command output)
├── research.md          # Phase 0 output (/speckit-plan command)
├── data-model.md        # Phase 1 output (/speckit-plan command)
├── quickstart.md        # Phase 1 output (/speckit-plan command)
└── tasks.md             # Phase 2 output (/speckit-tasks command)
```

### Source Code (repository root)

```text
src/test/java/archtest/
├── ArchitectureLayerTest.java           # [MODIFY] Expand framework isolation & add static mapper purity rule
└── DocumentationGovernanceTest.java     # [NEW] Validate 1:1 UseCase <-> .agents/use_cases/*.md, README, architecture.md

.agents/
├── architecture.md                      # [MODIFY] Add DocumentationGovernanceTest to verification gates
└── architecture-rules.md                # [MODIFY] Add DocumentationGovernanceTest to Section 6 gates

.specify/memory/
└── constitution.md                      # [MODIFY] Add DocumentationGovernanceTest to Development Workflow & Quality Gates
```

**Structure Decision**: Governance tests reside in `src/test/java/archtest/` alongside `ArchitectureLayerTest`.

## Complexity Tracking

No constitutional violations exist.
