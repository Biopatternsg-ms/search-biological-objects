# Internal Ports & Use Case Contracts

**Feature**: `003-use-cases-hygiene-and-parameter-encapsulation` | **Date**: 2026-11-22

## 1. Outbound Port: `ConfigAndControlRepository`

**Package**: `com.biopatternsg.domain.port.out.repositories`

### Updated Signature

```java
public interface ConfigAndControlRepository {
    void updatePipelineStep(PipelineStepUpdate update);
}
```

- **Previous 4-parameter method removed**: `updatePipelineStep(String pipelineId, PipelineSteps pipelineStep, Status status, Map<String, String> metrics)`
- **Adapter Behavior (`ConfigAndControlAdapter`)**: Maps `PipelineStepUpdate` fields (`pipelineId()`, `step()`, `status()`, `metrics()`) into `PipelineStepInternalRequest` and invokes `queryConfigAndControl.updatePipelineStep(request, sessionUtil.getUserId())`.

---

## 2. Use Case: `FindBiologicalObjectFatherBrothersAndSonsUseCase`

**Inbound Port**: `com.biopatternsg.domain.port.in.FindBiologicalObjectFatherBrothersAndSons`
**Signature**: `List<BiologicalObject> execute(String pipelineId, String biologicalObjectId)`

### Behavioral Contract
1. **Missing `MinedObject` or `BiologicalObject`**: If `minedObjectRepository.find(biologicalObjectId, pipelineId)` returns `null` or `biologicalObjectRepository.findById(currentMinedObject.getBiologicalObjectId())` returns `null`, return `List.of()`.
2. **Root Node (`biologicalObjectParentId == null`)**: Skip father and sibling lookups; return deduplicated list containing `currentBiologicalObject` and its direct sons (if any).
3. **Non-Root Node (`biologicalObjectParentId != null`)**:
   - Fetch father (include if non-null).
   - Fetch brothers sharing `biologicalObjectParentId` in `pipelineId`, excluding `currentBiologicalObject.getId()`.
   - Fetch sons whose `biologicalObjectParentId` equals `currentBiologicalObject.getId()`.
   - Return deduplicated `List<BiologicalObject>`.

---

## 3. Use Case: `UpdateBiologicalObjectMeshIdUseCase`

**Inbound Port**: `com.biopatternsg.domain.port.in.UpdateBiologicalObjectMeshId`
**Signature**: `void execute(String biologicalObjectId, String meshId)`

### Behavioral Contract
1. Look up `BiologicalObject` via `biologicalObjectRepository.findById(biologicalObjectId)`.
2. **If `null`**: Throw `ApiException(GeneralError.NOT_FOUND, "Biological object not found with id: " + biologicalObjectId)`.
3. **If found**: Set `meshId`, call `biologicalObjectRepository.update(biologicalObject)`, and log info.

---

## 4. Use Case: `LaunchPipelineUseCase`

**Inbound Port**: `com.biopatternsg.domain.port.in.LaunchPipeline`
**Signature**: `void execute(PipelineConfig pipelineConfig)`

### Behavioral Contract
- Depends solely on `PipelineService`.
- Measures execution duration and logs it via SLF4J `log.info(...)` (zero `System.out.println` statements).
