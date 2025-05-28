package com.biopatternsg.infrastructure.adapters.out.repositories;

import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.port.out.repositories.BiologicalObjectRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class BiologicalObjectRepositoryImpl implements BiologicalObjectRepository {

    @Override
    public BiologicalObject save(BiologicalObject biologicalObject) {
        return biologicalObject;
    }
}
