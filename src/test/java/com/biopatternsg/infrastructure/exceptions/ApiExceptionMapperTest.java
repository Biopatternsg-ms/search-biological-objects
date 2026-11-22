package com.biopatternsg.infrastructure.exceptions;

import com.biopatternsg.domain.exceptions.ApiException;
import com.biopatternsg.domain.exceptions.ApiResponseError;
import com.biopatternsg.domain.exceptions.GeneralError;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ApiExceptionMapperTest {

    private ApiExceptionMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ApiExceptionMapper();
    }

    @Test
    @DisplayName("Should map UNPROCESSABLE_ENTITY to HTTP 422 with correct error payload")
    void testMapUnprocessableEntity() {
        ApiException ex = new ApiException(GeneralError.UNPROCESSABLE_ENTITY);

        Response response = mapper.toResponse(ex);

        assertNotNull(response);
        assertEquals(422, response.getStatus());

        ApiResponseError entity = (ApiResponseError) response.getEntity();
        assertNotNull(entity);
        assertEquals("422", entity.getCode());
        assertEquals("Unprocessable Entity", entity.getMessage());
    }

    @Test
    @DisplayName("Should preserve custom code and message when provided")
    void testCustomCodeAndMessage() {
        ApiException ex = new ApiException(GeneralError.BAD_REQUEST, 4001, "Custom validation failed");

        Response response = mapper.toResponse(ex);

        assertNotNull(response);
        assertEquals(400, response.getStatus());

        ApiResponseError entity = (ApiResponseError) response.getEntity();
        assertNotNull(entity);
        assertEquals("4001", entity.getCode());
        assertEquals("Custom validation failed", entity.getMessage());
    }

    @Test
    @DisplayName("Should map NOT_FOUND and INTERNAL_SERVER_ERROR correctly")
    void testStandardErrors() {
        ApiException notFoundEx = new ApiException(GeneralError.NOT_FOUND);
        Response notFoundResponse = mapper.toResponse(notFoundEx);
        assertEquals(404, notFoundResponse.getStatus());

        ApiException serverErrorEx = new ApiException(GeneralError.INTERNAL_SERVER_ERROR);
        Response serverErrorResponse = mapper.toResponse(serverErrorEx);
        assertEquals(500, serverErrorResponse.getStatus());
    }
}
