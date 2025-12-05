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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@ApplicationScoped
@Path("/biological-object")
@RequiredArgsConstructor
public class ExperimentController {

    private final LaunchPipeline launchExperiment;
    private final Executor executor;

    @POST
    @Path("/launch-pipeline")
    public Response experiment(@RequestBody PipelineConfig launchExperimentRequest) {

        CompletableFuture.runAsync(() -> {
            try {
                launchExperiment.execute(launchExperimentRequest);
            } catch (Exception e) {
                log.error("Error launch pipeline", e);
            }
        }, executor);

        return Response.accepted()
                .entity("{\"message\": \"successful launch pipeline " + launchExperimentRequest.getPipelineId() + "\"}")
                .build();
    }

}
