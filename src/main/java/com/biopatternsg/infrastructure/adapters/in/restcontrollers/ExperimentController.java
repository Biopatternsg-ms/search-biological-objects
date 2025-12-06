package com.biopatternsg.infrastructure.adapters.in.restcontrollers;

import com.biopatternsg.domain.models.pipeline_config.PipelineConfig;
import com.biopatternsg.domain.port.in.LaunchPipeline;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

@Slf4j
@ApplicationScoped
@Path("/biological-object")
@RequiredArgsConstructor
public class ExperimentController {

    private final LaunchPipeline launchExperiment;

    @POST
    @Path("/launch-pipeline")
    public Response experiment(@RequestBody PipelineConfig launchExperimentRequest) {

        launchExperiment.execute(launchExperimentRequest);
        return Response.accepted()
                .entity("{\"message\": \"successful launch pipeline " + launchExperimentRequest.getPipelineId() + "\"}")
                .build();
    }

}
