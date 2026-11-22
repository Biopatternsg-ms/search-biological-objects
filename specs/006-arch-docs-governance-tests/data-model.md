# Data Model: Governance Rules & Verified Artifacts

**Feature**: `006-arch-docs-governance-tests` | **Date**: 2026-11-22

## 1. ArchUnit Rules (`ArchitectureLayerTest`)

| Rule | Target Package | Constraint |
|---|---|---|
| `domainLayerFrameworkIsolationTest` | `com.biopatternsg.domain..` | No dependencies on `jakarta.ws.rs..`, `jakarta.enterprise..`, `jakarta.inject..`, `io.quarkus..`, `org.bson..`, `org.eclipse.microprofile..` |
| `applicationLayerFrameworkIsolationTest` | `com.biopatternsg.application..` | No dependencies on `jakarta.ws.rs..`, `io.quarkus.mongodb..`, `org.bson..` |
| `useCaseIsolationFromServiceImplTest` | `com.biopatternsg.application.usecase..` | No dependencies on `com.biopatternsg.application.services.impl..` |
| `staticMappersShouldBeStatelessFinalUtilityClassesTest` | `..mappers..` (excl. `..exceptions..`) | `FINAL`, `PRIVATE` constructor(s), no `@ApplicationScoped`/`@Singleton`/`@RequestScoped` |

## 2. Documentation Governance Rules (`DocumentationGovernanceTest`)

- **Source directory**: `src/main/java/com/biopatternsg/application/usecase` (`*UseCase.java`)
- **Docs directory**: `.agents/use_cases` (`*.md`, excluding `README.md`)
- **Indexes**: `.agents/use_cases/README.md` and `.agents/architecture.md` (Section 5)
