# Data Model: Persistence Mapper Null-Safety & `@ToString` Additions

**Feature**: `005-clean-code-lambdas-di-mappers` | **Date**: 2026-11-22

## 1. Persistence Mappers (`com.biopatternsg.infrastructure.mongo_db.mappers`)

| Class | Structure | Null-Safety Contract |
|---|---|---|
| `MinedObjectMapper` | `public final class`, `private MinedObjectMapper()` | `toMinedObject(null)` -> `null`; `id != null ? id.toString() : null`; `toMinedObjectCollection(null)` -> `null` (sets `ObjectId` when `ObjectId.isValid(id)`); `toMinedObjects(null)` -> `List.of()` |
| `BiologicalObjectMapper` | `public final class`, `private BiologicalObjectMapper()` | `toBiologicalObject(null)` -> `null`; `id != null ? id.toString() : null`; `toBiologicalObjectCollection(null)` -> `null`; `toBiologicalObjects(null)` -> `List.of()` |

## 2. `@ToString` Additions

Added `@ToString` to:
- `domain.models`: `BiologicalObject`, `Complex`, `GeneOntology`, `MinedObject`
- `domain.models.external_entities`: `HGNCResponse`, `UniprotResponse`
- `domain.models.pipeline_config`: `BiologicalObjectConfig`, `DiscoveryObjectConfig`, `MinedObjectConfig`, `PipelineConfig`, `TranscriptionFactorConfig`
- `infrastructure.dtos`: `JasparRegion`
