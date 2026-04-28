package com.biopatternsg.infrastructure.adapters.out.repositories;

import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.port.out.repositories.BiologicalObjectRepository;
import com.biopatternsg.domain.port.out.repositories.UserRepository;
import com.biopatternsg.infrastructure.mongo_db.collections.BiologicalObjectCollection;
import com.biopatternsg.infrastructure.mongo_db.mappers.BiologicalObjectMapper;
import com.biopatternsg.infrastructure.mongo_db.repositories.BiologicalObjectRepositoryDB;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;

import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
public class BiologicalObjectRepositoryAdapter implements BiologicalObjectRepository, PanacheMongoRepository<BiologicalObjectCollection> {

    @Inject
    private BiologicalObjectRepositoryDB biologicalObjectRepositoryDB;
    @Inject
    private UserRepository userRepository;

    @Override
    public BiologicalObject save(BiologicalObject biologicalObject) {

        if(biologicalObject == null){return null;}

        var mongoObject = saveBiologicalObject(biologicalObject);
        return BiologicalObjectMapper.toBiologicalObject(mongoObject);
    }

    @Override
    public BiologicalObject findById(String id) {

        var mongoObject = biologicalObjectRepositoryDB.findById(new ObjectId(id));
        return BiologicalObjectMapper.toBiologicalObject(mongoObject);
    }

    @Override
    public BiologicalObject findByUniprotId(String uniprotId) {

        var mongoObject = biologicalObjectRepositoryDB.findByUniprotId(uniprotId, userRepository.getUserId());
        return BiologicalObjectMapper.toBiologicalObject(mongoObject);
    }

    @Override
    public BiologicalObject findByHgncId(String hgncId) {

        StringBuilder queryBuilder = new StringBuilder("{'userId': :userId");
        Parameters parameters = Parameters.with("userId", userRepository.getUserId());
        queryBuilder.append(", '").append("hgncId").append("': :fieldValue");
        parameters.and("fieldValue", hgncId);
        queryBuilder.append("}");

        BiologicalObjectCollection mongoObject = BiologicalObjectCollection
                .find(queryBuilder.toString(), parameters)
                .firstResult();

        if(mongoObject == null){return null;}

        return BiologicalObjectMapper.toBiologicalObject(mongoObject);
    }

    @Override
    public BiologicalObject findBySymbol(String symbol) {

        StringBuilder queryBuilder = new StringBuilder("{'userId': :userId");
        Parameters parameters = Parameters.with("userId", userRepository.getUserId());
        queryBuilder.append(", '").append("symbol").append("': :fieldValue");
        parameters.and("fieldValue", symbol);
        queryBuilder.append("}");

        BiologicalObjectCollection mongoObject = BiologicalObjectCollection
                .find(queryBuilder.toString(), parameters)
                .firstResult();

        if(mongoObject == null){return null;}

        return BiologicalObjectMapper.toBiologicalObject(mongoObject);
    }

    @Override
    public void update(BiologicalObject biologicalObject) {
        BiologicalObjectCollection mongoObject = BiologicalObjectMapper.toBiologicalObjectCollection(biologicalObject);
        mongoObject.id = new ObjectId(biologicalObject.getId());

        persistOrUpdate(mongoObject);
    }

    @Override
    public List<BiologicalObject> findByIds(List<String> ids) {
        return BiologicalObjectMapper.toBiologicalObjects(biologicalObjectRepositoryDB.findByIds(ids));
    }


    private BiologicalObjectCollection saveBiologicalObject(BiologicalObject biologicalObject){

        BiologicalObjectCollection mongoObject = new BiologicalObjectCollection();

        mongoObject.setUserId(biologicalObject.getUserId());
        mongoObject.setSymbol(biologicalObject.getSymbol());
        mongoObject.setName(biologicalObject.getName());
        mongoObject.setLocusType(biologicalObject.getLocusType());
        mongoObject.setHgncId(biologicalObject.getHgncId());
        mongoObject.setUniprotId(biologicalObject.getUniprotId());
        mongoObject.setSynonyms(biologicalObject.getSynonyms());
        mongoObject.setGeneOntology(biologicalObject.getGeneOntology());
        mongoObject.setTissues(biologicalObject.getTissues());
        mongoObject.setGeneFamilies(biologicalObject.getGeneFamilies());
        mongoObject.setTranscriptionFactor(biologicalObject.getTranscriptionFactor());

        mongoObject.persist();

        return mongoObject;
    }
}
