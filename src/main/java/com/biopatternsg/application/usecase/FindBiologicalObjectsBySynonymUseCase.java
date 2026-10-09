package com.biopatternsg.application.usecase;

import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.port.in.FindBiologicalObjectsBySynonym;
import com.biopatternsg.domain.port.out.repositories.BiologicalObjectRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
public class FindBiologicalObjectsBySynonymUseCase implements FindBiologicalObjectsBySynonym {

    private final BiologicalObjectRepository biologicalObjectRepository;

    @Override
    public List<BiologicalObject> execute(String synonym) {
        return biologicalObjectRepository.findBySynonym(synonym);
    }
}
