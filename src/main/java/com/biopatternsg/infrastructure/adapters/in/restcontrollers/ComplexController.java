package com.biopatternsg.infrastructure.adapters.in.restcontrollers;

import com.biopatternsg.application.services.BuildPdbService;
import com.biopatternsg.domain.models.Complex;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import lombok.RequiredArgsConstructor;
import java.util.List;

@ApplicationScoped
@Path("/biological-object")
@RequiredArgsConstructor
public class ComplexController {

    private final BuildPdbService buildPdbeService;

    @GET
    @Path("/complex/{uniprotId}")
    public List<Complex> find(@PathParam("uniprotId") String value){

        return buildPdbeService.complexes(value);
    }
}
