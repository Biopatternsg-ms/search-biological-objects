package com.biopatternsg.infrastructure.adapters.in.restcontrollers;

import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.port.in.FindBiologicalObject;
import com.biopatternsg.domain.port.in.UpdateBiologicalObjectMeshId;
import com.biopatternsg.infrastructure.adapters.dtos.UpdateMeshIdRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@Slf4j
@ApplicationScoped
@Path("/biological-object")
@RequiredArgsConstructor
public class BiologicalObjectController {

    private final FindBiologicalObject findBiologicalObject;
    private final UpdateBiologicalObjectMeshId updateBiologicalObjectMeshId;

    @GET
    @Path("/search/{type}/{value}")
    @Operation(
        summary = "Find biological object by type and value",
        description = "Searches for a biological object based on the specified type and value."
    )
    @APIResponse(
        responseCode = "200",
        description = "Successfully retrieved biological object",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                implementation = BiologicalObject.class,
                description = "The biological object matching the search criteria"
            )
        )
    )
    public BiologicalObject find(@PathParam("type") String type, @PathParam("value") String value){
        return findBiologicalObject.execute(type, value);
    }

    @PATCH
    @Path("/update-mesh-id")
    @Operation(
        summary = "Update MeSH ID for biological object",
        description = "Updates the MeSH identifier for a specific biological object."
    )
    @APIResponse(
        responseCode = "200",
        description = "Successfully updated MeSH ID",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                type = SchemaType.STRING,
                description = "Success message"
            )
        )
    )
    @APIResponse(
        responseCode = "500",
        description = "Internal server error occurred while updating MeSH ID",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                type = SchemaType.STRING,
                description = "Error message"
            )
        )
    )
    public Response updateMeshId(@RequestBody UpdateMeshIdRequest updateMeshIdRequest){
        try {
            updateBiologicalObjectMeshId.execute(updateMeshIdRequest.biologicalObjectId(), updateMeshIdRequest.meshId());
        } catch (Exception e) {
            log.error("Error updating mesh id", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok().build();
    }
}
