# Data Model: Delivery DTOs & Static Mappers

**Feature**: `004-rest-controllers-purity-and-delivery-layer-compliance` | **Date**: 2026-11-22

## 1. Delivery DTO Records (`com.biopatternsg.infrastructure.adapters.dtos`)

All new DTOs are Java 21 `record`s (immutable, with automatic `toString()`).

### 1.1. `LaunchPipelineRequest` & `BiologicalObjectConfigDTO` & `TranscriptionFactorConfigRequest`
- **`LaunchPipelineRequest`**: `String pipelineId`, `Integer levels`, `Integer maxComplexes`, `List<BiologicalObjectConfigDTO> expertObjects`, `TranscriptionFactorConfigRequest transcriptionFactorConfig`
- **`BiologicalObjectConfigDTO`**: `String uniprotId`, `String hgncId`, `String symbol`
- **`TranscriptionFactorConfigRequest`**: `String promoterRegion`, `int reliability`, `BigDecimal start`, `BigDecimal end`, `Genome genome`, `String track`, `Float identity`, `String chromosome`, `Strand strand`, `List<TranscriptionFactorSource> sources`

### 1.2. `LaunchPipelineResponse`
- Fields: `String message`
- Format: `"successful launch pipeline " + pipelineId`

### 1.3. `BiologicalObjectDetailDTO`
- Mirrors `BiologicalObject` wire format for `GET /biological-object/search/{type}/{value}`:
  - `String id`, `String symbol`, `String name`, `String locusType`, `String ensemblGeneId`, `String hgncId`, `String uniprotId`, `String meshId`, `Set<String> synonyms`, `GeneOntology geneOntology` (or DTO), `List<String> geneFamilies`, `List<String> tissues`, `List<String> prevSymbol`, `TranscriptionFactorDTO transcriptionFactor`

### 1.4. `TranscriptionFactorDTO`
- Fields: `String name`, `float reliability`, `int number`, `String sign`, `String chain`, `String matrix`, `TranscriptionFactorSource source`

### 1.5. `ComplexDTO`
- Fields: `String complexPdbId`, `List<String> participants`, `float score`

### 1.6. `BlatSearchOptionDTO`
- Fields: `double identity`, `String chromosome`, `String strand`, `String start`, `String end`

---

## 2. Static Pure Mappers (`com.biopatternsg.infrastructure.adapters.mappers`)

Each mapper is `public final` with a private no-arg constructor, zero CDI annotations, zero mutable state, and null-safe static methods:

| Mapper Class | Methods |
|---|---|
| `BiologicalObjectDtoMapper` | `toDetailDto(BiologicalObject)`, `toSummaryDto(BiologicalObject)`, `toSummaryDtoList(List<BiologicalObject>)` |
| `PipelineDtoMapper` | `toDomain(LaunchPipelineRequest)` |
| `TranscriptionFactorMapper` | `requestToModel(JasparRequest)`, `toDomain(TranscriptionFactorConfigRequest)`, `toDto(TranscriptionFactor)`, `toDtoList(List<TranscriptionFactor>)` |
| `ComplexDtoMapper` | `toDto(Complex)`, `toDtoList(List<Complex>)` |
| `BlatDtoMapper` | `toDto(BlatSearchOptionsResponse)`, `toDtoList(List<BlatSearchOptionsResponse>)` |
