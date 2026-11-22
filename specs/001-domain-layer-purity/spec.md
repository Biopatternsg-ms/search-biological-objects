# Feature Specification: Domain Layer Purity and Framework Decoupling

**Feature Branch**: `001-domain-layer-purity`

**Created**: 2026-11-22

**Status**: Draft

**Input**: User description: "/speckit-specify basado en /home/yacson/Proyectos/biopatternsg-ms/SDD/biological-objects/01-domain-layer-purity-and-framework-decoupling"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Maintain Hexagonal Layer Purity in Domain and Application Layers (Priority: P1)

As a software engineer and architect maintaining the biological objects service,
I want the core domain business logic and application orchestration services to be completely isolated from external delivery and HTTP frameworks,
So that the core business domain can evolve independently of external transport protocols, framework lifecycles, and library upgrades.

**Why this priority**: Violating domain layer isolation introduces tight coupling to web frameworks, leaks transport lifecycle concerns (such as response stream closing and entity parsing) into business services, and directly violates project architectural principles.

**Independent Test**: Can be independently verified by inspecting domain and application packages to ensure zero web/persistence framework imports and verifying that ontology tree requests return pure domain data structures.

**Acceptance Scenarios**:

1. **Given** an outbound repository port for ontologies, **When** domain interfaces or application services interact with it, **Then** they receive pure domain representations or language primitives without exposing transport or HTTP response types.
2. **Given** an outbound adapter communicating with external ontology services, **When** HTTP responses are received, **Then** transport entity extraction, resource cleanup, and mapping to domain models occur entirely inside the infrastructure adapter boundary.

---

### User Story 2 - Decouple Domain Exceptions from Transport HTTP Status Objects (Priority: P1)

As an API consumer and backend developer,
I want domain errors to define business error identities and codes without directly embedding web framework status objects,
So that errors remain transport-neutral and are reliably translated into standard HTTP status codes (such as 422 Unprocessable Entity) without runtime null pointer errors or stub failures.

**Why this priority**: Domain exceptions holding framework-specific status objects break domain purity and caused defective status stubs, leading to `null` HTTP status codes at runtime during unprocessable entity scenarios.

**Independent Test**: Can be independently tested by raising unprocessable entity domain errors and asserting that the boundary exception mapper emits an HTTP 422 response with standard error details, without runtime failures.

**Acceptance Scenarios**:

1. **Given** a business validation failure resulting in an unprocessable entity condition, **When** a domain exception is raised, **Then** it carries a pure error code and message without holding framework response types.
2. **Given** a domain exception propagated to the infrastructure boundary, **When** the centralized error translation executes, **Then** it accurately maps the domain error code to the proper HTTP client error status (including 422 Unprocessable Entity) with standard response details.

---

### Edge Cases

- **External service returns empty or malformed payload**: When an external ontology integration returns empty, non-standard, or error payloads, the infrastructure adapter safely processes and closes any underlying HTTP connection and transforms the response into a domain result or raises a domain `ApiException`.
- **Unmapped domain error code at boundary**: When an unrecognized or unmapped domain error code is processed at the boundary, the centralized exception handler falls back to a standardized internal server error response without exposing internal implementation details or throwing null reference errors.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Domain port interfaces MUST declare return types using pure domain models or language primitives, without exposing web framework transport objects.
- **FR-002**: Application services MUST NOT import or process HTTP response transport entities, delegating all entity parsing and resource lifecycle management to outbound infrastructure adapters.
- **FR-003**: Outbound adapters communicating with external ontology services MUST handle HTTP connection lifecycles, entity deserialization, and safe resource release within the infrastructure boundary.
- **FR-004**: Domain exception definitions MUST NOT depend on or import web framework status structures, defining only framework-agnostic error codes and human-readable messages.
- **FR-005**: Infrastructure exception mapping MUST translate domain business error codes into their corresponding HTTP status codes, guaranteeing that unprocessable entity conditions accurately resolve to HTTP 422.
- **FR-006**: Domain and application layers MUST strictly adhere to architectural layer constraints with zero framework dependency leakage into the core business boundaries.

### Key Entities *(include if feature involves data)*

- **Ontology Tree Data**: Pure domain representation of biological ontology hierarchical trees (such as Gene Ontology and MeSH Ontology structures) passed across domain boundaries.
- **General Error**: Standard domain error definition containing a unique numeric error code and user-facing descriptive message representing business error conditions.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of domain and application source files are completely decoupled from web and persistence framework packages.
- **SC-002**: 100% of ontology integration interactions properly release underlying network resources within infrastructure adapters without resource leaks.
- **SC-003**: 100% of business error conditions (including unprocessable entities) produce deterministic HTTP status codes without null status resolutions or runtime failures.
- **SC-004**: Automated architectural verification rules pass with zero layer boundary or naming convention violations.

## Assumptions

- The external HTTP contract of the ontology service remains unchanged.
- The existing domain exception hierarchy (`ApiException`) remains the standard mechanism for business error propagation.
- Existing client-facing HTTP response schemas for errors and ontology tree endpoints remain backward-compatible.
