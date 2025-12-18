package com.biopatternsg.infrastructure.session;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.MultivaluedMap;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApplicationScoped
public class SessionUtil {

    private MultivaluedMap<String, String> context;

    public String getUserId(){ return this.context.get("userId").getFirst(); }
    //public Long getUserId(){ return Long.parseLong(this.context.get("userId").getFirst()); }
}
