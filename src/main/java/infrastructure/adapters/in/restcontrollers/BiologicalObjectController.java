package infrastructure.adapters.in.restcontrollers;

import domain.models.BiologicalObject;
import domain.port.in.FindBiologicalObject;
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

    @GET
    @Path("/build/{label}")
    BiologicalObject find(@PathParam("label") String label) {
        return findBiologicalObject.execute(label);
    }

}
