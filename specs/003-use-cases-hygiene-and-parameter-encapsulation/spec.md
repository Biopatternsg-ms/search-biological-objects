# Feature Specification: Use Cases Hygiene, Defensive Domain Validation, and Parameter Encapsulation

**Feature Branch**: `feature/03-use-cases-hygiene-and-parameter-encapsulation`

**Created**: 2026-11-22

**Status**: Draft

**Input**: User description: "/speckit-specify basado en /home/yacson/Proyectos/biopatternsg-ms/SDD/biological-objects/03-use-cases-hygiene-and-parameter-encapsulation"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Defensive Lineage Resolution and Null-Safe Hierarchy Navigation (Priority: P1) 🎯 MVP

As a researcher or client application exploring hierarchical biological relationships,
I want the hierarchy retrieval use case to handle root nodes and missing mined objects defensively without runtime null dereferences or crashes,
So that hierarchical navigation (father, siblings, and offspring) is resilient, predictable, and cleanly returns empty results when an object is not part of a pipeline execution.

**Why this priority**: Unchecked null dereferences during hierarchy queries trigger `NullPointerException` when querying root objects (which have null parents) or non-existent objects, breaking client exploration workflows and visualization graphs.

**Independent Test**: Can be independently tested by querying a root-level biological object (with `biologicalObjectParentId == null`) and asserting that it returns the target object and its sons without throwing `NullPointerException`, and querying a non-existent object ID to verify it returns an empty list `List.of()`.

**Acceptance Scenarios**:

1. **Given** a biological object at the root level of a pipeline execution (`biologicalObjectParentId == null`), **When** `FindBiologicalObjectFatherBrothersAndSonsUseCase` executes, **Then** it safely skips parent and sibling lookups and returns a deduplicated collection containing only the target object and its direct descendants.
2. **Given** a biological object ID or pipeline ID that has no corresponding mined object record, **When** hierarchy resolution is requested, **Then** the use case returns an empty collection (`List.of()`) without dereferencing null fields.
3. **Given** complex multi-step lineage assembly, **When** resolving father, sibling, and son biological objects, **Then** the logic is decomposed into clean, single-responsibility private helper methods adhering to method discipline.

---

### User Story 2 - Encapsulate Method Signatures to Satisfy Clean Code Limits (Priority: P1)

As a software engineer and architect maintaining service boundaries,
I want repository outbound ports, outbound adapters, and internal pipeline service methods to accept at most 3 individual parameters,
So that method signatures comply with Clean Code discipline (Constitution Principle IV) and parameter clusters are encapsulated in cohesive domain records and context models.

**Why this priority**: Methods accepting 4 individual parameters (`ConfigAndControlRepository.updatePipelineStep`, `ConfigAndControlAdapter.updatePipelineStep`, `PipelineServiceImpl.newObjects`, `buildMinedObject`, and `updatePipelineStep`) violate the maximum 3-parameter rule, increasing cognitive complexity and risking parameter transposition bugs.

**Independent Test**: Can be independently verified by inspecting method signatures across `domain.port.out.repositories.*`, `infrastructure.adapters.out.repositories.*`, and `application.services.impl.*` to assert that zero methods exceed 3 parameters, and running pipeline step updates using the new encapsulated domain record.

**Acceptance Scenarios**:

1. **Given** a pipeline step status update requiring pipeline ID, step enum, status, and execution metrics, **When** passed to `ConfigAndControlRepository` or `ConfigAndControlAdapter`, **Then** these attributes are encapsulated in a single domain record (e.g., `PipelineStepUpdate`).
2. **Given** internal mined object creation in `PipelineServiceImpl`, **When** instantiating new mined objects across pipeline levels, **Then** creation context parameters (pipeline ID, parent ID, level) are encapsulated in a dedicated context record, ensuring all method signatures take at most 2 parameters.
3. **Given** any method in `application.usecase.*`, `application.services.*`, `domain.port.*`, or `infrastructure.adapters.*`, **When** signatures are analyzed, **Then** zero methods accept more than 3 individual parameters.

---

### User Story 3 - Clean Up Unused Dependencies and Standardize Logging in Use Cases (Priority: P2)

As a maintainer and DevOps engineer,
I want `LaunchPipelineUseCase` to eliminate unused injected dependencies and replace console output with structured SLF4J logging,
So that dependency graphs remain minimal and execution durations are properly structured in the service logging framework.

**Why this priority**: Injecting unused beans (`DiscoveryObjectService`) wastes context resources and creates misleading dependencies, while raw `System.out.println` escapes log level filtering and production log aggregation.

**Independent Test**: Can be independently verified by inspecting `LaunchPipelineUseCase` imports and fields to verify the absence of `DiscoveryObjectService`, and asserting that pipeline execution time is logged via `@Slf4j` with zero calls to `System.out.println`.

**Acceptance Scenarios**:

1. **Given** `LaunchPipelineUseCase`, **When** inspected or compiled, **Then** it injects only actively required dependencies (`PipelineService`) with zero unused fields or imports.
2. **Given** a request to launch a pipeline, **When** `LaunchPipelineUseCase` completes execution, **Then** execution duration is logged via structured `log.info` using SLF4J, with zero console prints.

---

### User Story 4 - Explicit Domain Exception on Missing Entity Update (Priority: P2)

As an API consumer updating biological object metadata (such as MeSH ID),
I want the use case to throw an explicit domain `ApiException(GeneralError.NOT_FOUND)` when attempting to update a non-existent biological object,
So that callers receive accurate error signaling rather than a false-positive success response.

**Why this priority**: Silently logging an error and returning `void` when a biological object is not found causes delivery adapters to respond with HTTP 200 OK, concealing missing entities and misleading callers.

**Independent Test**: Can be independently tested by attempting to update the MeSH ID of a non-existent biological object ID and verifying that `UpdateBiologicalObjectMeshIdUseCase` throws `ApiException` with `GeneralError.NOT_FOUND`.

**Acceptance Scenarios**:

1. **Given** a valid biological object ID and MeSH ID, **When** `UpdateBiologicalObjectMeshIdUseCase` executes, **Then** the object's MeSH ID is updated and persisted successfully.
2. **Given** a biological object ID that does not exist in the repository, **When** `UpdateBiologicalObjectMeshIdUseCase` executes, **Then** it throws `ApiException` with `GeneralError.NOT_FOUND` containing a descriptive error message.

---

### Edge Cases

- **Root biological object with null parent ID:** When `biologicalObjectParentId` is `null`, `FindBiologicalObjectFatherBrothersAndSonsUseCase` skips parent and sibling lookups and aggregates only the current object and any children without raising `NullPointerException`.
- **Target biological object has no children:** When a target object has no children in the pipeline, the sons query yields an empty list and only the object and its ancestors/siblings are aggregated.
- **Pipeline step update with null metrics:** The parameter encapsulation record handles nullable metrics cleanly without raising null pointer exceptions when converted to outbound client requests.
- **Missing biological object during MeSH update:** An explicit domain `ApiException` is thrown with `GeneralError.NOT_FOUND` rather than silently returning or throwing an uncaught runtime exception.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST guard hierarchy resolution in `FindBiologicalObjectFatherBrothersAndSonsUseCase` against null `biologicalObjectParentId` so root-level nodes skip father and sibling lookups without throwing `NullPointerException`.
- **FR-002**: System MUST return an empty collection (`List.of()`) in `FindBiologicalObjectFatherBrothersAndSonsUseCase` when the requested `biologicalObjectId` is not found in the given pipeline.
- **FR-003**: System MUST decompose hierarchy aggregation in `FindBiologicalObjectFatherBrothersAndSonsUseCase` into cohesive, single-responsibility private helper methods.
- **FR-004**: System MUST encapsulate multi-parameter pipeline step updates into a dedicated domain record in `com.biopatternsg.domain.models` (e.g. `PipelineStepUpdate`) containing pipeline ID, step, status, and optional metrics map.
- **FR-005**: System MUST update `ConfigAndControlRepository` and `ConfigAndControlAdapter` to accept the encapsulated `PipelineStepUpdate` domain model, eliminating signatures with 4 individual parameters.
- **FR-006**: System MUST encapsulate mined object creation parameters in `PipelineServiceImpl` (`newObjects`, `buildMinedObject`) into an internal Java record, ensuring all method signatures accept at most 2 parameters.
- **FR-007**: System MUST ensure that zero methods across `application.usecase.*`, `application.services.*`, `domain.port.*`, and `infrastructure.adapters.*` accept more than 3 individual parameters.
- **FR-008**: System MUST remove the unused `DiscoveryObjectService` field and unused imports from `LaunchPipelineUseCase`.
- **FR-009**: System MUST replace all `System.out.println` invocations in `LaunchPipelineUseCase` with structured logging using `@Slf4j` and `log.info`.
- **FR-010**: System MUST validate entity presence in `UpdateBiologicalObjectMeshIdUseCase` and throw `ApiException(GeneralError.NOT_FOUND)` when the specified biological object does not exist.
- **FR-011**: System MUST synchronize use case architecture documentation in `.agents/use_cases/` (`LaunchPipelineUseCase.md`, `FindBiologicalObjectFatherBrothersAndSonsUseCase.md`, `UpdateBiologicalObjectMeshIdUseCase.md`) reflecting updated signatures, flows, and exception contracts.

### Key Entities *(include if feature involves data)*

- **PipelineStepUpdate**: Domain record encapsulating pipeline step transition details (`String pipelineId`, `PipelineSteps step`, `Status status`, `Map<String, String> metrics`).
- **MinedObjectCreationContext**: Internal context record in `PipelineServiceImpl` encapsulating creation scope (`String pipelineId`, `String parentId`, `int level`).
- **BiologicalObject**: Pure business entity representing biological entities with metadata, identifiers, and MeSH mapping.
- **MinedObject**: Domain model representing a biological object discovered or mined within a specific pipeline execution hierarchy and level.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of hierarchy queries for root-level biological objects (`biologicalObjectParentId == null`) execute successfully without throwing `NullPointerException`.
- **SC-002**: Hierarchy queries for non-existent biological objects or pipelines return empty results within normal response latency (< 100ms) with zero uncaught runtime exceptions.
- **SC-003**: Zero methods across `application.usecase.*`, `application.services.*`, `domain.port.*`, and `infrastructure.adapters.*` accept more than 3 individual parameters.
- **SC-004**: Updating MeSH ID for a non-existent biological object throws a domain `ApiException` with `GeneralError.NOT_FOUND` in 100% of occurrences.
- **SC-005**: Zero unused fields, zero unused imports, and zero `System.out` statements remain in `LaunchPipelineUseCase`.
- **SC-006**: 100% of architectural governance tests (`ArchitectureLayerTest`) and compilation checks (`./mvnw clean compile`) pass with zero errors.

## Assumptions

- Returning `List.of()` when a mined object is not found in `FindBiologicalObjectFatherBrothersAndSonsUseCase` aligns with existing list-returning use cases (such as `FindBiologicalObjectsByPipelineIdAndLevelUseCase`) and avoids unnecessary 404 responses for empty exploration results.
- `UpdateBiologicalObjectMeshIdUseCase` throwing `ApiException(GeneralError.NOT_FOUND)` provides domain-level validation; delivery adapters can propagate or map this exception cleanly via `ApiExceptionMapper`.
- The new `PipelineStepUpdate` record belongs to `domain.models` as it represents a core business state transition across domain repository ports.
- Existing database schemas and MongoDB collections remain completely unchanged.
