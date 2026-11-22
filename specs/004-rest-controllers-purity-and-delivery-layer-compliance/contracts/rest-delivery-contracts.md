# REST Delivery Contracts

**Feature**: `004-rest-controllers-purity-and-delivery-layer-compliance` | **Date**: 2026-11-22

All existing HTTP paths, verbs, status codes, and JSON property names are preserved.

## 1. `BiologicalObjectController` (`/biological-object`)
- `GET /biological-object/search/{type}/{value}`: Returns `BiologicalObjectDetailDTO` (200 OK) mapped via `BiologicalObjectDtoMapper.toDetailDto(...)`.
- `PATCH /biological-object/update-mesh-id`: Accepts `UpdateMeshIdRequest`. No local `try/catch`; throws `ApiException(GeneralError.NOT_FOUND)` -> mapped to HTTP `404` by `ApiExceptionMapper`, or returns `200 OK` on success.
- `POST /biological-object/name-and-synonyms`, `/get-expert-objects`, `/father-brothers-and-sons`, `GET /search-by-synonym/{synonym}`: Return `List<BiologicalObjectDTO>`.

## 2. `PipelineController` (`/biological-object`)
- `POST /biological-object/launch-pipeline`: Accepts `LaunchPipelineRequest`, maps via `PipelineDtoMapper.toDomain(request)`, dispatches via `AsyncPipelineDispatcher.dispatch(config)`, returns `202 Accepted` with `LaunchPipelineResponse("successful launch pipeline " + request.pipelineId())`.

## 3. `TranscriptionFactorController` (`/transcription-factor`)
- `POST /transcription-factor/by-promoter-region`: Accepts `@Valid PromoterRegionRequest`, returns `List<TranscriptionFactorDTO>` (200 OK).
- `POST /transcription-factor/by-blat-coordinates`: Accepts `@Valid JasparRequest`, maps to `JasparQuery` via `TranscriptionFactorMapper.requestToModel(jasparRequest)`, returns `List<TranscriptionFactorDTO>` (200 OK).
- `POST /transcription-factor/by-config`: Accepts `@Valid TranscriptionFactorConfigRequest`, maps via `TranscriptionFactorMapper.toDomain(request)`, returns `List<TranscriptionFactorDTO>` (200 OK).

## 4. `ComplexController` (`/biological-object`)
- `GET /biological-object/complex/{uniprotId}`: Returns `List<ComplexDTO>` (200 OK) mapped via `ComplexDtoMapper.toDtoList(...)`.

## 5. `BlatController` (`/blat`)
- `POST /blat/search-options` (`@Deprecated`): Accepts `@Valid PromoterRegionRequest`, returns `List<BlatSearchOptionDTO>` (200 OK) mapped via `BlatDtoMapper.toDtoList(...)`.
