package com.biopatternsg.application.usecase;

import com.biopatternsg.application.services.impl.BiologicalObjectStrategy.BiologicalObjectContext;
import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.port.in.FindBiologicalObject;
import com.biopatternsg.domain.port.out.repositories.BiologicalObjectRepository;
import com.biopatternsg.infrastructure.dtos.ExpertObjects;
import com.biopatternsg.infrastructure.dtos.ExpertObjectsData;
import com.biopatternsg.infrastructure.model_mongo.mappers.BiologicalObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@ApplicationScoped
public class FindBiologicalObjectUseCase implements FindBiologicalObject {

    private final BiologicalObjectContext biologicalObjectContext;
    private final BiologicalObjectRepository biologicalObjectRepository;

    @Override
    public BiologicalObject execute(String type, String value) {

        var context = biologicalObjectContext.load(type);
        var biologicalObject = context.execute(value);
        return biologicalObjectRepository.save(biologicalObject, null);
    }

    @Override
    public List<BiologicalObject> experiment(ExpertObjects expertObjects) {

        List<BiologicalObject> biologicalObjectsList = new ArrayList<>();
        expertObjects.expertObjectsList().forEach(expertObjectsData -> {

            BiologicalObject biologicalObject = null;
            var biologicalObjectCollection = biologicalObjectRepository.searchDB(expertObjects.userId(),expertObjectsData);
            if(biologicalObjectCollection == null){
                var contextValues = contextValues(expertObjectsData);
                if(contextValues.get("type") != null && !contextValues.get("type").isBlank()){
                    var context = biologicalObjectContext.load(contextValues.get("type"));
                    var findbiologicalObject = context.execute(contextValues.get("value"));
                    biologicalObject = biologicalObjectRepository.save(findbiologicalObject, expertObjects.userId());
                }
            }else{
                biologicalObject = BiologicalObjectMapper.toBiologicalObject(biologicalObjectCollection);
            }

            if(biologicalObject != null){
                biologicalObjectsList.add(biologicalObject);
            }
        });

        return biologicalObjectsList;
    }

    /*
    @Override
    public BiologicalObject execute(TranscriptionFactor transcriptionFactor) {
        return buildBiologicalObjectService.execute(transcriptionFactor);
    }
    */

    private Map<String, String> contextValues(ExpertObjectsData expertObjectsData) {

        Map<String, String> context = new HashMap<>();
        if (expertObjectsData.uniprotId() != null && !expertObjectsData.uniprotId().isBlank()) {
            context.put("type", "uniprot");
            context.put("value", expertObjectsData.uniprotId());
        } else if (expertObjectsData.hgncId() != null && !expertObjectsData.hgncId().isBlank()) {
            context.put("type", "hgnc");
            context.put("value", expertObjectsData.hgncId());
        } else if (expertObjectsData.symbol() != null && !expertObjectsData.symbol().isBlank()) {
            context.put("type", "symbol");
            context.put("value", expertObjectsData.symbol());
        }

        return context;
    }
}
