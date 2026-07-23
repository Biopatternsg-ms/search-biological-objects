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

import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.port.in.FindBiologicalObject;
import com.biopatternsg.domain.port.in.FindBiologicalObjectFatherBrothersAndSons;
import com.biopatternsg.domain.port.in.FindBiologicalObjectsByPipelineAndLevel;
import com.biopatternsg.domain.port.in.GetExpertObjectsByPipelineAndLevel;
import com.biopatternsg.domain.port.in.UpdateBiologicalObjectMeshId;
import com.biopatternsg.domain.port.in.UpdateBiologicalObjectsSynonymsAfterKnowledgeBaseGeneration;
import com.biopatternsg.infrastructure.dtos.PipelineSynonymDTO;
import com.biopatternsg.infrastructure.adapters.dtos.BiologicalObjectDTO;
import com.biopatternsg.infrastructure.adapters.dtos.FatherBrothersAndSonsRequest;
import com.biopatternsg.infrastructure.adapters.dtos.BiologicalObjectRequest;
import com.biopatternsg.infrastructure.adapters.dtos.UpdateMeshIdRequest;
import com.biopatternsg.infrastructure.session.SessionUtil;
import io.quarkus.arc.Arc;
import io.quarkus.arc.ManagedContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.CompletableFuture;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.util.List;

@Slf4j
@ApplicationScoped
@Path("/biological-object")
@RequiredArgsConstructor
public class BiologicalObjectController {

    private final FindBiologicalObject findBiologicalObject;
    private final UpdateBiologicalObjectMeshId updateBiologicalObjectMeshId;
    private final FindBiologicalObjectsByPipelineAndLevel findBiologicalObjectsByPipelineAndLevel;
    private final FindBiologicalObjectFatherBrothersAndSons findBiologicalObjectFatherBrothersAndSons;
    private final GetExpertObjectsByPipelineAndLevel getExpertObjectsByPipelineAndLevel;
    private final UpdateBiologicalObjectsSynonymsAfterKnowledgeBaseGeneration updateBiologicalObjectsSynonymsAfterKnowledgeBaseGeneration;
    private final SessionUtil sessionUtil;

    @GET
    @Path("/search/{type}/{value}")
    @Operation(
            summary = "Find biological object by type and value",
            description = "Searches for a biological object based on the specified type and value."
    )
    @APIResponse(
            responseCode = "200",
            description = "Successfully retrieved biological object",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = BiologicalObject.class,
                            description = "The biological object matching the search criteria"
                    )
            )
    )
    public BiologicalObject find(@PathParam("type") String type, @PathParam("value") String value){
        return findBiologicalObject.execute(type, value);
    }

    @PATCH
    @Path("/update-mesh-id")
    @Operation(
            summary = "Update MeSH ID for biological object",
            description = "Updates the MeSH (Medical Subject Headings) identifier for a specific biological object."
    )
    @APIResponse(
            responseCode = "200",
            description = "Successfully updated MeSH ID",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.STRING,
                            description = "Success message"
                    )
            )
    )
    @APIResponse(
            responseCode = "500",
            description = "Internal server error occurred while updating MeSH ID",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.STRING,
                            description = "Error message"
                    )
            )
    )
    public Response updateMeshId(@RequestBody UpdateMeshIdRequest updateMeshIdRequest){
        try {
            updateBiologicalObjectMeshId.execute(updateMeshIdRequest.biologicalObjectId(), updateMeshIdRequest.meshId());
        } catch (Exception e) {
            log.error("Error updating mesh id", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok().build();
    }

    @POST
    @Path("/name-and-synonyms")
    @Operation(
            summary = "Get biological objects by pipeline and level",
            description = "Retrieves biological objects with names and synonyms based on pipeline ID and level."
    )
    @APIResponse(
            responseCode = "200",
            description = "Successfully retrieved biological objects",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = BiologicalObjectDTO.class,
                            type = SchemaType.ARRAY,
                            description = "List of biological objects with names and synonyms"
                    )
            )
    )
    public List<BiologicalObjectDTO> getResumedBiologicalObjects(@RequestBody BiologicalObjectRequest nameAndSynonymRequest){
        return findBiologicalObjectsByPipelineAndLevel.execute(nameAndSynonymRequest.pipelineId(), nameAndSynonymRequest.level())
                .stream().map(BiologicalObjectDTO::fromDomain).toList();
    }

    @POST
    @Path("/get-expert-objects")
    @Operation(
            summary = "Get expert biological objects by pipeline and level",
            description = "Retrieves biological objects with names and synonyms based on pipeline ID and level, filtering out those that have a transcription factor."
    )
    @APIResponse(
            responseCode = "200",
            description = "Successfully retrieved biological objects",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = BiologicalObjectDTO.class,
                            type = SchemaType.ARRAY,
                            description = "List of expert biological objects (without transcription factor)"
                    )
            )
    )
    public List<BiologicalObjectDTO> getExpertObjects(@RequestBody BiologicalObjectRequest biologicalObjectRequest){
        return getExpertObjectsByPipelineAndLevel.execute(biologicalObjectRequest.pipelineId(), biologicalObjectRequest.level())
                .stream().map(BiologicalObjectDTO::fromDomain).toList();
    }

    @POST
    @Path("/father-brothers-and-sons")
    @Operation(
            summary = "Get biological objects with father, brothers and sons",
            description = "Retrieves biological objects"
    )
    @APIResponse(
            responseCode = "200",
            description = "Successfully retrieved biological objects",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = BiologicalObjectDTO.class,
                            type = SchemaType.ARRAY,
                            description = "List of biological objects with father, brothers and sons"
                    )
            )
    )
    public List<BiologicalObjectDTO> getFatherBrothersAndSons(@RequestBody FatherBrothersAndSonsRequest fatherBrothersAndSonsRequest){
        return findBiologicalObjectFatherBrothersAndSons.execute(fatherBrothersAndSonsRequest.pipelineId(), fatherBrothersAndSonsRequest.biologicalObjectId())
                .stream().map(BiologicalObjectDTO::fromDomain).toList();
    }

    @POST
    @Path("/update-synonyms/{pipelineId}")
    @ActivateRequestContext
    @Operation(
            summary = "Update biological objects synonyms after knowledge base generation",
            description = "Retrieves synonyms from pubmed-integration and updates the biological objects asynchronously."
    )
    @APIResponse(
            responseCode = "202",
            description = "Synonyms update accepted and processing started",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            type = SchemaType.STRING,
                            description = "Success message"
                    )
            )
    )
    public Response updateSynonyms(@PathParam("pipelineId") String pipelineId) {
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
                if (contextToPropagate != null) {
                    sessionUtil.setContext(contextToPropagate);
                }
                updateBiologicalObjectsSynonymsAfterKnowledgeBaseGeneration.execute(pipelineId);
            } catch (Exception e) {
                log.error("Error updating synonyms asynchronously", e);
            } finally {
                if (newlyActivated) {
                    requestContext.terminate();
                }
            }
        });

        return Response.accepted()
                .entity("{\"message\": \"Biological objects synonyms update started\"}")
                .build();
    }
}
