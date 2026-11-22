# Data Model & Entity Specifications: Domain Layer Purity

## Domain Enums & Models

### 1. `GeneralError` (`com.biopatternsg.domain.exceptions.GeneralError`)

Domain enum representing canonical business and protocol error codes.

- **Package**: `com.biopatternsg.domain.exceptions`
- **Purity Constraints**:
  - Zero imports from `jakarta.ws.rs.*` or any external web framework.
  - Pure Java language constructs.

**Fields**:
| Field Name | Type | Description |
|------------|------|-------------|
| `code` | `Integer` | HTTP-compatible standard numeric error code (e.g., 400, 422, 500) |
| `message` | `String` | Canonical human-readable description of the error |

**Constants**:
| Constant | Code | Message |
|----------|------|---------|
| `BAD_REQUEST` | `400` | `"Bad Request"` |
| `UNAUTHORIZED` | `401` | `"Unauthorized"` |
| `FORBIDDEN` | `403` | `"Forbidden"` |
| `NOT_FOUND` | `404` | `"Not Found"` |
| `UNPROCESSABLE_ENTITY` | `422` | `"Unprocessable Entity"` |
| `INTERNAL_SERVER_ERROR` | `500` | `"Internal Server Error"` |

---

### 2. `ApiException` (`com.biopatternsg.domain.exceptions.ApiException`)

Standard runtime domain exception raised across use cases and domain models.

- **Package**: `com.biopatternsg.domain.exceptions`
- **Fields**:
  - `generalError`: `GeneralError` (mandatory)
  - `customCode`: `Object` (optional)
  - `customMessage`: `String` (optional)

---

### 3. `CustomStatus` (`com.biopatternsg.domain.exceptions.CustomStatus`)

- **Status**: **REMOVED**
- **Action**: Completely delete `CustomStatus.java`. No longer required since `GeneralError.UNPROCESSABLE_ENTITY` maps directly via standard integer `422`.

---

## Port Interfaces

### 4. `OntologiesRepository` (`com.biopatternsg.domain.port.out.external_repositories.OntologiesRepository`)

Outbound port interface declaring ontology tree resolution operations.

- **Package**: `com.biopatternsg.domain.port.out.external_repositories`
- **Purity Constraints**:
  - Zero imports from `jakarta.ws.rs.*`.
  - Must return pure Java types (`void` or `String`).

**Methods**:
| Method Signature | Input | Output | Description |
|------------------|-------|--------|-------------|
| `void buildGeneOntologyTree(GeneOntology geneOntology)` | `GeneOntology` | `void` | Requests construction of Gene Ontology tree for given model |
| `void buildMeshOntologyTree(BiologicalObject biologicalObject)` | `BiologicalObject` | `void` | Requests construction of MeSH Ontology tree for given model |

---

## Infrastructure Adapters & Mappers

### 5. `OntologiesAdapter` (`infrastructure.adapters.out.external_repositories.OntologiesAdapter`)

- **Package**: `com.biopatternsg.infrastructure.adapters.out.external_repositories`
- **Implements**: `OntologiesRepository`
- **Responsibilities**:
  - Injects `QueryOntologies`.
  - Encapsulates JAX-RS `Response` handling.
  - Guarantees response closing using `try (Response response = ...)`.
  - Reads entity string safely before stream closure.

---

### 6. `ApiExceptionMapper` (`infrastructure.exceptions.ApiExceptionMapper`)

- **Package**: `com.biopatternsg.infrastructure.exceptions`
- **Implements**: `ExceptionMapper<ApiException>`
- **Responsibilities**:
  - Translates `ApiException` into standard HTTP JAX-RS `Response`.
  - Resolves HTTP status using `Response.status(ex.getGeneralError().getCode())`.
  - Builds `ApiResponseError` payload with resolved code and message.
