package com.biopatternsg.infrastructure.exceptions;

import com.biopatternsg.domain.exceptions.ApiException;
import com.biopatternsg.domain.exceptions.ApiResponseError;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Provider
@Slf4j
public class ApiExceptionMapper implements ExceptionMapper<ApiException> {

    @Override
    public Response toResponse(ApiException ex) {

        log.error("Handling ApiException: customCode={}, customMessage={}, generalError.code={}, generalError.message={}, httpStatus={}",
                ex.getCustomCode(), ex.getCustomMessage(), ex.getGeneralError().getCode(),
                ex.getGeneralError().getMessage(), ex.getGeneralError().getHttpStatusCode(), ex);

        String customCode = Optional.ofNullable(ex.getCustomCode())
                .map(Object::toString)
                .orElse(null);

        ApiResponseError errorResponse = ApiResponseError.builder()
                .code(Optional.ofNullable(customCode).orElse(String.valueOf(ex.getGeneralError().getCode())))
                .message(Optional.ofNullable(ex.getCustomMessage()).orElse(ex.getGeneralError().getMessage()))
                .build();

        return Response.status(ex.getGeneralError().getHttpStatusCode())
                .entity(errorResponse)
                .build();
    }
}