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

import com.biopatternsg.domain.models.external_entities.BlatSearchOptionsResponse;
import com.biopatternsg.domain.port.in.FindBlatOption;
import com.biopatternsg.infrastructure.dtos.PromoterRegionRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@ApplicationScoped
@Path("/blat")
@RequiredArgsConstructor
public class BlatController {

    private final FindBlatOption findBlatOptions;

    @POST
    @Path("/search-options")
    @Operation(
        summary = "Search for BLAT options",
        description = "Searches for BLAT options based on the provided promoter region sequence."
    )
    @APIResponse(
        responseCode = "200",
        description = "Successfully retrieved BLAT search options",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                implementation = BlatSearchOptionsResponse.class,
                type = SchemaType.ARRAY
            )
        )
    )
    @Deprecated
    public Response getByPromoterJasparRegion(
            @Valid PromoterRegionRequest promoterRegion) {
        return Response
                .ok(findBlatOptions.execute(promoterRegion.reliability(), promoterRegion.promoterRegion()))
                .build();
    }

}
