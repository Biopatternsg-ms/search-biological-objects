package com.biopatternsg.infrastructure.adapters.in.restcontrollers;

import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.port.in.FindBiologicalObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@Path("/biological-object")
@RequiredArgsConstructor
public class BiologicalObjectController {

    private final FindBiologicalObject findBiologicalObject;

    /*
    @GET
    @Path("/build/{label}")
    BiologicalObject find(@PathParam("label") String label) {
        return findBiologicalObject.execute(label);
    }*/

    @GET
    @Path("/search/{type}/{value}")
    public BiologicalObject find(@PathParam("type") String type, @PathParam("value") String value){
        return findBiologicalObject.execute(type, value);
    }

}
