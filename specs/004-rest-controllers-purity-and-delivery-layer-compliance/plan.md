# Implementation Plan: REST Controllers Purity and Delivery Layer Compliance

**Branch**: `feature/04-rest-controllers-purity-and-delivery-layer-compliance` | **Date**: 2026-11-22 | **Spec**: [spec.md](file:///home/yacson/Proyectos/biopatternsg-ms/services/search-biological-objects/specs/004-rest-controllers-purity-and-delivery-layer-compliance/spec.md)

**Input**: Feature specification from `/specs/004-rest-controllers-purity-and-delivery-layer-compliance/spec.md`

## Summary

Bring all 5 inbound REST controllers (`BiologicalObjectController`, `PipelineController`, `TranscriptionFactorController`, `ComplexController`, `BlatController`) into full compliance with Hexagonal Architecture (Constitution Principle III) and Clean Code / Pure Mapper rules (Principles IV & V):
1. Remove unused injected fields (`SessionUtil` in `BiologicalObjectController`, `ManagedExecutor` in `PipelineController`).
2. Remove the manual `try/catch (Exception e)` in `BiologicalObjectController#updateMeshId` so `ApiException(GeneralError.NOT_FOUND)` propagates to `ApiExceptionMapper`.
3. Extract `Arc.container().requestContext()` lifecycle management, `SessionUtil` header propagation, and `CompletableFuture.runAsync` from `PipelineController#experiment` into a dedicated `AsyncPipelineDispatcher` component, and return a typed `LaunchPipelineResponse` record instead of raw JSON string concatenation.
4. Introduce stateless `public final` static mappers under `com.biopatternsg.infrastructure.adapters.mappers.*` (`BiologicalObjectDtoMapper`, `PipelineDtoMapper`, `TranscriptionFactorMapper`, `ComplexDtoMapper`, `BlatDtoMapper`) and delivery DTO records under `com.biopatternsg.infrastructure.adapters.dtos.*` so zero raw domain models are exposed or constructed inline in controllers while preserving 100% of existing JSON property names, HTTP paths, and status codes.
5. Document the consolidated DTO package boundary (`infrastructure.adapters.dtos` for REST delivery vs `infrastructure.dtos` for HTTP client integration) in `.agents/architecture.md` and `.agents/architecture-rules.md`.

## Technical Context

**Language/Version**: Java 21 (`maven.compiler.release` 21)

**Primary Dependencies**: Quarkus 3.31.4 (`quarkus-bom`), Jakarta REST (`jakarta.ws.rs.*`), Quarkus ArC (`ManagedContext`), Lombok 1.18.36, Bean Validation (`@Valid`)

**Storage**: MongoDB with Panache (unchanged; no persistence changes)

**Testing**: JUnit 5, Mockito, ArchUnit (`archtest.ArchitectureLayerTest`)

**Target Platform**: Linux microservice container (`search-biological-objects`)

**Project Type**: Hexagonal microservice (Domain / Application / Infrastructure)

**Performance Goals**: Zero additional latency overhead in REST marshaling; async pipeline dispatch remains non-blocking (`202 Accepted`)

**Constraints**: Preserve 100% of existing HTTP routes, verbs, status codes, and JSON field names; zero business logic or inline multi-arg domain construction in controllers; stateless pure static mappers with private constructors

**Scale/Scope**: 5 REST Controllers, 1 new Async Dispatcher, 5 new Static Mapper classes, 8+ Delivery DTO records, and 2 `.agents/*.md` architecture docs

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **I. Hexagonal Layer Isolation (NON-NEGOTIABLE)**: PASS. All changes reside strictly within `com.biopatternsg.infrastructure.*`. No `domain.*` or `application.*` dependencies on `infrastructure.*` are introduced.
- **II. Use Cases & Application Services Encapsulation**: PASS. Controllers continue to depend exclusively on `domain.port.in.*` Inbound Ports.
- **III. Zero Business Logic in Delivery & Outbound Adapters**: PASS. Removes manual `try/catch` in `BiologicalObjectController#updateMeshId` (restoring `ApiExceptionMapper` handling) and extracts `ManagedContext`/async orchestration from `PipelineController` into `AsyncPipelineDispatcher`.
- **IV. Java 21 Clean Code & Method Discipline**: PASS. All new DTOs are Java 21 `record`s (automatic `toString()`, immutable); unused controller fields are removed; multi-line lambda removed from `PipelineController`.
- **V. Stateless Pure Mappers & Naming Conventions**: PASS. New mappers in `infrastructure.adapters.mappers` are `public final` with private constructors, `public static` null-safe pure functions, and zero CDI annotations. Controller naming (`*Controller`) is preserved.

## Project Structure

### Documentation (this feature)

```text
specs/004-rest-controllers-purity-and-delivery-layer-compliance/
├── plan.md              # This file (/speckit-plan command output)
├── research.md          # Phase 0 output (/speckit-plan command)
├── data-model.md        # Phase 1 output (/speckit-plan command)
├── quickstart.md        # Phase 1 output (/speckit-plan command)
├── contracts/           # Phase 1 output (/speckit-plan command)
│   └── rest-delivery-contracts.md
└── tasks.md             # Phase 2 output (/speckit-tasks command)
```

### Source Code (repository root)

```text
src/
├── main/java/com/biopatternsg/infrastructure/
│   └── adapters/
│       ├── dtos/
│       │   ├── BiologicalObjectDetailDTO.java         # [NEW] Full biological object response DTO
│       │   ├── BiologicalObjectConfigDTO.java         # [NEW] Expert object config request DTO
│       │   ├── LaunchPipelineRequest.java             # [NEW] Pipeline launch request DTO
│       │   ├── LaunchPipelineResponse.java            # [NEW] Typed launch response DTO
│       │   ├── TranscriptionFactorDTO.java            # [NEW] Transcription factor response DTO
│       │   ├── TranscriptionFactorConfigRequest.java  # [NEW] TF config request DTO
│       │   ├── ComplexDTO.java                        # [NEW] Complex response DTO
│       │   └── BlatSearchOptionDTO.java               # [NEW] BLAT search option response DTO
│       ├── mappers/
│       │   ├── BiologicalObjectDtoMapper.java         # [NEW] Static pure mapper
│       │   ├── PipelineDtoMapper.java                 # [NEW] Static pure mapper
│       │   ├── TranscriptionFactorMapper.java         # [NEW] Static pure mapper
│       │   ├── ComplexDtoMapper.java                  # [NEW] Static pure mapper
│       │   └── BlatDtoMapper.java                     # [NEW] Static pure mapper
│       └── in/
│           ├── AsyncPipelineDispatcher.java           # [NEW] Async context propagation & dispatch
│           └── restcontrollers/
│               ├── BiologicalObjectController.java    # [MODIFY] Remove SessionUtil, try/catch, use DetailDTO
│               ├── PipelineController.java            # [MODIFY] Remove ManagedExecutor, Arc, JSON concat
│               ├── TranscriptionFactorController.java # [MODIFY] Use TranscriptionFactorMapper & DTOs
│               ├── ComplexController.java             # [MODIFY] Return List<ComplexDTO>
│               └── BlatController.java                # [MODIFY] Return List<BlatSearchOptionDTO>
└── test/java/com/biopatternsg/infrastructure/adapters/
    ├── in/restcontrollers/
    │   ├── BiologicalObjectControllerTest.java        # [NEW] Unit tests
    │   └── PipelineControllerTest.java                # [NEW] Unit tests
    └── mappers/
        └── DeliveryMappersTest.java                   # [NEW] Null-safety & mapping unit tests
```

**Structure Decision**: Existing Quarkus hexagonal package layout. All REST delivery DTOs are organized under `infrastructure.adapters.dtos`, static delivery mappers under `infrastructure.adapters.mappers`, and async dispatch helper under `infrastructure.adapters.in`. Outbound HTTP client DTOs (`JasparRegion`, `PipelineStepInternalRequest`, etc.) remain in `infrastructure.dtos`.

## Complexity Tracking

No constitutional violations exist.
