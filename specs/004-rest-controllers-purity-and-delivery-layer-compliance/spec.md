# Feature Specification: REST Controllers Purity and Delivery Layer Compliance

**Feature Branch**: `feature/04-rest-controllers-purity-and-delivery-layer-compliance`

**Created**: 2026-11-22

**Status**: Draft

**Input**: User description: "/speckit-specify BASADO EN /home/yacson/Proyectos/biopatternsg-ms/SDD/biological-objects/04-rest-controllers-purity-and-delivery-layer-compliance"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Thin Delivery Controllers with Centralized Exception Mapping (Priority: P1) 🎯 MVP

As an API consumer and service maintainer,
I want REST controllers to delegate directly to inbound ports without manual `try/catch` blocks or unused injected dependencies,
So that domain exceptions (such as `NOT_FOUND` on missing entities) are mapped consistently to accurate HTTP error responses and controller dependency graphs remain minimal.

**Why this priority**: Manual `try/catch (Exception e)` in `BiologicalObjectController.updateMeshId` intercepts domain exceptions (`ApiException(GeneralError.NOT_FOUND)`) and converts them into generic HTTP 500 errors, breaking API error semantics established in `REQ-BO-ARCH-03`. Unused fields (`SessionUtil`, `ManagedExecutor`) add dead weight to CDI injection.

**Independent Test**: Can be independently tested by inspecting all controller classes for unused injected fields, calling the MeSH ID update endpoint for a non-existent biological object and verifying a 404 response is returned via centralized exception mapping, and verifying compilation.

**Acceptance Scenarios**:

1. **Given** a request to update the MeSH ID of a non-existent biological object, **When** `BiologicalObjectController.updateMeshId` delegates to the inbound port, **Then** the controller does not swallow the exception in a local `try/catch`, allowing centralized exception mapping to return the appropriate 404 Not Found response.
2. **Given** any REST controller in the delivery layer, **When** inspected for injected dependencies, **Then** zero unused injected fields exist (`SessionUtil` removed from `BiologicalObjectController`, `ManagedExecutor` removed from `PipelineController`).

---

### User Story 2 - Clean Asynchronous Pipeline Dispatch and Typed Responses (Priority: P1)

As a backend developer and operator,
I want `PipelineController` to delegate asynchronous execution and request-context propagation to a dedicated infrastructure dispatcher and return a typed response object instead of raw JSON string concatenation,
So that the controller remains a thin delivery adapter free of multi-line lifecycle management and fragile string formatting.

**Why this priority**: Embedding 30+ lines of request-context activation/termination, header copying, `CompletableFuture` orchestration, and manual JSON string building inside `PipelineController.experiment` violates delivery-layer purity and makes the controller error-prone and hard to test.

**Independent Test**: Can be independently tested by invoking the pipeline launch endpoint and verifying it dispatches asynchronously with preserved user session context, returns a structured JSON response matching the expected message contract, and leaves `PipelineController.experiment` free of container lifecycle calls or multi-line lambdas.

**Acceptance Scenarios**:

1. **Given** a valid pipeline launch request, **When** `PipelineController.experiment` handles the request, **Then** it delegates asynchronous execution and context propagation to a dedicated infrastructure dispatcher component without any container lifecycle management or multi-line async blocks in the controller.
2. **Given** a pipeline launch request succeeds, **When** the endpoint responds, **Then** it returns a typed response DTO (e.g., `LaunchPipelineResponse`) serialized cleanly to JSON with the exact expected message field, rather than concatenating raw JSON strings.

---

### User Story 3 - Isolate Domain Models Behind Delivery DTOs and Stateless Mappers (Priority: P2)

As a service architect and API maintainer,
I want all REST controller endpoints to accept and return dedicated delivery DTOs converted via stateless pure static mappers rather than exposing raw domain models or constructing domain queries inline,
So that internal domain model evolution is decoupled from external HTTP API contracts.

**Why this priority**: Exposing raw domain models (`BiologicalObject`, `PipelineConfig`, `Complex`, `TranscriptionFactor`, `TranscriptionFactorConfig`, `BlatSearchOptionsResponse`) directly in HTTP endpoints couples external API schemas to internal domain structures, while inline 7-argument constructor calls (`new JasparQuery(...)`) clutter controllers.

**Independent Test**: Can be independently tested by inspecting controller method signatures and static mapper classes to confirm domain models are converted through stateless mappers, and verifying that all HTTP request/response JSON payloads remain backward-compatible.

**Acceptance Scenarios**:

1. **Given** a JASPAR region query request in `TranscriptionFactorController`, **When** converting the request DTO to a domain query, **Then** the controller delegates to a stateless static mapper (`TranscriptionFactorMapper`) instead of calling `new JasparQuery(...)` inline.
2. **Given** REST endpoints across `BiologicalObjectController`, `PipelineController`, `TranscriptionFactorController`, `ComplexController`, and `BlatController`, **When** receiving requests or returning responses, **Then** they use dedicated DTOs mapped via stateless pure static mappers without breaking existing JSON property names or HTTP status codes.

---

### User Story 4 - Consolidate Delivery DTO Packages and Documentation (Priority: P3)

As a maintainer onboarding to the codebase,
I want delivery DTOs organized in a single consistent package with synchronized architecture documentation,
So that there is no ambiguity about where request/response DTOs belong and documentation reflects the actual codebase structure.

**Why this priority**: Splitting delivery DTOs across two separate packages (`infrastructure.adapters.dtos.*` and `infrastructure.dtos.*`) creates structural inconsistency and confusion for developers and automated governance checks.

**Independent Test**: Can be independently verified by checking that all delivery DTOs reside under a single consolidated package and that `.agents/architecture.md` and `.agents/architecture-rules.md` accurately document the package structure.

**Acceptance Scenarios**:

1. **Given** existing delivery DTOs across the infrastructure layer, **When** consolidated, **Then** all REST delivery DTOs reside under one consistent package structure without breaking internal service DTOs or JSON contracts.
2. **Given** `.agents/architecture.md` and `.agents/architecture-rules.md`, **When** reviewed after consolidation, **Then** all DTO package references and controller patterns match the implemented structure.

---

### Edge Cases

- **Asynchronous pipeline execution failure:** If the background pipeline execution fails after the HTTP response is returned, the dedicated async dispatcher cleanly terminates the managed request context in a `finally` block and logs the failure without leaking thread-local context.
- **Missing session header during async dispatch:** When the incoming request lacks user session headers, the async dispatcher propagates safely without throwing `NullPointerException`.
- **Null input to static delivery mappers:** Static mappers handle `null` inputs gracefully (returning `null` or empty collections for lists) without throwing `NullPointerException`.
- **Backward-compatible JSON serialization:** Consolidating DTOs and replacing raw domain models with DTOs preserves exact JSON field names and HTTP status codes expected by `config-and-control`, `pubmed-integration`, and UI consumers.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST remove all unused injected fields from REST controllers (`SessionUtil` from `BiologicalObjectController`, `ManagedExecutor` from `PipelineController`).
- **FR-002**: System MUST remove the local `try/catch` block from `BiologicalObjectController.updateMeshId` so exceptions propagate to centralized `ApiExceptionMapper` handling.
- **FR-003**: System MUST extract request-context propagation, header cloning, and asynchronous execution out of `PipelineController.experiment` into a dedicated infrastructure dispatcher component.
- **FR-004**: System MUST replace raw JSON string concatenation in `PipelineController.experiment` with a typed response DTO (`LaunchPipelineResponse`) preserving the `"message"` JSON property.
- **FR-005**: System MUST introduce stateless pure static mappers under `com.biopatternsg.infrastructure.adapters.mappers` (e.g., `BiologicalObjectDtoMapper`, `TranscriptionFactorMapper`, `PipelineDtoMapper`, `ComplexDtoMapper`, `BlatDtoMapper`) with private constructors and null-safe static methods.
- **FR-006**: System MUST replace inline `new JasparQuery(...)` construction in `TranscriptionFactorController` with a call to a static mapper method.
- **FR-007**: System MUST decouple REST controller endpoints in `BiologicalObjectController`, `PipelineController`, `TranscriptionFactorController`, `ComplexController`, and `BlatController` from raw domain models by using dedicated Request/Response DTOs mapped through static mappers.
- **FR-008**: System MUST preserve all existing HTTP paths, HTTP verbs, status codes, and JSON field names for backward compatibility with downstream microservices and UI clients.
- **FR-009**: System MUST consolidate delivery DTOs into a single consistent package and synchronize `.agents/architecture.md` and `.agents/architecture-rules.md` accordingly.
- **FR-010**: System MUST pass `./mvnw clean compile` and `./mvnw test -Dtest=ArchitectureLayerTest` with zero errors.

### Key Entities *(include if feature involves data)*

- **Delivery DTOs**: Immutable request/response data transfer objects (Java `record`s or DTO classes with explicit `toString()`) representing external HTTP contracts without domain behavior.
- **Static Delivery Mappers**: Stateless `public final` utility classes in `infrastructure.adapters.mappers` converting deterministically between Delivery DTOs and Domain Models.
- **Async Pipeline Dispatcher**: Dedicated infrastructure component responsible for propagating request context (`SessionUtil` / `ManagedContext`) across asynchronous pipeline execution boundaries.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Zero unused injected fields exist across all `*Controller` classes (`0` occurrences).
- **SC-002**: `BiologicalObjectController.updateMeshId` contains zero `try/catch` blocks, and missing-entity updates propagate `404 Not Found` via `ApiExceptionMapper`.
- **SC-003**: `PipelineController.experiment` contains zero `Arc.container()` calls, zero multi-line lambdas, and zero manual JSON string concatenations.
- **SC-004**: Zero REST controller endpoints directly instantiate multi-argument domain models inline or expose raw domain models in their HTTP signatures.
- **SC-005**: 100% of existing HTTP routes, status codes, and JSON property names remain backward-compatible.
- **SC-006**: 100% of architectural governance tests (`ArchitectureLayerTest`) and compilation checks (`./mvnw clean compile`) pass with zero errors.

## Assumptions

- Preserving existing JSON field names (e.g., `"message"` in `LaunchPipelineResponse`, field names of `BiologicalObject`, `PipelineConfig`, `Complex`, `TranscriptionFactor`, `BlatSearchOptionsResponse`) guarantees zero breaking changes for `config-and-control`, `pubmed-integration`, and frontend consumers.
- The `@Deprecated` endpoint in `BlatController` should remain functional (or use a Response DTO) until a separate deprecation removal schedule is approved, since removing existing HTTP paths is out of scope.
- Consolidating delivery DTOs under `com.biopatternsg.infrastructure.adapters.dtos` (or `com.biopatternsg.infrastructure.dtos`) should keep internal service DTOs distinct or cleanly organized so no naming collisions occur.
