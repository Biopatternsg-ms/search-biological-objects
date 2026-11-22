# Data Model: Use Cases Hygiene & Parameter Encapsulation

**Feature**: `003-use-cases-hygiene-and-parameter-encapsulation` | **Date**: 2026-11-22

## 1. New Domain Record: `PipelineStepUpdate`

- **Package**: `com.biopatternsg.domain.models`
- **Type**: Java 21 `public record`
- **Purpose**: Encapsulates pipeline step execution state transitions and optional metrics sent to the `config-and-control` service, eliminating 4-parameter method signatures on outbound ports and adapters.

| Field | Type | Constraints / Nullability | Description |
|---|---|---|---|
| `pipelineId` | `String` | Non-null, non-blank | Identifier of the pipeline execution |
| `step` | `PipelineSteps` | Non-null | Current pipeline execution step |
| `status` | `Status` | Non-null | Step execution status (`IN_PROGRESS`, `COMPLETED`, etc.) |
| `metrics` | `Map<String, String>` | Nullable | Optional key-value execution metrics for the step |

**Constructors**:
- Canonical: `PipelineStepUpdate(String pipelineId, PipelineSteps step, Status status, Map<String, String> metrics)`
- Convenience: `PipelineStepUpdate(String pipelineId, PipelineSteps step, Status status)` -> delegates with `metrics = null`.

---

## 2. Internal Context Record: `MinedObjectCreationContext`

- **Scope**: `private record` inside `com.biopatternsg.application.services.impl.PipelineServiceImpl`
- **Purpose**: Encapsulates hierarchical context for instantiating `MinedObject` instances during pipeline execution, reducing `newObjects` and `buildMinedObject` from 4 parameters to 2 parameters.

| Field | Type | Constraints / Nullability | Description |
|---|---|---|---|
| `pipelineId` | `String` | Non-null | Identifier of the pipeline execution |
| `parentId` | `String` | Nullable (`null` for level 1 root nodes) | Identifier of the parent `BiologicalObject` |
| `level` | `int` | `>= 1` | Depth level in the biological discovery tree |

---

## 3. Existing Domain Models Referenced (Unchanged)

- **`BiologicalObject`** (`com.biopatternsg.domain.models.BiologicalObject`): Target entity for MeSH ID updates and lineage aggregation.
- **`MinedObject`** (`com.biopatternsg.domain.models.MinedObject`): Represents a discovered node in a pipeline with `biologicalObjectId`, `biologicalObjectParentId`, `pipelineId`, and `level`.
- **`ApiException` & `GeneralError`** (`com.biopatternsg.domain.exceptions.*`): Domain exception thrown with `GeneralError.NOT_FOUND` (`404`) when a `BiologicalObject` is missing during MeSH update.
