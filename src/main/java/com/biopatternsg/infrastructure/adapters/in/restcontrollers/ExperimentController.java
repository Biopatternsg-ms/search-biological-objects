package com.biopatternsg.infrastructure.adapters.in.restcontrollers;

import com.biopatternsg.domain.models.pipeline_config.PipelineConfig;
import com.biopatternsg.domain.port.in.LaunchPipeline;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

@Slf4j
@ApplicationScoped
@Path("/biological-object")
@RequiredArgsConstructor
public class ExperimentController {

    @Inject
    @ConfigProperty(name = "quarkus.mongodb.credentials.username")
    private final String mongoUser;
    @Inject
    @ConfigProperty(name = "quarkus.mongodb.credentials.password")
    private final String mongoPassword;

    private final LaunchPipeline launchExperiment;

    @POST
    @Path("/launch-experiment")
    public void experiment(@RequestBody PipelineConfig launchExperimentRequest){

        log.error("MONGO USER: {} - PASSWORD: {}", mongoUser, mongoPassword);

        launchExperiment.execute(launchExperimentRequest);
    }

}
