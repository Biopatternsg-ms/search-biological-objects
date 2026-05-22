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

import com.biopatternsg.domain.models.Complex;
import com.biopatternsg.domain.port.in.FindComplex;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.util.List;

@ApplicationScoped
@Path("/biological-object")
@RequiredArgsConstructor
public class ComplexController {

    private final FindComplex findComplex;

    @GET
    @Path("/complex/{uniprotId}")
    @Operation(
        summary = "Find complexes by UniProt ID",
        description = "Retrieves a list of complexes associated with the specified UniProt identifier."
    )
    @APIResponse(
        responseCode = "200",
        description = "Successfully retrieved complexes",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                implementation = Complex.class,
                type = SchemaType.ARRAY,
                description = "List of complexes for the specified UniProt ID"
            )
        )
    )
    public List<Complex> find(@PathParam("uniprotId") String value){

        return findComplex.execute(value);
    }
}
