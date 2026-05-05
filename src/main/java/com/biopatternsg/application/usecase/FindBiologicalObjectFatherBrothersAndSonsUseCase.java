package com.biopatternsg.application.usecase;

import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.models.MinedObject;
import com.biopatternsg.domain.port.in.FindBiologicalObjectFatherBrothersAndSons;
import com.biopatternsg.domain.port.out.repositories.BiologicalObjectRepository;
import com.biopatternsg.domain.port.out.repositories.MinedObjectRepository;
import com.biopatternsg.infrastructure.adapters.dtos.BiologicalObjectDTO;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@ApplicationScoped
public class FindBiologicalObjectFatherBrothersAndSonsUseCase implements FindBiologicalObjectFatherBrothersAndSons {

    private final MinedObjectRepository minedObjectRepository;
    private final BiologicalObjectRepository biologicalObjectRepository;

    @Override
    public List<BiologicalObjectDTO> execute(String pipelineId, String biologicalObjectId) {

        MinedObject currentMinedObject = minedObjectRepository.find(biologicalObjectId, pipelineId);

        List<MinedObject> currentMinedBrothers = minedObjectRepository.findByParentId(currentMinedObject.getBiologicalObjectParentId(), pipelineId);

        BiologicalObject currentBiologicalObject = biologicalObjectRepository.findById(currentMinedObject.getBiologicalObjectId());
        BiologicalObject currentFather = biologicalObjectRepository.findById(currentMinedObject.getBiologicalObjectParentId());
        List<BiologicalObject> currentBrothers = biologicalObjectRepository.findByIds(
                currentMinedBrothers.stream()
                        .filter(brother -> !brother.getId().equals(currentBiologicalObject.getId()))
                        .map(MinedObject::getBiologicalObjectId).toList()
        );

        List<MinedObject> currentMinedSons = minedObjectRepository.findByParentId(currentBiologicalObject.getId(), pipelineId);
        List<BiologicalObject> currentSons = biologicalObjectRepository.findByIds(
                currentMinedSons.stream().map(MinedObject::getBiologicalObjectId).toList()
        );

        Map<String, BiologicalObject> biologicalObjectsMap = new HashMap<>();
        biologicalObjectsMap.put(currentBiologicalObject.getId(), currentBiologicalObject);
        biologicalObjectsMap.put(currentFather.getId(), currentFather);
        biologicalObjectsMap.putAll(currentBrothers.stream().collect(Collectors.toMap(BiologicalObject::getId, Function.identity())));
        biologicalObjectsMap.putAll(currentSons.stream().collect(Collectors.toMap(BiologicalObject::getId, Function.identity())));

        List<BiologicalObject> biologicalObjects = biologicalObjectsMap.values().stream().toList();

        return biologicalObjects.stream().map(BiologicalObjectDTO::fromDomain).toList();
    }
}
