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
    public List<MinedObject> save(List<MinedObject> minedObjects) {

        List<MinedObjectCollection> listMinedObjectsCollection = minedObjects.stream().map(minedObject ->
                MinedObjectCollection.builder()
                        .userId(sessionUtil.getUserId())
                        .pipelineId(minedObject.getPipelineId())
                        .biologicalObjectId(minedObject.getBiologicalObjectId())
                        .biologicalObjectParentId(minedObject.getBiologicalObjectParentId())
                        .level(minedObject.getLevel())
                        .build()
                ).toList();

        MinedObjectCollection.persist(listMinedObjectsCollection);

        return MinedObjectMapper.toMinedObjects(listMinedObjectsCollection);
    }

    @Override
    public List<MinedObject> find(List<String> ids, String pipelineId) {

        List<MinedObjectCollection> response = new ArrayList<>(MinedObjectCollection.list("biologicalObjectId IN ?1 and pipelineId = ?2", ids, pipelineId));
        return MinedObjectMapper.toMinedObjects(response);
    }

    @Override
    public List<MinedObject> findByLevel(int level, String pipelineId) {
        List<MinedObjectCollection> response = new ArrayList<>(MinedObjectCollection.list("level = ?1 and pipelineId = ?2", level, pipelineId));
        return MinedObjectMapper.toMinedObjects(response);
    }

    @Override
    public MinedObject find(String biologicalObjectId, String pipelineId) {

        StringBuilder queryBuilder = new StringBuilder("{'biologicalObjectId': :objectId");
        Parameters parameters = Parameters.with("objectId", biologicalObjectId);
        queryBuilder.append(", '").append("pipelineId").append("': :pipelineId");
        parameters.and("pipelineId", pipelineId);
        queryBuilder.append("}");

        MinedObjectCollection mongoObject = MinedObjectCollection
                .find(queryBuilder.toString(), parameters)
                .firstResult();

        if(mongoObject == null){return null;}

        return MinedObjectMapper.toMinedObject(mongoObject);
    }

    private MinedObjectCollection saveMinedCollection(MinedObject minedObject) {

        MinedObjectCollection mongoObject = new MinedObjectCollection();

        mongoObject.setUserId(sessionUtil.getUserId());
        mongoObject.setPipelineId(minedObject.getPipelineId());
        mongoObject.setBiologicalObjectId(minedObject.getBiologicalObjectId());
        mongoObject.setBiologicalObjectId(minedObject.getBiologicalObjectId());
        mongoObject.setBiologicalObjectParentId(minedObject.getBiologicalObjectParentId());
        mongoObject.setLevel(minedObject.getLevel());

        mongoObject.persist();

        return mongoObject;
    }
}
