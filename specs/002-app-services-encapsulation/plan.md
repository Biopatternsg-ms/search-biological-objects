# Implementation Plan: Application Services Encapsulation and Strategy Abstraction

**Branch**: `002-app-services-encapsulation` | **Date**: 2026-11-22 | **Spec**: [specs/002-app-services-encapsulation/spec.md](file:///home/yacson/Proyectos/biopatternsg-ms/services/search-biological-objects/specs/002-app-services-encapsulation/spec.md)

**Input**: Feature specification from `specs/002-app-services-encapsulation/spec.md`

## Summary

Refactor Application Services and Use Cases to adhere to Principle II (*Use Cases & Application Services Encapsulation*) and Principle IV (*Java 21 Clean Code & Method Discipline*) of the project constitution. Specifically:
1. Abstract biological object resolution behind a public interface `BiologicalObjectSearchService`, decoupling `FindBiologicalObjectUseCase` and peer services (`ExpertObjectServicesImpl`, `TranscriptionFactorsServiceImpl`, etc.) from concrete classes in `application.services.impl.*`.
2. Encapsulate `BiologicalObjectSearch` and `DiscoveryObjectContext` with `private final` fields, wiring chain links once during construction to eliminate per-request mutation and concurrency hazards in `@ApplicationScoped` singletons.
3. Replace production assertions (`assert value != null;`) in strategy context classes with explicit domain validations (`ApiException` with `GeneralError.BAD_REQUEST`).
4. Enforce method visibility discipline (`executeTFBind` and `firstLevel` made `private`), add `@Override` annotations, and remove dead commented-out code overloads in `DiscoveryObjectService`.
5. Add ArchUnit rules to permanently enforce that use cases do not depend on `application.services.impl.*`.

## Technical Context

**Language/Version**: Java 21 (OpenJDK 21.0.13)
**Primary Dependencies**: Quarkus 3.31.4, ArchUnit 1.4.1, Lombok 1.18.36
**Storage**: MongoDB (Panache) - not affected
**Testing**: JUnit 5, ArchUnit, Mockito (`./mvnw test`)
**Target Platform**: Linux server (JVM containerized microservice)
**Project Type**: Microservice (Hexagonal Architecture / Ports & Adapters)
**Performance Goals**: High-concurrency safety; eliminate race conditions and memory hazards in singleton beans
**Constraints**: Must strictly adhere to Constitution Principles II (Use Cases & Application Services Encapsulation) and IV (Java 21 Clean Code)
**Scale/Scope**: 1 new interface, refactoring 7 existing classes/interfaces, updating 1 test class

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principle | Requirement | Plan Status | Notes |
|-----------|-------------|-------------|-------|
| **I. Hexagonal Layer Isolation** | Domain pure business core; Application depends only on domain; Infrastructure interacts through inbound ports. | **PASS** | Maintains pure layer isolation; use cases do not cross into infrastructure. |
| **II. Use Cases & Application Services Encapsulation** | Use cases act as lightweight orchestrators; multi-step strategies reside in Application Services; Use cases MUST NOT depend on `impl.*`. | **PASS** | Use cases depend strictly on `application.services.*` interfaces. ArchUnit rule enforces boundary. |
| **III. Zero Business Logic in Delivery & Outbound Adapters** | Adapters remain thin and free of business rules. | **PASS** | No changes to delivery/adapters; application layer encapsulation strengthened. |
| **IV. Java 21 Clean Code & Method Discipline** | Max 3 parameters; constructor injection; private helpers; explicit override; no mutable public fields. | **PASS** | All strategy fields converted to `private final`; helper methods made `private`; `@Override` added. |
| **V. Stateless Pure Mappers & Naming Conventions** | ArchUnit naming conventions (`*UseCase`, `*Controller`, `*Adapter`). | **PASS** | Preserves all naming conventions. |

## Project Structure

### Documentation (this feature)

```text
specs/002-app-services-encapsulation/
├── spec.md              # Feature specification
├── plan.md              # This implementation plan
├── research.md          # Phase 0 research decisions
├── data-model.md        # Phase 1 data model & service specifications
├── quickstart.md        # Phase 1 quickstart & verification guide
├── contracts/           # Phase 1 contracts
│   └── biological-object-search-service-contract.md
└── checklists/
    └── requirements.md  # Quality checklist
```

### Source Code (repository layout)

```text
src/main/java/com/biopatternsg/application/
├── services/
│   ├── BiologicalObjectSearchService.java           # [NEW] Public service interface
│   └── DiscoveryObjectService.java                  # [EDIT] Remove dead execute overload
├── usecase/
│   └── FindBiologicalObjectUseCase.java             # [EDIT] Inject BiologicalObjectSearchService
└── services/impl/
    ├── ExpertObjectServicesImpl.java                # [EDIT] Inject BiologicalObjectSearchService
    ├── TranscriptionFactorsServiceImpl.java         # [EDIT] Inject service; executeTFBind -> private
    ├── PipelineServiceImpl.java                     # [EDIT] Add @Override; firstLevel -> private
    ├── DiscoveryObjectServiceImpl.java              # [EDIT] Remove dead execute method
    ├── UnmatchedSynonymServiceImpl.java             # [EDIT] Inject BiologicalObjectSearchService
    ├── biological_object_strategy/
    │   ├── BiologicalObjectSearch.java              # [EDIT] Implement service; private final; wire once
    │   └── BiologicalObjectContext.java             # [EDIT] Replace assert with ApiException
    └── discovery_object_strategy/
        ├── DiscoveryObjectContext.java              # [EDIT] private final field; replace assert
        └── DiscoveryObjectByPdb.java                # [EDIT] Inject BiologicalObjectSearchService

src/test/java/
├── archtest/
│   └── ArchitectureLayerTest.java                   # [EDIT] Add usecase -> impl isolation test
└── com/biopatternsg/application/services/impl/
    └── TranscriptionFactorsServiceImplTest.java    # [EDIT] Update mock to BiologicalObjectSearchService
```

**Structure Decision**: Hexagonal Architecture single-module structure under `application.services` and `application.services.impl`.

## Complexity Tracking

> **No Constitution violations. All gates PASS.**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | N/A | N/A |
