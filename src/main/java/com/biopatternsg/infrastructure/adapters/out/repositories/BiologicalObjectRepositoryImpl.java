package com.biopatternsg.infrastructure.adapters.out.repositories;

import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.port.out.repositories.BiologicalObjectRepository;
import com.biopatternsg.infrastructure.dtos.ExpertObjectsData;
import com.biopatternsg.infrastructure.mongo_db.collections.BiologicalObjectCollection;
import com.biopatternsg.infrastructure.mongo_db.mappers.BiologicalObjectMapper;
import com.biopatternsg.infrastructure.session.SessionUtil;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class BiologicalObjectRepositoryImpl implements BiologicalObjectRepository {

    private final SessionUtil sessionUtil;

    @Override
    public BiologicalObject save(BiologicalObject biologicalObject) {

        /*
        if(biologicalObject != null){
            var mongoObject = saveBiologicalObject(biologicalObject, userId);
            return BiologicalObjectMapper.toBiologicalObject(mongoObject);
        }*/

        return null;
    }

    @Override
    public BiologicalObject findByUniprotId(String uniprotId) {
        return null;
    }

    @Override
    public BiologicalObject findByHgncId(String hgncId) {
        return null;
    }

    @Override
    public BiologicalObject findBySymbol(String symbol) {
        return null;
    }

    /*
    @Override
    public BiologicalObjectCollection searchDB(ExpertObjectsData expertObjectsData) {

        //Aca buscaria por el userId que llega del header
        SessionUtil.

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
    }*/

    private BiologicalObjectCollection saveBiologicalObject(BiologicalObject biologicalObject, Long userId){

        BiologicalObjectCollection mongoObject = new BiologicalObjectCollection();

        mongoObject.setUserId(userId);
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
