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
