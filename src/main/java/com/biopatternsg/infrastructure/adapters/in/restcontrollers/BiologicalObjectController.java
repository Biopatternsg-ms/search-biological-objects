package com.biopatternsg.infrastructure.adapters.in.restcontrollers;

import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.port.in.FindBiologicalObject;
import com.biopatternsg.domain.port.in.UpdateBiologicalObjectMeshId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

    @PUT
    @Path("/update-mesh-id/{biologicalObjectId}/{meshId}")
    public Response updateMeshId(
            @PathParam("biologicalObjectId") String biologicalObjectId,
            @PathParam("meshId") String meshId
    ){
        try {
            updateBiologicalObjectMeshId.execute(biologicalObjectId, meshId);
        } catch (Exception e) {
            log.error("Error updating mesh id", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok().build();
    }
}
