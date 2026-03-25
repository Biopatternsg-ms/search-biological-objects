package com.biopatternsg.infrastructure.adapters.in.restcontrollers;

import com.biopatternsg.domain.models.pipeline_config.PipelineConfig;
import com.biopatternsg.domain.port.in.LaunchPipeline;
import io.quarkus.arc.Arc;
import io.quarkus.arc.ManagedContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.util.concurrent.CompletableFuture;

@Slf4j
@ApplicationScoped
@Path("/biological-object")
@RequiredArgsConstructor
public class PipelineController {

    private final LaunchPipeline launchExperiment;
    private final ManagedExecutor executor;

    @POST
    @Path("/launch-pipeline")
    @ActivateRequestContext
    @Operation(
        summary = "Launch biological pipeline",
        description = "Launches a biological data processing pipeline asynchronously based on the provided configuration."
    )
    @APIResponse(
        responseCode = "202",
        description = "Pipeline launch accepted and processing started",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                type = SchemaType.STRING,
                description = "Success message with pipeline ID"
            )
        )
    )
    public Response experiment(@RequestBody PipelineConfig launchExperimentRequest) {

        CompletableFuture.runAsync(() -> {
            ManagedContext requestContext = Arc.container().requestContext();
            if (!requestContext.isActive()) {
                requestContext.activate();
            }
            try {
                launchExperiment.execute(launchExperimentRequest);
            } catch (Exception e) {
                log.error("Error launch pipeline", e);
            } finally {
                requestContext.terminate();
            }
        }, executor);

        return Response.accepted()
                .entity("{\"message\": \"successful launch pipeline " + launchExperimentRequest.getPipelineId() + "\"}")
                .build();
    }

}
