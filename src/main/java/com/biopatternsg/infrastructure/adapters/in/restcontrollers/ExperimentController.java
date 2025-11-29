package com.biopatternsg.infrastructure.adapters.in.restcontrollers;

import com.biopatternsg.domain.models.pipeline_config.PipelineConfig;
import com.biopatternsg.domain.port.in.LaunchPipeline;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

@Slf4j
@ApplicationScoped
@Path("/biological-object")
@RequiredArgsConstructor
public class ExperimentController {

    @Inject
    Config config;

    private final LaunchPipeline launchExperiment;

    @POST
    @Path("/launch-experiment")
    public void experiment(@RequestBody PipelineConfig launchExperimentRequest){

        String mongoUser = config.getOptionalValue("quarkus.mongodb.credentials.username", String.class).orElse("N/A");
        String mongoPassword = config.getOptionalValue("quarkus.mongodb.credentials.password", String.class).orElse("N/A");
        String mongoAuth = config.getOptionalValue("quarkus.mongodb.credentials.authentication-database", String.class).orElse("N/A");

        log.error("MONGO USER: {} - PASSWORD: {} - AUTH: {}", mongoUser, mongoPassword, mongoAuth);

        launchExperiment.execute(launchExperimentRequest);
    }

}
