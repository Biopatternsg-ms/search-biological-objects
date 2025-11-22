package com.biopatternsg.infrastructure.adapters.out.repositories;

import com.biopatternsg.domain.models.MinedObject;
import com.biopatternsg.domain.port.out.repositories.MinedObjectRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class MinedObjectRepositoryImpl implements MinedObjectRepository {

    @Override
    public MinedObject save(MinedObject minedObject) {
        return null;
    }
}
