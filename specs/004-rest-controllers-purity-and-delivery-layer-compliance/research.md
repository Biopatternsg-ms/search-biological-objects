# Research & Design Decisions: REST Controllers Purity & Delivery Layer Compliance

**Feature**: `004-rest-controllers-purity-and-delivery-layer-compliance` | **Date**: 2026-11-22

## 1. DTO Package Consolidation Strategy

- **Decision**: Standardize all REST delivery Request/Response DTOs in `com.biopatternsg.infrastructure.adapters.dtos`, while keeping internal/external HTTP client DTOs (`JasparRegion`, `JasparRegionData`, `PipelineStepInternalRequest`, `ResumePipelineRequest`, `PaginatedResultDTO`, `PipelineSynonymDTO`, `ExpertObjects`, `ExpertObjectsData`) in `com.biopatternsg.infrastructure.dtos`. Shared outbound request records (`PromoterRegionRequest`, `JasparRequest`) remain in `infrastructure.dtos` where outbound adapters (`BlatAdapter`, `TFBindAdapter`, `JasparAdapter`) already consume them, or are cleanly documented as shared request records.
- **Rationale**:
  - `.agents/architecture.md` already defines `adapters/dtos/` as *"DTOs de entrada/salida REST"* and `dtos/` as *"DTOs de infraestructura"*.
  - Moving `PromoterRegionRequest` and `JasparRequest` out of `infrastructure.dtos` would force unnecessary churn across 6+ outbound external service classes (`QueryBlat`, `QueryTFBIND`, `BlatAdapter`, `TFBindAdapter`, `JasparAdapter`), whereas placing all *new* REST delivery DTOs in `infrastructure.adapters.dtos` cleanly enforces the delivery boundary.
- **Alternatives Considered**:
  - *Moving every DTO into `infrastructure.dtos`*: Rejected because it mixes REST delivery contracts with MicroProfile RestClient external payloads.

## 2. Async Context Propagation Extraction (`AsyncPipelineDispatcher`)

- **Decision**: Create `com.biopatternsg.infrastructure.adapters.in.AsyncPipelineDispatcher` (`@ApplicationScoped`) with a single method `dispatch(PipelineConfig config)` (or generic `dispatch(Runnable task)`), encapsulating `SessionUtil` context snapshot (`MultivaluedHashMap`), `CompletableFuture.runAsync`, `Arc.container().requestContext()` activation/termination, and error logging.
- **Rationale**:
  - Removes 30+ lines of ArC `ManagedContext` boilerplate, `try/catch/finally`, and multi-line lambdas from `PipelineController`.
  - `PipelineController#experiment` becomes a 3-line delivery method: map `LaunchPipelineRequest` -> `PipelineConfig`, call `asyncPipelineDispatcher.dispatch(pipelineConfig)`, and return `Response.accepted(new LaunchPipelineResponse(...)).build()`.
- **Alternatives Considered**:
  - *Using `@Asynchronous` or Quarkus `ManagedExecutor` directly*: Previously commented out in `PipelineController` line 100 (`// Removing 'executor' so it uses the common pool and doesn't inherit the parent RequestContext`) because inheriting an expiring parent `RequestContext` caused `ContextNotActiveException` in background threads. Preserving the explicit `Arc.container().requestContext()` lifecycle inside `AsyncPipelineDispatcher` maintains exact runtime behavior.

## 3. Backward-Compatible JSON Serialization for DTOs

- **Decision**: Define Java 21 `record` DTOs whose component names match the exact JSON property names of the replaced domain models:
  - `LaunchPipelineResponse(String message)` -> serializes to `{"message": "successful launch pipeline <id>"}` (exact match with previous string concatenation).
  - `BiologicalObjectDetailDTO` -> matches all 14 fields of `BiologicalObject` (`id`, `symbol`, `name`, `locusType`, `ensemblGeneId`, `hgncId`, `uniprotId`, `meshId`, `synonyms`, `geneOntology`, `geneFamilies`, `tissues`, `prevSymbol`, `transcriptionFactor`). Note: `geneOntology` and `transcriptionFactor` can use `GeneOntologyDTO` / `TranscriptionFactorDTO` or matching records with identical JSON field names.
  - `ComplexDTO(String complexPdbId, List<String> participants, float score)` -> exact match with `Complex`.
  - `TranscriptionFactorDTO(String name, float reliability, int number, String sign, String chain, String matrix, TranscriptionFactorSource source)` -> exact match with `TranscriptionFactor`.
  - `BlatSearchOptionDTO(double identity, String chromosome, String strand, String start, String end)` -> exact match with `BlatSearchOptionsResponse`.
- **Rationale**: Guarantees 100% wire-format backward compatibility with `config-and-control`, `pubmed-integration`, and frontend clients while eliminating direct exposure of domain classes in REST signatures.

## 4. `BiologicalObjectController#updateMeshId` Exception Propagation

- **Decision**: Remove the `try/catch (Exception e)` block from `BiologicalObjectController#updateMeshId` and return `Response.ok().build()`.
- **Rationale**: `UpdateBiologicalObjectMeshIdUseCase` throws `ApiException(GeneralError.NOT_FOUND, ...)` when the target object is missing. Removing the controller `try/catch` allows Quarkus's `ApiExceptionMapper` to map `ApiException` to HTTP `404 Not Found` centrally.
