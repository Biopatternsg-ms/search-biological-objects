package com.biopatternsg.infrastructure.adapters.out.repositories;

import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.port.out.repositories.BiologicalObjectRepository;
import com.biopatternsg.infrastructure.dtos.ExpertObjectsData;
import com.biopatternsg.infrastructure.model_mongo.BiologicalObjectCollection;
import com.biopatternsg.infrastructure.model_mongo.mappers.BiologicalObjectMapper;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
@ApplicationScoped
public class BiologicalObjectRepositoryImpl implements BiologicalObjectRepository {

    @Override
    public BiologicalObject save(BiologicalObject biologicalObject, Long userId) {

        if(biologicalObject != null){
            var mongoObject = saveBiologicalObject(biologicalObject, userId);
            return BiologicalObjectMapper.toBiologicalObject(mongoObject);
        }

        return null;
    }

    @Override
    public BiologicalObjectCollection searchDB(Long userId, ExpertObjectsData expertObjectsData) {

        StringBuilder queryBuilder = new StringBuilder("{'userId': :userId");
        Parameters parameters = Parameters.with("userId", userId);

        String searchValue = null;
        String searchField = null;

        if (expertObjectsData.uniprotId() != null && !expertObjectsData.uniprotId().isBlank()) {
            searchField = "uniprotId";
            searchValue = expertObjectsData.uniprotId();
        } else if (expertObjectsData.hgncId() != null && !expertObjectsData.hgncId().isBlank()) {
            searchField = "hgncId";
            searchValue = expertObjectsData.hgncId();
        } else if (expertObjectsData.symbol() != null && !expertObjectsData.symbol().isBlank()) {
            searchField = "symbol";
            searchValue = expertObjectsData.symbol();
        }

        if (searchField == null) {
            return null;
        }

        queryBuilder.append(", '").append(searchField).append("': :fieldValue");
        parameters.and("fieldValue", searchValue);
        queryBuilder.append("}");

        return BiologicalObjectCollection
                .find(queryBuilder.toString(), parameters)
                .firstResult();
    }

    private BiologicalObjectCollection saveBiologicalObject(BiologicalObject biologicalObject, Long userId){

        BiologicalObjectCollection mongoObject = new BiologicalObjectCollection();

        //This is temporal, I need this to assign a random userId when I create a register with the search by type function
        if(userId == null){
            Random random = new Random();
            mongoObject.setUserId(random.nextLong(1,10));
        }else{
            mongoObject.setUserId(userId);
        }

        mongoObject.setSymbol(biologicalObject.getSymbol());
        mongoObject.setName(biologicalObject.getName());
        mongoObject.setLocusType(biologicalObject.getLocusType());
        mongoObject.setHgncId(biologicalObject.getHgncId());
        mongoObject.setUniprotId(biologicalObject.getUniprotId());
        mongoObject.setSynonyms(biologicalObject.getSynonyms());
        mongoObject.setGeneOntology(biologicalObject.getGeneOntology());
        mongoObject.setTissues(biologicalObject.getTissues());
        mongoObject.setGeneFamilies(biologicalObject.getGeneFamilies());

        mongoObject.persist();

        return mongoObject;
    }
}
