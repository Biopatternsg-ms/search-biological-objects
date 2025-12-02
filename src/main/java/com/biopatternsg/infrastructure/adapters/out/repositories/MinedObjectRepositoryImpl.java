package com.biopatternsg.infrastructure.adapters.out.repositories;

import com.biopatternsg.domain.models.MinedObject;
import com.biopatternsg.domain.port.out.repositories.MinedObjectRepository;
import com.biopatternsg.infrastructure.mongo_db.collections.MinedObjectCollection;
import com.biopatternsg.infrastructure.mongo_db.mappers.MinedObjectMapper;
import com.biopatternsg.infrastructure.session.SessionUtil;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class MinedObjectRepositoryImpl implements MinedObjectRepository {

    private final SessionUtil sessionUtil;
    @Override
    public MinedObject save(MinedObject minedObject) {

        if(minedObject == null){return null;}

        var mongoObject = saveMinedCollection(minedObject);
        return MinedObjectMapper.toMinedObject(mongoObject);
    }

    @Override
    public MinedObject find(String biologicalObjectId) {

        StringBuilder queryBuilder = new StringBuilder("{'biologicalObjectId': :objectId");
        Parameters parameters = Parameters.with("objectId", biologicalObjectId);
        queryBuilder.append("}");

        MinedObjectCollection mongoObject = MinedObjectCollection
                .find(queryBuilder.toString(), parameters)
                .firstResult();

        if(mongoObject == null){return null;}

        return MinedObjectMapper.toMinedObject(mongoObject);
    }

    @Override
    public List<MinedObject> findLevelList(int levels, String pipelineId) {

        StringBuilder queryBuilder = new StringBuilder("{'pipelineId': :pipelineId");
        Parameters parameters = Parameters.with("pipelineId", pipelineId);
        queryBuilder.append(", '").append("userId").append("': :userId");
        parameters.and("userId", sessionUtil.getUserId());
        queryBuilder.append(", '").append("level").append("': :level");
        parameters.and("level", levels);
        queryBuilder.append("}, {'biologicalObjectId': 1, '_id': 0}");

        var collectionList = MinedObjectCollection
                .find(queryBuilder.toString(), parameters)
                .list();

        if(collectionList == null){ return null;}

        List<MinedObject> response = new ArrayList<>();
        collectionList.forEach(register ->
                response.add(MinedObjectMapper.toMinedObject((MinedObjectCollection) register)));

        return response;
    }

    @Override
    public List<MinedObject> findPipelineList(String pipelineId) {

        StringBuilder queryBuilder = new StringBuilder("{'pipelineId': :pipelineId");
        Parameters parameters = Parameters.with("pipelineId", pipelineId);
        queryBuilder.append(", '").append("userId").append("': :userId");
        parameters.and("userId", sessionUtil.getUserId());
        queryBuilder.append("}, {'biologicalObjectId': 1, '_id': 0}");

        var collectionList = MinedObjectCollection
                .find(queryBuilder.toString(), parameters)
                .list();

        if(collectionList == null){ return null;}

        List<MinedObject> response = new ArrayList<>();
        collectionList.forEach(register ->
                response.add(MinedObjectMapper.toMinedObject((MinedObjectCollection) register)));

        return response;
    }

    private MinedObjectCollection saveMinedCollection(MinedObject minedObject) {

        MinedObjectCollection mongoObject = new MinedObjectCollection();

        mongoObject.setUserId(sessionUtil.getUserId());
        mongoObject.setPipelineId(minedObject.getPipelineId());
        mongoObject.setBiologicalObjectId(minedObject.getBiologicalObjectId());
        mongoObject.setUniprotId(minedObject.getUniprotId());
        mongoObject.setUniprotIdFather(minedObject.getUniprotIdFather());
        mongoObject.setLevel(minedObject.getLevel());

        mongoObject.persist();

        return mongoObject;
    }
}
