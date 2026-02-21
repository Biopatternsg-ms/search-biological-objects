package com.biopatternsg.infrastructure.adapters.dtos;

public record UpdateMeshIdRequest(
        String biologicalObjectId,
        String meshId
) {
}
