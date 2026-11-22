# Implementation Plan: Domain Layer Purity and Framework Decoupling

**Branch**: `001-domain-layer-purity` | **Date**: 2026-11-22 | **Spec**: [specs/001-domain-layer-purity/spec.md](file:///home/yacson/Proyectos/biopatternsg-ms/services/search-biological-objects/specs/001-domain-layer-purity/spec.md)

**Input**: Feature specification from `specs/001-domain-layer-purity/spec.md`

## Summary

Decouple the `com.biopatternsg.domain` and `com.biopatternsg.application` layers from `jakarta.ws.rs.*` web framework classes to satisfy Hexagonal Architecture purity (Principle I of the Constitution). Specifically:
1. Update `OntologiesRepository` to return `void` (pure Java type), moving JAX-RS `Response` handling and stream closing into `OntologiesAdapter`.
2. Remove `Response.Status` and `httpStatusCode` from `GeneralError`, deleting `CustomStatus.java`, and update `ApiExceptionMapper` to resolve HTTP statuses directly via integer codes (`Response.status(int)`), ensuring HTTP 422 maps accurately without runtime defects.
3. Enhance `ArchitectureLayerTest` with automated ArchUnit rules to permanently prevent framework imports in domain and application packages.

## Technical Context

**Language/Version**: Java 21 (OpenJDK 21.0.13)
**Primary Dependencies**: Quarkus 3.31.4, ArchUnit 1.4.1, Lombok 1.18.36, Jakarta REST
**Storage**: MongoDB (Panache) - not affected by this refactoring
**Testing**: JUnit 5, ArchUnit, Quarkus Test (`./mvnw test`)
**Target Platform**: Linux server (JVM containerized microservice)
**Project Type**: Microservice (Hexagonal Architecture / Ports & Adapters)
**Performance Goals**: Zero performance overhead; eliminate connection leaks via safe `try-with-resources` response closing in outbound adapters
**Constraints**: Must strictly adhere to Constitution Principles I (Hexagonal Layer Isolation) and III (Zero Business Logic in Delivery/Outbound Adapters)
**Scale/Scope**: Refactoring 4 source files, deleting 1 file, and updating 1 test class

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principle | Requirement | Plan Status | Notes |
|-----------|-------------|-------------|-------|
| **I. Hexagonal Layer Isolation** | Domain MUST NOT depend on application, infrastructure, or web/persistence frameworks (`jakarta.ws.rs.*`). Application MUST NOT depend on infrastructure. | **PASS** | Removes all 4 `jakarta.ws.rs` imports from `domain` and `application`. Adds ArchUnit rules to enforce isolation. |
| **II. Use Cases & Application Services Encapsulation** | Use cases coordinate outbound ports or application services; single-responsibility. | **PASS** | `OntologiesService` and `OntologiesServiceImpl` maintain clean orchestration without transport knowledge. |
| **III. Zero Business Logic in Delivery & Outbound Adapters** | Adapters encapsulate transport details and framework lifecycle; controllers only invoke inbound ports. | **PASS** | `OntologiesAdapter` encapsulates JAX-RS `Response` lifecycle and entity extraction. `ApiExceptionMapper` handles HTTP status translation. |
| **IV. Java 21 Clean Code & Method Discipline** | Max 3 parameters; constructor injection; clean null safety. | **PASS** | Method signatures remain ≤ 1 parameter; uses try-with-resources. |
| **V. Stateless Pure Mappers & Naming Conventions** | Static pure mappers; naming conventions (UseCase, Controller, Adapter). | **PASS** | Preserves all existing conventions; zero new stateful mappers. |

## Project Structure

### Documentation (this feature)

```text
specs/001-domain-layer-purity/
├── spec.md              # Feature specification
├── plan.md              # This implementation plan
├── research.md          # Phase 0 research decisions
├── data-model.md        # Phase 1 data model & entity specifications
├── quickstart.md        # Phase 1 quickstart & verification guide
├── contracts/           # Phase 1 contracts
│   ├── error-handling-contract.md
│   └── ontologies-port-contract.md
└── checklists/
    └── requirements.md  # Quality checklist
```

### Source Code (repository layout)

```text
src/main/java/com/biopatternsg/
├── domain/
│   ├── exceptions/
│   │   ├── ApiException.java
│   │   ├── GeneralError.java              # [EDIT] Remove JAX-RS Status import & field
│   │   └── CustomStatus.java              # [DELETE] Remove file
│   └── port/out/external_repositories/
│       └── OntologiesRepository.java      # [EDIT] Return void; remove JAX-RS Response import
├── application/
│   └── services/impl/
│       └── OntologiesServiceImpl.java     # [EDIT] Remove JAX-RS Response import & entity reading
└── infrastructure/
    ├── adapters/out/external_repositories/
    │   └── OntologiesAdapter.java         # [EDIT] Encapsulate JAX-RS Response reading & closing
    └── exceptions/
        └── ApiExceptionMapper.java        # [EDIT] Map GeneralError code to Response.status(code)

src/test/java/
└── archtest/
    └── ArchitectureLayerTest.java         # [EDIT] Add ArchUnit framework isolation tests
```

**Structure Decision**: Standard Maven/Quarkus single-module layout following hexagonal architecture packages (`domain`, `application`, `infrastructure`).

## Complexity Tracking

> **No Constitution violations. All gates PASS.**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | N/A | N/A |
