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

import jakarta.ws.rs.core.Response.Status;
import lombok.Getter;

@Getter
public enum GeneralError {
    BAD_REQUEST(400, "Bad Request", Status.BAD_REQUEST),
    UNAUTHORIZED(401, "Unauthorized", Status.UNAUTHORIZED),
    FORBIDDEN(403, "Forbidden", Status.FORBIDDEN),
    NOT_FOUND(404, "Not Found", Status.NOT_FOUND),
    UNPROCESSABLE_ENTITY(422, "Unprocessable Entity", CustomStatus.UNPROCESSABLE_ENTITY.toEnum()),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error", Status.INTERNAL_SERVER_ERROR);

    private final Integer code;
    private final String message;
    private final Status httpStatusCode;

    GeneralError(Integer code, String message, Status httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }
}
