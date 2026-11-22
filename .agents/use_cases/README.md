# Use Cases Catalog — `search-biological-objects`

This directory contains the functional and technical documentation for every Use Case implemented in the `search-biological-objects` microservice (`com.biopatternsg.application.usecase.*`).

> **Language Policy:** All documentation under `.agents/use_cases/` MUST be written in English.

## Index

| Use Case | Inbound Port | Primary Responsibility | Documentation |
|---|---|---|---|
| `FindBiologicalObjectUseCase` | `FindBiologicalObject` | Resolves a biological object by type and value using strategy chain resolution. | [`FindBiologicalObjectUseCase.md`](FindBiologicalObjectUseCase.md) |
| `FindBiologicalObjectsBySynonymUseCase` | `FindBiologicalObjectsBySynonym` | Searches biological objects globally by matching an entry in `synonyms`. | [`FindBiologicalObjectsBySynonymUseCase.md`](FindBiologicalObjectsBySynonymUseCase.md) |
| `FindBiologicalObjectsByPipelineIdAndLevelUseCase` | `FindBiologicalObjectsByPipelineAndLevel` | Retrieves biological objects associated with a pipeline and level that have valid names or synonyms. | [`FindBiologicalObjectsByPipelineIdAndLevelUseCase.md`](FindBiologicalObjectsByPipelineIdAndLevelUseCase.md) |
| `FindBiologicalObjectFatherBrothersAndSonsUseCase` | `FindBiologicalObjectFatherBrothersAndSons` | Retrieves a biological object along with its parent, siblings, and children within a pipeline. | [`FindBiologicalObjectFatherBrothersAndSonsUseCase.md`](FindBiologicalObjectFatherBrothersAndSonsUseCase.md) |
| `GetExpertObjectsUseCase` | `GetExpertObjectsByPipelineAndLevel` | Retrieves expert biological objects (without transcription factor) for a pipeline and level. | [`GetExpertObjectsUseCase.md`](GetExpertObjectsUseCase.md) |
| `UpdateBiologicalObjectMeshIdUseCase` | `UpdateBiologicalObjectMeshId` | Updates the MeSH identifier (`meshId`) of an existing biological object. | [`UpdateBiologicalObjectMeshIdUseCase.md`](UpdateBiologicalObjectMeshIdUseCase.md) |
| `FindComplexUseCase` | `FindComplex` | Retrieves macromolecular protein complexes from PDBe for a given UniProt ID. | [`FindComplexUseCase.md`](FindComplexUseCase.md) |
| `GetBlatSearchOptionsUseCase` | `FindBlatOption` | Generates BLAT genomic search options from reliability and promoter region inputs. | [`GetBlatSearchOptionsUseCase.md`](GetBlatSearchOptionsUseCase.md) |
| `GetTranscriptionFactorsUseCase` | `FindTranscriptionFactor` | Retrieves transcription factors from JASPAR, TFBIND, or pipeline configuration. | [`GetTranscriptionFactorsUseCase.md`](GetTranscriptionFactorsUseCase.md) |
| `LaunchPipelineUseCase` | `LaunchPipeline` | Executes the biological object discovery and enrichment phase for a pipeline configuration. | [`LaunchPipelineUseCase.md`](LaunchPipelineUseCase.md) |
