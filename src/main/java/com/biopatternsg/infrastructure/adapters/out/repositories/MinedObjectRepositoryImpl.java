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
