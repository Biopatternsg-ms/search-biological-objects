# Implementation Plan: Use Cases Hygiene, Defensive Domain Validation, and Parameter Encapsulation

**Branch**: `feature/03-use-cases-hygiene-and-parameter-encapsulation` | **Date**: 2026-11-22 | **Spec**: [spec.md](file:///home/yacson/Proyectos/biopatternsg-ms/services/search-biological-objects/specs/003-use-cases-hygiene-and-parameter-encapsulation/spec.md)

**Input**: Feature specification from `/specs/003-use-cases-hygiene-and-parameter-encapsulation/spec.md`

## Summary

Remediate Clean Code, defensive validation, and parameter encapsulation violations across Use Cases, Application Services, and Outbound Ports/Adapters:
1. Remove the unused `DiscoveryObjectService` injection from `LaunchPipelineUseCase` and replace `System.out.println` with structured `@Slf4j` `log.info`.
2. Add null-safe guards in `FindBiologicalObjectFatherBrothersAndSonsUseCase` for missing `MinedObject` records (returning `List.of()`) and root-level nodes (`biologicalObjectParentId == null`), and extract lineage aggregation into single-responsibility private helper methods.
3. Throw `ApiException(GeneralError.NOT_FOUND, ...)` in `UpdateBiologicalObjectMeshIdUseCase` when the target `BiologicalObject` does not exist.
4. Encapsulate 4-parameter signatures into Java 21 `record` types: `PipelineStepUpdate` in `domain.models` (for `ConfigAndControlRepository`, `ConfigAndControlAdapter`, and `PipelineServiceImpl#updatePipelineStep`) and `MinedObjectCreationContext` inside `PipelineServiceImpl` (for `newObjects` and `buildMinedObject`).
5. Synchronize `.agents/use_cases/` documentation (`LaunchPipelineUseCase.md`, `FindBiologicalObjectFatherBrothersAndSonsUseCase.md`, `UpdateBiologicalObjectMeshIdUseCase.md`) and verify with `./mvnw clean compile` and `ArchitectureLayerTest`.

## Technical Context

**Language/Version**: Java 21 (`maven.compiler.release` 21)

**Primary Dependencies**: Quarkus 3.31.4 (`quarkus-bom`), Lombok 1.18.36, Jakarta CDI (`@ApplicationScoped`), SLF4J (`@Slf4j`)

**Storage**: MongoDB with Panache (no schema changes required)

**Testing**: JUnit 5, Mockito, ArchUnit (`archtest.ArchitectureLayerTest`)

**Target Platform**: Linux microservice container (`search-biological-objects`)

**Project Type**: Hexagonal microservice (Domain / Application / Infrastructure)

**Performance Goals**: Zero regression in lineage lookup latency (< 100ms for missing/root queries without unnecessary DB round-trips)

**Constraints**: Strict adherence to Hexagonal Layer Isolation, maximum 3 parameters per method, zero `System.out.println`, no MongoDB schema changes

**Scale/Scope**: 3 Use Cases, 1 Application Service (`PipelineServiceImpl`), 1 Outbound Port (`ConfigAndControlRepository`), 1 Outbound Adapter (`ConfigAndControlAdapter`), 1 new Domain Record (`PipelineStepUpdate`), and 3 `.agents/use_cases/*.md` files

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **I. Hexagonal Layer Isolation (NON-NEGOTIABLE)**: PASS. `PipelineStepUpdate` is placed in `com.biopatternsg.domain.models` and depends only on `com.biopatternsg.domain.enums.*` and `java.util.Map`. `Application` depends only on `Domain`. `Infrastructure` implements `ConfigAndControlRepository` without leaking infrastructure types.
- **II. Use Cases & Application Services Encapsulation**: PASS. Use cases remain lightweight orchestrators (`LaunchPipelineUseCase` drops unused `DiscoveryObjectService`; `UpdateBiologicalObjectMeshIdUseCase` throws `ApiException`; `FindBiologicalObjectFatherBrothersAndSonsUseCase` delegates to clean private helpers).
- **III. Zero Business Logic in Delivery & Outbound Adapters**: PASS. `ConfigAndControlAdapter` simply maps `PipelineStepUpdate` fields into `PipelineStepInternalRequest` and delegates to `QueryConfigAndControl`. (Note: `BiologicalObjectController#updateMeshId` currently wraps execution in a broad `try/catch`; delivery-layer exception handling cleanup is tracked in Requirement 04).
- **IV. Java 21 Clean Code & Method Discipline**: PASS. All methods across `application.usecase.*`, `application.services.*`, `domain.port.*`, and `infrastructure.adapters.*` will have `<= 3` parameters. `PipelineStepUpdate` is a Java `record` (providing an explicit `toString()`). Null-safe checks and `List.of()` returns replace NPE-prone dereferences.
- **V. Stateless Pure Mappers & Naming Conventions**: PASS. Existing `*UseCase` and `*Adapter` naming conventions are preserved; no mapper state is introduced.

## Project Structure

### Documentation (this feature)

```text
specs/003-use-cases-hygiene-and-parameter-encapsulation/
├── plan.md              # This file (/speckit-plan command output)
├── research.md          # Phase 0 output (/speckit-plan command)
├── data-model.md        # Phase 1 output (/speckit-plan command)
├── quickstart.md        # Phase 1 output (/speckit-plan command)
├── contracts/           # Phase 1 output (/speckit-plan command)
│   └── internal-ports-and-usecases.md
└── tasks.md             # Phase 2 output (/speckit-tasks command)
```

### Source Code (repository root)

```text
src/
├── main/java/com/biopatternsg/
│   ├── domain/
│   │   ├── models/
│   │   │   └── PipelineStepUpdate.java                       # [NEW] Domain record encapsulating step update
│   │   └── port/out/repositories/
│   │       └── ConfigAndControlRepository.java               # [MODIFY] Accept PipelineStepUpdate
│   ├── application/
│   │   ├── services/impl/
│   │   │   └── PipelineServiceImpl.java                      # [MODIFY] Add MinedObjectCreationContext record, use PipelineStepUpdate
│   │   └── usecase/
│   │       ├── LaunchPipelineUseCase.java                    # [MODIFY] Remove unused service, use @Slf4j
│   │       ├── FindBiologicalObjectFatherBrothersAndSonsUseCase.java # [MODIFY] Null-safe guards + helper extraction
│   │       └── UpdateBiologicalObjectMeshIdUseCase.java      # [MODIFY] Throw ApiException(GeneralError.NOT_FOUND)
│   └── infrastructure/
│       └── adapters/out/repositories/
│           └── ConfigAndControlAdapter.java                  # [MODIFY] Implement updated ConfigAndControlRepository
└── test/java/com/biopatternsg/
    └── application/usecase/
        ├── LaunchPipelineUseCaseTest.java                    # [NEW] Unit tests
        ├── FindBiologicalObjectFatherBrothersAndSonsUseCaseTest.java # [NEW] Unit tests
        └── UpdateBiologicalObjectMeshIdUseCaseTest.java      # [NEW] Unit tests
```

**Structure Decision**: Existing single-module Quarkus hexagonal microservice layout under `src/main/java/com/biopatternsg/` and `src/test/java/com/biopatternsg/`. No new modules or packages are needed.

## Complexity Tracking

No constitutional violations exist. All changes bring the codebase into full compliance with Principles I–V.
