package com.biopatternsg.infrastructure.adapters.out.repositories;

import com.biopatternsg.domain.port.out.repositories.UserRepository;
import com.biopatternsg.infrastructure.session.SessionUtil;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final SessionUtil sessionUtil;
    @Override
    public String getUserId() {
        return sessionUtil.getUserId();
    }
}
