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
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

@Slf4j
@ApplicationScoped
@Path("/biological-object")
@RequiredArgsConstructor
public class BiologicalObjectController {

    private final FindBiologicalObject findBiologicalObject;
    private final UpdateBiologicalObjectMeshId updateBiologicalObjectMeshId;

    @GET
    @Path("/search/{type}/{value}")
    public BiologicalObject find(@PathParam("type") String type, @PathParam("value") String value){
        return findBiologicalObject.execute(type, value);
    }

    @PATCH
    @Path("/update-mesh-id")
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
