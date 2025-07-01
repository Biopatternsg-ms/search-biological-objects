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
