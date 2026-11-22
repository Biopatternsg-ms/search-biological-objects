# Research & Design Decisions: Use Cases Hygiene & Parameter Encapsulation

**Feature**: `003-use-cases-hygiene-and-parameter-encapsulation` | **Date**: 2026-11-22

## 1. Missing `currentMinedObject` Behavior in `FindBiologicalObjectFatherBrothersAndSonsUseCase`

- **Decision**: Return `List.of()` when `minedObjectRepository.find(biologicalObjectId, pipelineId)` returns `null` (or when `currentBiologicalObject` is `null`).
- **Rationale**:
  - Matches existing list-returning query use cases in the service (`FindBiologicalObjectsByPipelineIdAndLevelUseCase` returns `List.of()` when `byLevel.isEmpty()`).
  - Complies with Constitution Principle IV (*return empty collections `List.of()` instead of `null` for list return types*).
  - Avoids breaking exploration UI clients expecting an empty JSON array `[]` when a biological object is not yet mined in a given pipeline.
- **Alternatives Considered**:
  - *Throwing `ApiException(GeneralError.NOT_FOUND)`*: Rejected for this list query because `BiologicalObjectController#getFatherBrothersAndSons` returns `List<BiologicalObjectDTO>`, and sibling list endpoints return `[]` on empty lookups.

## 2. Root-Level Node (`biologicalObjectParentId == null`) Handling

- **Decision**: Guard parent and brother resolution behind `if (currentMinedObject.getBiologicalObjectParentId() != null)`. When `null`, skip both `minedObjectRepository.findByParentId(parentId, pipelineId)` and `biologicalObjectRepository.findById(parentId)`, returning only the current biological object and its direct sons. Also guard `currentFather != null` and `currentBiologicalObject != null` before calling `.getId()` or inserting into the result map.
- **Rationale**: Root nodes (level 1 in `PipelineServiceImpl#firstLevel`) are persisted with `parentId = null`. Previously, passing `null` to `biologicalObjectRepository.findById(null)` and dereferencing `currentFather.getId()` caused an immediate `NullPointerException`.
- **Alternatives Considered**:
  - *Returning an empty list for root nodes*: Rejected because root nodes still have valid self-data and direct children (sons) that callers need to inspect.

## 3. Parameter Encapsulation for `ConfigAndControlRepository` & `PipelineServiceImpl`

- **Decision**:
  1. Create a public Java 21 `record PipelineStepUpdate(String pipelineId, PipelineSteps step, Status status, Map<String, String> metrics)` in `com.biopatternsg.domain.models`, with a convenience 3-argument constructor `PipelineStepUpdate(String pipelineId, PipelineSteps step, Status status)` delegating `metrics` as `null`.
  2. Replace `ConfigAndControlRepository#updatePipelineStep(String, PipelineSteps, Status, Map)` and `ConfigAndControlAdapter#updatePipelineStep(...)` with `updatePipelineStep(PipelineStepUpdate update)`. Retain the 3-parameter convenience method `updatePipelineStep(String pipelineId, PipelineSteps pipelineStep, Status status)` only if needed, or delegate it cleanly to `updatePipelineStep(new PipelineStepUpdate(pipelineId, pipelineStep, status))` — keeping both `<= 3` parameters. (Checking codebase references confirmed `PipelineServiceImpl` is the sole caller, so `updatePipelineStep(PipelineStepUpdate)` is sufficient and cleanest).
  3. Create a private nested `record MinedObjectCreationContext(String pipelineId, String parentId, int level)` inside `PipelineServiceImpl` so `newObjects(List<String> newObjectIds, MinedObjectCreationContext context)` and `buildMinedObject(String biologicalObjectId, MinedObjectCreationContext context)` take 2 parameters each.
- **Rationale**:
  - Aligns with existing domain record style (`PipelineUpdate.java` in `domain.models`).
  - Java `record` provides immutability, structural equality, and an automatic `toString()` (satisfying Constitution Principle IV).
  - Keeps `MinedObjectCreationContext` scoped to `PipelineServiceImpl` since it is an internal implementation detail of pipeline level construction.
- **Alternatives Considered**:
  - *Lombok `@Builder` class for `PipelineStepUpdate`*: Rejected because a Java `record` is more concise, immutable by default, and matches `PipelineUpdate`.

## 4. `UpdateBiologicalObjectMeshIdUseCase` Not-Found Error Signaling

- **Decision**: Throw `new ApiException(GeneralError.NOT_FOUND, "Biological object not found with id: " + biologicalObjectId)` when `biologicalObjectRepository.findById(biologicalObjectId)` returns `null`.
- **Rationale**: Prevents silent `void` return and false-positive `200 OK` responses; aligns with Constitution Principle II & III.
- **Alternatives Considered**:
  - *Returning a `boolean`*: Rejected because Inbound Port `UpdateBiologicalObjectMeshId` is a `void` command interface and domain exceptions are the established error-signaling mechanism (`ApiException`).
