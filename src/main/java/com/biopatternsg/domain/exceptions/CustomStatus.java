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
package com.biopatternsg.domain.exceptions;

import jakarta.ws.rs.core.Response;

public enum CustomStatus implements Response.StatusType {
    UNPROCESSABLE_ENTITY(422, "Unprocessable Entity");

    private final int statusCode;
    private final String reasonPhrase;
    private final Response.Status.Family family;

    CustomStatus(int statusCode, String reasonPhrase) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
        this.family = Response.Status.Family.familyOf(statusCode);
    }

    @Override
    public int getStatusCode() {
        return 0;
    }

    @Override
    public Response.Status.Family getFamily() {
        return null;
    }

    @Override
    public String getReasonPhrase() {
        return "";
    }
}
