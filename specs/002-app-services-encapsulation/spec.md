# Feature Specification: Application Services Encapsulation and Strategy Abstraction

**Feature Branch**: `002-app-services-encapsulation`

**Created**: 2026-11-22

**Status**: Draft

**Input**: User description: "/speckit-specify basado en /home/yacson/Proyectos/biopatternsg-ms/SDD/biological-objects/02-application-services-encapsulation-and-strategy-abstraction"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Abstract Biological Object Resolution Behind Public Application Services (Priority: P1) 🎯 MVP

As a software engineer and architect maintaining the biological objects service,
I want use cases and dependent application services to depend strictly on public application service interfaces rather than internal implementation classes,
So that internal resolution strategies and pipeline components can evolve independently without leaking internal implementation details or violating architectural boundaries.

**Why this priority**: Directly injecting concrete implementation classes from `application.services.impl.*` into use cases and peer services violates Principle II of the Constitution and tightly couples orchestrators to internal strategy implementation details.

**Independent Test**: Can be independently tested by inspecting imports in `FindBiologicalObjectUseCase`, `ExpertObjectServicesImpl`, and `TranscriptionFactorsServiceImpl` to verify zero dependencies on `application.services.impl.*`, and running unit tests validating biological object lookups through the public service interface.

**Acceptance Scenarios**:

1. **Given** a request to find biological objects, **When** `FindBiologicalObjectUseCase` executes, **Then** it coordinates resolution through a public application service interface without referencing concrete implementation classes in `application.services.impl.*`.
2. **Given** multi-step biological object resolution (e.g. in `ExpertObjectServices` or `TranscriptionFactorsService`), **When** biological objects are resolved by type, value, or pipeline configurations, **Then** callers interact solely through clean application service interfaces.
3. **Given** an invalid or unsupported biological object lookup type, **When** resolution is requested, **Then** the service validates the input and raises a standardized domain `ApiException` with bad request error details instead of failing assertions.

---

### User Story 2 - Eliminate Concurrency Hazards and Mutable Public State in Strategy Beans (Priority: P1)

As an operator and backend developer,
I want strategy context beans and search chain coordinators to be immutable and thread-safe,
So that concurrent queries do not mutate shared singleton state, corrupt chain links, or expose internal bean references through public fields.

**Why this priority**: Shared singleton beans (`@ApplicationScoped`) mutating instance state (`setNext`) per request introduce critical race conditions under concurrent load, leading to unpredictable handler sequences and memory leaks.

**Independent Test**: Can be independently tested by simulating concurrent requests to biological search strategies and verifying that handler execution is thread-safe without modifying shared singleton fields, while inspecting classes to ensure all fields are `private final`.

**Acceptance Scenarios**:

1. **Given** concurrent user queries resolving biological objects, **When** search chains execute across multiple concurrent threads, **Then** handlers operate statelessly without modifying shared links or state on singleton bean instances.
2. **Given** strategy context beans and search coordinators, **When** instantiated by the application context, **Then** all strategy dependencies are strictly encapsulated as `private final` fields using constructor injection.

---

### User Story 3 - Enforce Clean Method Discipline and Remove Dead Code in Application Services (Priority: P2)

As a maintainer,
I want application service implementations to expose only methods declared in their service interfaces and maintain clean method visibility,
So that internal helper methods are properly encapsulated as private, interface contracts are explicitly overridden, and unused dead code is eliminated.

**Why this priority**: Public helper methods that are not part of service interfaces leak implementation details, while commented-out dead code and missing `@Override` annotations degrade code maintainability and violate project coding standards.

**Independent Test**: Can be independently verified by inspecting method signatures and annotations across application service implementations and asserting that internal helpers are private and interface contracts explicitly annotated with `@Override`.

**Acceptance Scenarios**:

1. **Given** internal helper logic within application services (such as TF-Bind execution or pipeline levels), **When** declared in implementation classes, **Then** it is strictly private to the class.
2. **Given** interface implementations, **When** implementing service contracts, **Then** all contract methods explicitly declare `@Override` annotations.
3. **Given** abandoned or dead code overloads (such as commented-out pipeline execution overloads), **When** reviewing service contracts and implementations, **Then** unused code is completely removed.

---

### Edge Cases

- **Concurrent requests with differing search strategies**: When concurrent requests trigger biological object lookups, thread-safe chain evaluation ensures that neither request's chain structure or execution state interferes with concurrent executions.
- **Unsupported or null lookup types in strategy context**: When an unsupported identifier or null type is provided, the strategy coordinator throws an `ApiException` with `BAD_REQUEST` rather than an assertion failure or unhandled null reference.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Use cases in `com.biopatternsg.application.usecase..` MUST NOT depend on or import any classes from `com.biopatternsg.application.services.impl..`.
- **FR-002**: Multi-step biological object resolution strategies MUST be exposed through public service interfaces in `com.biopatternsg.application.services.*`.
- **FR-003**: Dependent use cases and services (`FindBiologicalObjectUseCase`, `ExpertObjectServicesImpl`, `TranscriptionFactorsServiceImpl`) MUST depend solely on public application service interfaces.
- **FR-004**: Strategy contexts and chain coordinators (`BiologicalObjectSearch`, `DiscoveryObjectContext`) MUST NOT expose mutable public fields, encapsulating all dependencies as `private final` fields.
- **FR-005**: Chain-of-responsibility execution in `BiologicalObjectSearch` MUST be thread-safe without modifying shared instance state on singleton beans during request processing.
- **FR-006**: Invalid lookup types in biological object context MUST raise a domain `ApiException` with `GeneralError.BAD_REQUEST`.
- **FR-007**: Internal helper methods in service implementations MUST be private, and all implemented interface methods MUST be annotated with `@Override`.
- **FR-008**: Dead and commented-out methods in application service interfaces and implementations MUST be removed.

### Key Entities *(include if feature involves data)*

- **Biological Object Search Request**: Query parameters (type, value, user context) specifying how biological entities are located.
- **Biological Search Chain**: Immutable sequence of resolution handlers evaluated in priority order (User UniProt → User HGNC → User Symbol → Global UniProt → Global HGNC → Global Symbol).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of use case classes depend solely on public application service interfaces, with 0 imports of `application.services.impl.*`.
- **SC-002**: 0 public mutable fields exist in strategy coordinators and application-scoped singleton beans.
- **SC-003**: 100% thread-safe biological object search execution under concurrent requests with zero race conditions on chain links.
- **SC-004**: Automated architectural checks (`ArchitectureLayerTest`) and unit tests pass with 0 errors.

## Assumptions

- The business priority order of biological object lookup strategies remains unchanged.
- External caller contracts on REST controllers remain backward-compatible.
- The service continues to run on Java 21 and Quarkus.
