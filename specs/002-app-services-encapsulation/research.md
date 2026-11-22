# Research: Application Services Encapsulation and Strategy Abstraction

## Executive Summary

This research establishes design decisions for refactoring application services and use cases in `search-biological-objects` to achieve full compliance with Principle II (*Use Cases & Application Services Encapsulation*) and Principle IV (*Java 21 Clean Code & Method Discipline*) of the project constitution.

The primary issues resolved are:
1. Leaking concrete strategy implementation classes into `FindBiologicalObjectUseCase` and peer application services.
2. Thread-safety hazards in `@ApplicationScoped` singleton beans due to mutable public fields and per-request chain re-wiring.
3. Assertions used in place of domain validation in strategy context classes.
4. Internal helper methods exposed as public, missing `@Override` annotations, and dead code overloads.

---

## Research Decisions

### Decision 1: Dedicated Public Service Interface for Biological Object Resolution

- **Context**: `FindBiologicalObjectUseCase` directly imports and injects `com.biopatternsg.application.services.impl.biological_object_strategy.BiologicalObjectContext`. Concurrently, `ExpertObjectServicesImpl`, `TranscriptionFactorsServiceImpl`, `UnmatchedSynonymServiceImpl`, and `DiscoveryObjectByPdb` inject concrete `BiologicalObjectSearch` from `impl.biological_object_strategy`.
- **Decision**: Define a public application service interface:
  ```java
  package com.biopatternsg.application.services;

  public interface BiologicalObjectSearchService {
      BiologicalObject findByTypeAndValue(String type, String value);
      BiologicalObject request(BiologicalObjectConfig biologicalObjectConfig);
  }
  ```
  Implement this interface in `BiologicalObjectSearchServiceImpl` (or refactor `BiologicalObjectSearch` to implement `BiologicalObjectSearchService`), injecting it into `FindBiologicalObjectUseCase`, `ExpertObjectServicesImpl`, `TranscriptionFactorsServiceImpl`, `UnmatchedSynonymServiceImpl`, and `DiscoveryObjectByPdb`.
- **Rationale**:
  - Completely decouples `application.usecase.*` from `application.services.impl.*`.
  - Unifies biological object resolution behind a single, high-cohesion public application service contract.
  - Allows internal strategies, caching, or lookup mechanics to change without impacting use cases or peer services.
- **Alternatives Considered**:
  - *Expose `BiologicalObjectContext` directly in `application.services.*`*: Rejected because context classes that select strategies are an implementation detail; callers only need a clean resolution method (`findByTypeAndValue`).
  - *Split into two separate interfaces*: Rejected because both methods address the single responsibility of locating and resolving biological objects.

---

### Decision 2: Stateless, Thread-Safe Chain Execution

- **Context**: `BiologicalObjectSearch` declares 6 strategy beans plus `public ChainResponsibility next` as public mutable fields. On every invocation of `request(BiologicalObjectConfig)`, it re-links the handlers (`setNext(...)`) and mutates `this.next`. Under concurrent HTTP requests in a Quarkus application, this causes race conditions and inconsistent execution paths.
- **Decision**:
  1. Make all strategy dependencies `private final` and inject them via constructor.
  2. Wire the chain links (`setNext(...)`) exactly once during constructor initialization (or `@PostConstruct`).
  3. Store the head of the chain (`biologicalObjectByUserUniprotId`) and delegate requests directly to `head.request(config)`.
  4. Remove `public ChainResponsibility next` and `setNext`/`getNext` mutations from `BiologicalObjectSearch`.
- **Rationale**:
  - Eliminates all public mutable fields in `@ApplicationScoped` singletons.
  - Guaranteed thread-safe under high concurrency with zero lock contention.
  - Reuses the existing chain-of-responsibility handlers without per-request allocations.
- **Alternatives Considered**:
  - *Iterating over an immutable `List<BuildBiologicalObjectStrategy>`*: Also viable, but constructor pre-wiring preserves the existing `ChainResponsibility` handler interface and logic while fully fixing the concurrency hazard.

---

### Decision 3: Explicit Domain Validation Replacing Assertions

- **Context**: `BiologicalObjectContext.load(String type)` and `DiscoveryObjectContext.load(String type)` execute `assert value != null;`. In standard production JVMs, `assert` statements are disabled by default (`-ea` flag required), meaning an unrecognized type silently passes the assert and throws an unhandled `NullPointerException` or switch evaluation failure.
- **Decision**: Replace `assert value != null;` with explicit domain validation:
  ```java
  if (value == null) {
      throw new ApiException(GeneralError.BAD_REQUEST, 400, "Unsupported biological object type: " + type);
  }
  ```
- **Rationale**:
  - Fails fast and deterministically across all environments (production and test).
  - Emits a clean `ApiException` mapped by `ApiExceptionMapper` to an HTTP 400 Bad Request response.

---

### Decision 4: Method Visibility and Dead Code Removal

- **Context**:
  - `TranscriptionFactorsServiceImpl.executeTFBind` is declared `public` despite not being part of `TranscriptionFactorsService`.
  - `PipelineServiceImpl.execute` does not have `@Override`, and `firstLevel` is package-private.
  - `DiscoveryObjectService.execute(PipelineConfig, String)` has a commented-out body in `DiscoveryObjectServiceImpl` and no callers anywhere in the codebase.
- **Decision**:
  1. Make `TranscriptionFactorsServiceImpl.executeTFBind(...)` `private`.
  2. Add `@Override` to `PipelineServiceImpl.execute(...)` and make `firstLevel(...)` `private`.
  3. Remove `void execute(PipelineConfig pipelineConfig, String context)` from `DiscoveryObjectService` and `DiscoveryObjectServiceImpl`.
- **Rationale**:
  - Complies with Constitution Principle IV (Java 21 Clean Code & Method Discipline).
  - Eliminates dead/commented-out code and prevents implementation details from leaking through public method contracts.

---

## Resolved Clarifications Matrix

| Topic | Decision | Status |
|-------|----------|--------|
| Service Interface for Lookup | Create `BiologicalObjectSearchService` under `application.services` | Resolved |
| Chain Concurrency & Mutability | Wire chain once at construction time; encapsulate fields as `private final` | Resolved |
| Assertion Replacement | Throw `ApiException(GeneralError.BAD_REQUEST, ...)` for null/invalid types | Resolved |
| `executeTFBind` Visibility | Change from `public` to `private` | Resolved |
| `PipelineServiceImpl` Annotations | Add `@Override` to `execute`; restrict `firstLevel` to `private` | Resolved |
| Dead Code in `DiscoveryObjectService` | Remove commented-out `execute(PipelineConfig, String)` overload | Resolved |
| ArchUnit Enforcement | Add rule: Use cases must not depend on `application.services.impl..` | Resolved |
