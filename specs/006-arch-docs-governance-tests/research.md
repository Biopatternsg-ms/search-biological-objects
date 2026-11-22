# Research & Design Decisions: Architecture & Documentation Governance Tests

**Feature**: `006-arch-docs-governance-tests` | **Date**: 2026-11-22

## 1. Extending `ArchitectureLayerTest.java`

- **Decision**:
  - Update `domainLayerFrameworkIsolationTest` (or keep method name and expand packages) to forbid `jakarta.ws.rs..`, `jakarta.enterprise..`, `jakarta.inject..`, `io.quarkus..`, `org.bson..`, and `org.eclipse.microprofile..` in `com.biopatternsg.domain..`.
  - Update `applicationLayerFrameworkIsolationTest` to forbid `jakarta.ws.rs..`, `io.quarkus.mongodb..`, and `org.bson..` in `com.biopatternsg.application..`.
  - Retain `useCaseIsolationFromServiceImplTest` (`application.usecase..` must not depend on `application.services.impl..`).
  - Add `staticMappersShouldBeStatelessFinalUtilityClassesTest`: Using an ArchUnit `ArchCondition<JavaClass>`, verify every top-level class residing in `..mappers..` (and not in `..exceptions..`) has `Modifier.FINAL`, has at least one declared constructor and all declared constructors are `Modifier.PRIVATE`, and is not annotated with `ApplicationScoped`, `Singleton`, or `RequestScoped`.
- **Rationale**:
  - `ArchitectureLayerTest` already had 10 tests; expanding the existing 2 framework tests and adding 1 static mapper test brings total ArchUnit rules to 11 without duplicating existing rules.

## 2. `DocumentationGovernanceTest.java` Design

- **Decision**:
  - Scan `src/main/java/com/biopatternsg/application/usecase` for `*UseCase.java` files (sorted).
  - Verify for each `<UseCaseName>.java`:
    1. `.agents/use_cases/<UseCaseName>.md` exists.
    2. Content of `<UseCaseName>.md` contains `sequenceDiagram`.
    3. `.agents/use_cases/README.md` contains `<UseCaseName>.md`.
    4. Section 5 of `.agents/architecture.md` (substring from `## 5.` to next `## ` heading) contains `<UseCaseName>`.
  - Scan `.agents/use_cases/*.md` (excluding `README.md`) and assert that every `.md` filename corresponds to a `<Name>.java` in `src/main/java/com/biopatternsg/application/usecase/`.
- **Rationale**:
  - Matches the exact structure of `.agents/use_cases/README.md` and `.agents/architecture.md` Section 5 (`## 5. Casos de Uso Principales`), ensuring zero false positives and zero orphan documentation.
