# Research: Domain Layer Purity and Framework Decoupling

## Executive Summary

The `com.biopatternsg.domain` and `com.biopatternsg.application` packages currently contain 4 direct imports of `jakarta.ws.rs.*` (`Response`, `Response.Status`). This violates Principle I (*Hexagonal Layer Isolation*) of the project constitution. Furthermore, `CustomStatus` was implemented with stubbed zero/null values to represent HTTP 422, creating a runtime defect where `GeneralError.UNPROCESSABLE_ENTITY` yields a `null` HTTP status code.

This research documents the design decisions, trade-offs, and verification strategies to decouple the domain layer from web frameworks while maintaining clean, robust error mapping and resource lifecycle management.

---

## Research Decisions

### Decision 1: Return Type and Resource Lifecycle in `OntologiesRepository`

- **Context**: `OntologiesRepository` currently returns `jakarta.ws.rs.core.Response`. `OntologiesServiceImpl` consumes this response and calls `response.readEntity(String.class)`. In `OntologiesService` (the application service interface), both methods return `void`.
- **Decision**: Update `OntologiesRepository` methods to return `void` (or optionally `String` response payload if inspection is needed). Encapsulate all JAX-RS `Response` processing (calling `readEntity`, handling errors, and autoclosing the response using `try-with-resources`) inside `OntologiesAdapter` (`infrastructure.adapters.out.external_repositories.OntologiesAdapter`).
- **Rationale**:
  - Outbound repository ports in Hexagonal Architecture MUST return pure domain models or language primitives (`void`, `String`, etc.).
  - Managing network streams and HTTP response closure is an infrastructure concern. Placing it in `OntologiesAdapter` protects both `domain` and `application` layers from connection leaks and framework dependencies.
  - In `OntologiesServiceImpl`, the response was solely logged via `log.info(...)`. If `OntologiesAdapter` handles reading/logging or returns a pure `String` / `void`, the application layer remains completely free of JAX-RS dependencies. Returning `void` with logging encapsulated in `OntologiesAdapter` (or returning pure `String`) cleanly satisfies the requirements.
- **Alternatives Considered**:
  - *Keep `Response` but create a custom domain wrapper class*: Rejected because creating a mirror of HTTP `Response` in domain is an unnecessary anti-pattern for a void fire-and-forget/logging operation.
  - *Return `java.net.http.HttpResponse`*: Rejected because it still couples the domain to HTTP transport primitives.

---

### Decision 2: Domain Exception Status Mapping and Removal of `CustomStatus`

- **Context**: `GeneralError` currently imports `jakarta.ws.rs.core.Response.Status` and stores an instance as `httpStatusCode`. For HTTP 422, it calls `CustomStatus.UNPROCESSABLE_ENTITY.toEnum()`. In `CustomStatus`, `getStatusCode()` returns `0` and `getFamily()` returns `null`.
- **Decision**:
  1. Remove `import jakarta.ws.rs.core.Response.Status;` and the `httpStatusCode` field from `GeneralError`.
  2. Maintain only pure domain fields in `GeneralError`: `final Integer code` and `final String message`.
  3. Delete `CustomStatus.java` completely from `com.biopatternsg.domain.exceptions`.
  4. In `ApiExceptionMapper` (`infrastructure.exceptions.ApiExceptionMapper`), map HTTP status using the integer code directly: `Response.status(ex.getGeneralError().getCode()).entity(errorResponse).build()`.
- **Rationale**:
  - JAX-RS `Response.status(int status)` natively accepts standard integer status codes (including `422`, `400`, `401`, `403`, `404`, `500`).
  - By using `Response.status(ex.getGeneralError().getCode())`, we bypass `Response.Status` enum limitations without requiring custom `StatusType` implementations or stub methods.
  - Eliminates the runtime `null` status bug and completely decouples `GeneralError` from JAX-RS.
- **Alternatives Considered**:
  - *Move `CustomStatus` to infrastructure*: Unnecessary because `Response.status(int)` already accepts `422` directly without custom implementations.
  - *Use a switch-case inside `ApiExceptionMapper`*: Viable, but `Response.status(int)` already handles all standard HTTP status codes uniformly and concisely.

---

### Decision 3: ArchUnit Rules for Hexagonal Isolation Governance

- **Context**: `ArchitectureLayerTest` currently verifies layer-to-layer access between `domain`, `application`, and `infrastructure`, but lacks explicit rules checking for forbidden third-party framework imports in `domain` and `application`.
- **Decision**: Add explicit ArchUnit tests:
  1. `domainLayerIsolationFromFrameworksTest`: Ensure no class in `com.biopatternsg.domain..` imports or depends on `jakarta.ws.rs..`, `io.quarkus..`, or `org.bson..`.
  2. `applicationLayerIsolationFromFrameworksTest`: Ensure no class in `com.biopatternsg.application..` imports or depends on `jakarta.ws.rs..`.
- **Rationale**:
  - Principle I of the Constitution states Hexagonal Isolation is NON-NEGOTIABLE.
  - Automated tests guarantee regressions will immediately fail CI/CD.
- **Alternatives Considered**:
  - *Rely only on manual code review or checkstyle*: Rejected because ArchUnit provides immediate, deterministic compile-time and test-time validation.

---

## Resolved Clarifications Matrix

| Topic | Decision | Status |
|-------|----------|--------|
| `OntologiesRepository` return type | Pure Java type (`void` or `String`) | Resolved |
| Resource lifecycle management | Encapsulated with try-with-resources in `OntologiesAdapter` | Resolved |
| `GeneralError` HTTP coupling | Remove `Status` field; retain `Integer code` & `String message` | Resolved |
| `CustomStatus` class | Delete file entirely | Resolved |
| HTTP 422 mapping | `Response.status(ex.getGeneralError().getCode())` in `ApiExceptionMapper` | Resolved |
| Architecture test enforcement | Add explicit framework isolation rules to `ArchitectureLayerTest` | Resolved |
