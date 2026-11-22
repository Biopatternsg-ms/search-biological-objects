# Feature Specification: Clean Code Lambdas, Constructor Injection, Static Mappers, and `@ToString` Compliance

**Feature Branch**: `feature/05-clean-code-lambdas-dependency-injection-and-static-mappers`

**Created**: 2026-11-22

**Status**: Draft

**Input**: User description: "/speckit-specify basado en /home/yacson/Proyectos/biopatternsg-ms/SDD/biological-objects/05-clean-code-lambdas-dependency-injection-and-static-mappers"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Enforce Constructor Injection Across Adapters (Priority: P1) 🎯 MVP

As a developer and architect maintaining dependency graphs,
I want all repository and external adapters to inject their dependencies and configuration values exclusively through immutable constructor parameters rather than mutable field injection,
So that components are easily unit-testable without reflection hacks, dependencies are guaranteed immutable, and mixed injection styles are eliminated.

**Why this priority**: Mixing `@Inject` field injection with constructor injection in `BiologicalObjectRepositoryAdapter` and using field injection for configuration properties in `PdbAdapter` breaks immutability and complicates unit testing.

**Independent Test**: Can be independently tested by instantiating `BiologicalObjectRepositoryAdapter` and `PdbAdapter` directly via their constructors in unit tests with zero reflection or DI container, and inspecting source files for zero `@Inject` field annotations.

**Acceptance Scenarios**:

1. **Given** `BiologicalObjectRepositoryAdapter`, **When** instantiated, **Then** all dependencies (`biologicalObjectRepositoryDB`, `userRepository`, and peer dependencies) are `private final` fields injected via its constructor with zero `@Inject` field annotations.
2. **Given** `PdbAdapter`, **When** instantiated, **Then** its configuration property (`targetOrganism`) is a `private final` field injected via its constructor.

---

### User Story 2 - Null-Safe Stateless Pure Static Mappers (Priority: P1)

As a backend developer persisting and retrieving biological and mined objects,
I want persistence mappers (`MinedObjectMapper`, `BiologicalObjectMapper`) to be non-instantiable `public final` utility classes with null-safe static mapping methods,
So that mapping `null` inputs, `null` lists, or records without database IDs never triggers `NullPointerException`.

**Why this priority**: Currently `MinedObjectMapper` is not `final`, has a public default constructor, contains an unresolved ID `TODO`, and crashes with `NullPointerException` on `null` inputs, while `BiologicalObjectMapper` lacks null checks on `id` and collection inputs.

**Independent Test**: Can be independently tested by passing `null` single objects, `null` lists, and objects with `null` IDs to `MinedObjectMapper` and `BiologicalObjectMapper` and asserting `null` or `List.of()` outputs without exceptions.

**Acceptance Scenarios**:

1. **Given** `MinedObjectMapper` and `BiologicalObjectMapper`, **When** inspected, **Then** both are declared `public final class` with explicit `private` constructors and zero instance state.
2. **Given** `null` single inputs or `null` collections passed to any mapping method in `MinedObjectMapper` or `BiologicalObjectMapper`, **When** executed, **Then** single-object methods return `null` and list methods return `List.of()` without throwing `NullPointerException`.
3. **Given** a `BiologicalObjectCollection` or `MinedObjectCollection` with a `null` identifier, **When** mapped to a domain model, **Then** the identifier is safely mapped as `null` without calling `.toString()` on `null`.

---

### User Story 3 - Single-Expression Lambdas, Clean Loops, and Modern Stream Collectors (Priority: P2)

As a maintainer reading and debugging application services and outbound adapters,
I want multi-line stream lambdas containing object builders, `if/switch` branching, and nested loops extracted into small single-responsibility private helpers or static mapper references, and duplicate/legacy stream patterns removed,
So that business workflows and adapter transformations are readable, modular, and adhere to Clean Code standards.

**Why this priority**: Multi-line `.map(...)` and `.forEach(...)` blocks in `ExpertObjectServicesImpl`, `TranscriptionFactorsServiceImpl`, `BiologicalObjectRepositoryAdapter`, `MinedObjectRepositoryAdapter`, `PdbAdapter`, and `UniprotAdapter` obscure intent and inflate cognitive complexity, while `MinedObjectRepositoryAdapter` has a duplicate setter call and `PubmedIntegrationAdapter` uses legacy `Collectors.toList()`.

**Independent Test**: Can be independently verified by inspecting stream pipelines across all 6 classes for single-expression lambdas or method references, verifying zero duplicate setter calls in `MinedObjectRepositoryAdapter`, and running unit tests for refactored services and adapters.

**Acceptance Scenarios**:

1. **Given** stream pipelines in `ExpertObjectServicesImpl`, `TranscriptionFactorsServiceImpl`, `BiologicalObjectRepositoryAdapter`, `MinedObjectRepositoryAdapter`, `PdbAdapter`, and `UniprotAdapter`, **When** executed, **Then** complex transformations and conditional logic reside in cohesive private helper methods or static mappers invoked via method references or clean loops, preserving exact runtime behavior.
2. **Given** `MinedObjectRepositoryAdapter.saveMinedCollection`, **When** persisting mined objects, **Then** `biologicalObjectId` is set once without duplicate consecutive calls.
3. **Given** `PubmedIntegrationAdapter`, **When** collecting mapped stream results, **Then** it uses `.toList()` instead of `.collect(Collectors.toList())`.

---

### User Story 4 - Explicit `toString()` Across Domain Models and DTOs (Priority: P3)

As a developer troubleshooting logs and test failures,
I want all domain models and infrastructure DTOs to define an explicit, readable `toString()` representation,
So that logged entities and assertion failures display meaningful field values instead of opaque memory hash codes.

**Why this priority**: 11 domain model classes and 1 infrastructure DTO (`JasparRegion`) currently fall back to `Object.toString()`, hiding state during debugging and violating Constitution Principle IV.

**Independent Test**: Can be independently verified by invoking `toString()` on instances of the 12 affected classes and asserting the output contains class/field names rather than default `@hashCode` strings.

**Acceptance Scenarios**:

1. **Given** any of the 11 listed classes in `domain.models.*` and `JasparRegion` in `infrastructure.dtos`, **When** `toString()` is invoked, **Then** it outputs a structured representation of the object's fields.

---

### Edge Cases

- **Null elements inside non-null lists passed to mappers:** Static list mappers (`toMinedObjects`, `toBiologicalObjects`, etc.) safely handle `null` list references (`List.of()`) and preserve deterministic mapping.
- **Uniprot adapter with null or empty GO/synonym entries:** Extracting nested `forEach` blocks in `UniprotAdapter` into private helpers preserves existing null guards on GO terms and synonyms.
- **Deprecated method in `PdbAdapter`:** Preserving `getComplexesDeprecated` if still referenced while refactoring its multi-line lambda keeps backward compatibility intact.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST convert `biologicalObjectRepositoryDB` and `userRepository` in `BiologicalObjectRepositoryAdapter` to `private final` fields injected via constructor (`@RequiredArgsConstructor`), removing `@Inject` field annotations.
- **FR-002**: System MUST inject `@ConfigProperty` `targetOrganism` via the constructor in `PdbAdapter` as a `private final` field.
- **FR-003**: System MUST declare `BiologicalObjectMapper` and `MinedObjectMapper` as `public final class` with explicit `private` no-arg constructors and zero mutable state.
- **FR-004**: System MUST add null-safety guards to all static methods in `BiologicalObjectMapper` and `MinedObjectMapper` (returning `null` for null single inputs, `List.of()` for null lists, and checking `id != null` before `.toString()`), and resolve the `//TODO chequearID` comment in `MinedObjectMapper`.
- **FR-005**: System MUST refactor all multi-line `.map(...)` and `.forEach(...)` lambdas with builders, branching, or nested loops in `ExpertObjectServicesImpl`, `TranscriptionFactorsServiceImpl`, `BiologicalObjectRepositoryAdapter`, `MinedObjectRepositoryAdapter`, `PdbAdapter`, and `UniprotAdapter` into single-expression method references (`this::helper` / `Mapper::method`) or clean private helper methods.
- **FR-006**: System MUST remove the duplicate `mongoObject.setBiologicalObjectId(...)` statement in `MinedObjectRepositoryAdapter.saveMinedCollection`.
- **FR-007**: System MUST replace `.collect(Collectors.toList())` with `.toList()` in `PubmedIntegrationAdapter`.
- **FR-008**: System MUST add explicit `@ToString` to `BiologicalObject`, `Complex`, `GeneOntology`, `MinedObject`, `HGNCResponse`, `UniprotResponse`, `BiologicalObjectConfig`, `DiscoveryObjectConfig`, `MinedObjectConfig`, `PipelineConfig`, `TranscriptionFactorConfig`, and `JasparRegion`.
- **FR-009**: System MUST pass `./mvnw clean compile` and existing plus new unit tests with zero errors.

### Key Entities *(include if feature involves data)*

- **Persistence Mappers (`BiologicalObjectMapper`, `MinedObjectMapper`)**: Stateless `public final` utility classes translating between MongoDB Panache collections and pure domain entities.
- **Domain Models & Infrastructure DTOs**: 11 domain model classes (`BiologicalObject`, `Complex`, `GeneOntology`, `MinedObject`, `HGNCResponse`, `UniprotResponse`, and 5 `pipeline_config` classes) plus `JasparRegion`.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Zero `@Inject` or `@ConfigProperty` field injections remain in `application.*` or `infrastructure.adapters.*` (`0` occurrences).
- **SC-002**: Zero multi-line object builders or `if/switch` branching blocks remain inside stream/optional lambdas across the 6 refactored service/adapter classes.
- **SC-003**: `BiologicalObjectMapper` and `MinedObjectMapper` handle 100% of `null` single, list, and ID inputs without throwing `NullPointerException`.
- **SC-004**: 100% of classes in `domain.models.*`, `infrastructure.adapters.dtos.*`, and `infrastructure.dtos.*` define an explicit `toString()`.
- **SC-005**: `./mvnw clean compile`, `ArchitectureLayerTest`, and all unit tests pass with zero errors.

## Assumptions

- `PdbAdapter#getComplexesDeprecated` is retained (with its lambda cleaned up) in case external callers or tests still reference it.
- Replacing `@UtilityClass` on `BiologicalObjectMapper` with `public final class` + explicit `private` constructor aligns both persistence mappers with the project's mapper standard in `infrastructure.adapters.mappers.*`.
- Quarkus config property constructor injection (`@ConfigProperty(name = "...")` on constructor parameter) works cleanly with an explicit constructor on `PdbAdapter`.
