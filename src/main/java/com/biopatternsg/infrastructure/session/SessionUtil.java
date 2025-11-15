package com.biopatternsg.infrastructure.session;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;

@RequestScoped
public class SessionUtil {

    @Context
    HttpHeaders httpHeaders;

    public Long getUserId(){

        return Long.parseLong(httpHeaders.getRequestHeader("userId").getFirst());
    }
}
