# Implementation Plan: Clean Code Lambdas, Constructor Injection, Static Mappers, and `@ToString` Compliance

**Branch**: `feature/05-clean-code-lambdas-dependency-injection-and-static-mappers` | **Date**: 2026-11-22 | **Spec**: [spec.md](file:///home/yacson/Proyectos/biopatternsg-ms/services/search-biological-objects/specs/005-clean-code-lambdas-di-mappers/spec.md)

**Input**: Feature specification from `/specs/005-clean-code-lambdas-di-mappers/spec.md`

## Summary

Bring the codebase into full compliance with Constitution Principles IV and V (`java-rules.md` §§2.1, 2.2, 3.1, 3.2, 3.3, 4):
1. Replace `@Inject` and `@ConfigProperty` field injections with constructor injection (`private final` fields) in `BiologicalObjectRepositoryAdapter` and `PdbAdapter`.
2. Decompose multi-line `.map(...)` and `.forEach(...)` lambdas (with inline builders, branching, and nested loops) into single-expression method references (`this::helper`, `Mapper::method`) or clean private helper methods across `ExpertObjectServicesImpl`, `TranscriptionFactorsServiceImpl`, `BiologicalObjectRepositoryAdapter`, `MinedObjectRepositoryAdapter`, `PdbAdapter`, and `UniprotAdapter`.
3. Remove the duplicate `mongoObject.setBiologicalObjectId(...)` in `MinedObjectRepositoryAdapter#saveMinedCollection` and replace `.collect(Collectors.toList())` with `.toList()` in `PubmedIntegrationAdapter`.
4. Standardize `MinedObjectMapper` and `BiologicalObjectMapper` as `public final class` with explicit `private` constructors, null-safe static methods, and resolve `//TODO chequearID` in `MinedObjectMapper`.
5. Add `@ToString` to the 11 domain model classes under `domain/models/**` and `infrastructure/dtos/JasparRegion.java`.

## Technical Context

**Language/Version**: Java 21 (`maven.compiler.release` 21)

**Primary Dependencies**: Quarkus 3.31.4, Lombok 1.18.36, MicroProfile Config (`@ConfigProperty`), MongoDB Panache (`ObjectId`)

**Storage**: MongoDB with Panache (no schema changes)

**Testing**: JUnit 5, Mockito, ArchUnit (`archtest.ArchitectureLayerTest`)

**Target Platform**: Linux microservice container (`search-biological-objects`)

**Project Type**: Hexagonal microservice (Domain / Application / Infrastructure)

**Performance Goals**: Zero runtime performance regression; immutable `.toList()` in `PubmedIntegrationAdapter`

**Constraints**: Zero `@Inject` / `@ConfigProperty` field injections; zero multi-line lambdas with branching/builders; null-safe static mappers; explicit `toString()` on all domain models and DTOs; preserve `PdbAdapter#getComplexesDeprecated` signature

**Scale/Scope**: 2 Persistence Mappers, 2 Constructor-DI Adapters, 6 Lambda-refactored Services/Adapters, 1 Stream Collector fix, 12 `@ToString` additions, and Unit Tests

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **I. Hexagonal Layer Isolation (NON-NEGOTIABLE)**: PASS. All changes preserve `domain.*`, `application.*`, and `infrastructure.*` boundaries; `@ToString` is compile-time Lombok only.
- **II. Use Cases & Application Services Encapsulation**: PASS. Helper methods extracted in `ExpertObjectServicesImpl` and `TranscriptionFactorsServiceImpl` remain strictly `private`.
- **III. Zero Business Logic in Delivery & Outbound Adapters**: PASS. Refactoring adapter lambdas into private mapping/enrichment helpers preserves thin adapter responsibilities.
- **IV. Java 21 Clean Code & Method Discipline**: PASS. Directly remediates constructor injection, single-expression lambdas, null-safety, and explicit `toString()` across all target classes.
- **V. Stateless Pure Mappers & Naming Conventions**: PASS. `BiologicalObjectMapper` and `MinedObjectMapper` become `public final` with private constructors and null-safe pure static methods.

## Project Structure

### Documentation (this feature)

```text
specs/005-clean-code-lambdas-di-mappers/
├── plan.md              # This file (/speckit-plan command output)
├── research.md          # Phase 0 output (/speckit-plan command)
├── data-model.md        # Phase 1 output (/speckit-plan command)
├── quickstart.md        # Phase 1 output (/speckit-plan command)
└── tasks.md             # Phase 2 output (/speckit-tasks command)
```

### Source Code (repository root)

```text
src/
├── main/java/com/biopatternsg/
│   ├── domain/models/
│   │   ├── BiologicalObject.java, Complex.java, GeneOntology.java, MinedObject.java  # [MODIFY] @ToString
│   │   ├── external_entities/{HGNCResponse,UniprotResponse}.java                       # [MODIFY] @ToString
│   │   └── pipeline_config/{BiologicalObjectConfig,DiscoveryObjectConfig,MinedObjectConfig,PipelineConfig,TranscriptionFactorConfig}.java # [MODIFY] @ToString
│   ├── application/services/impl/
│   │   ├── ExpertObjectServicesImpl.java                                               # [MODIFY] Extract lambdas
│   │   └── TranscriptionFactorsServiceImpl.java                                        # [MODIFY] Extract lambdas
│   └── infrastructure/
│       ├── dtos/JasparRegion.java                                                      # [MODIFY] @ToString
│       ├── mongo_db/mappers/{BiologicalObjectMapper,MinedObjectMapper}.java            # [MODIFY] final + private ctor + null-safe
│       └── adapters/out/
│           ├── repositories/{BiologicalObjectRepositoryAdapter,MinedObjectRepositoryAdapter}.java # [MODIFY] DI + lambdas + dup fix
│           └── external_repositories/{PdbAdapter,UniprotAdapter,PubmedIntegrationAdapter}.java    # [MODIFY] DI + lambdas + toList()
└── test/java/com/biopatternsg/infrastructure/
    └── mongo_db/mappers/PersistenceMappersTest.java                                    # [NEW] Null-safety & mapping tests
```

**Structure Decision**: In-place Clean Code and mapper standardization across existing packages; no new packages needed.

## Complexity Tracking

No constitutional violations exist.
