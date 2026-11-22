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

import com.biopatternsg.infrastructure.adapters.dtos.LaunchPipelineRequest;
import com.biopatternsg.infrastructure.adapters.dtos.LaunchPipelineResponse;
import com.biopatternsg.infrastructure.adapters.in.AsyncPipelineDispatcher;
import com.biopatternsg.infrastructure.adapters.mappers.PipelineDtoMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@ApplicationScoped
@Path("/biological-object")
@RequiredArgsConstructor
public class PipelineController {

    private final AsyncPipelineDispatcher asyncPipelineDispatcher;

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
                implementation = LaunchPipelineResponse.class,
                description = "Success message with pipeline ID"
            )
        )
    )
    public Response experiment(@Valid @RequestBody LaunchPipelineRequest launchExperimentRequest) {
        asyncPipelineDispatcher.dispatch(PipelineDtoMapper.toDomain(launchExperimentRequest));
        return Response.accepted(
                new LaunchPipelineResponse("successful launch pipeline " + launchExperimentRequest.pipelineId())
        ).build();
    }
}
