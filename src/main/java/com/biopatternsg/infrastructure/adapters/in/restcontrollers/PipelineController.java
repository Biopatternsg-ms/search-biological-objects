/*
 * Copyright © 2026 biopatternsg (biopatternsg@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import com.biopatternsg.infrastructure.session.SessionUtil;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

@Slf4j
@ApplicationScoped
@Path("/biological-object")
@RequiredArgsConstructor
public class PipelineController {

    private final LaunchPipeline launchExperiment;
    private final ManagedExecutor executor;
    private final SessionUtil sessionUtil;

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

        // Get the context from the current request scope before going async
        // We create a copy to avoid issues if the original request ends and the map is cleared/recycled
        MultivaluedMap<String, String> currentContext = null;
        if (sessionUtil.getContext() != null) {
            currentContext = new MultivaluedHashMap<>(sessionUtil.getContext());
        }
        final MultivaluedMap<String, String> contextToPropagate = currentContext;

        CompletableFuture.runAsync(() -> {
            ManagedContext requestContext = Arc.container().requestContext();
            boolean newlyActivated = false;
            if (!requestContext.isActive()) {
                requestContext.activate();
                newlyActivated = true;
            }
            try {
                // Set the copied context into the new request scope
                if (contextToPropagate != null) {
                    sessionUtil.setContext(contextToPropagate);
                }
                launchExperiment.execute(launchExperimentRequest);
            } catch (Exception e) {
                log.error("Error launch pipeline", e);
            } finally {
                if (newlyActivated) {
                    requestContext.terminate();
                }
            }
        }); // Removing 'executor' so it uses the common pool and doesn't inherit the parent RequestContext

        return Response.accepted()
                .entity("{\"message\": \"successful launch pipeline " + launchExperimentRequest.getPipelineId() + "\"}")
                .build();
    }
}
