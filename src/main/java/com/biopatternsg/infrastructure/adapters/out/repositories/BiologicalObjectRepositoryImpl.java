package com.biopatternsg.infrastructure.adapters.out.repositories;

import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.port.out.repositories.BiologicalObjectRepository;
import com.biopatternsg.infrastructure.mongo_db.collections.BiologicalObjectCollection;
import com.biopatternsg.infrastructure.mongo_db.mappers.BiologicalObjectMapper;
import com.biopatternsg.infrastructure.mongo_db.repositories.BiologicalObjectRepositoryDB;
import com.biopatternsg.infrastructure.session.SessionUtil;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;

import java.util.List;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class BiologicalObjectRepositoryImpl implements BiologicalObjectRepository {

    private final SessionUtil sessionUtil;
    private final BiologicalObjectRepositoryDB biologicalObjectRepositoryDB;

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
    public List<BiologicalObject> findByIds(List<String> ids) {

        var mongoObjects = biologicalObjectRepositoryDB.findByIds(ids);
        return BiologicalObjectMapper.toBiologicalObjects(mongoObjects);

    }

    @Override
    public BiologicalObject findByUniprotId(String uniprotId) {

        var mongoObject = biologicalObjectRepositoryDB.findByUniprotId(uniprotId, sessionUtil.getUserId());
        return mongoObject.map(BiologicalObjectMapper::toBiologicalObject).orElse(null);
    }

    @Override
    public BiologicalObject findByHgncId(String hgncId) {

        var mongoObject = biologicalObjectRepositoryDB.findByHgncId(hgncId, sessionUtil.getUserId());
        return mongoObject.map(BiologicalObjectMapper::toBiologicalObject).orElse(null);
    }

    @Override
    public BiologicalObject findBySymbol(String symbol) {

        var mongoObject = biologicalObjectRepositoryDB.findBySymbol(symbol, sessionUtil.getUserId());
        return mongoObject.map(BiologicalObjectMapper::toBiologicalObject).orElse(null);
    }

    private BiologicalObjectCollection saveBiologicalObject(BiologicalObject biologicalObject){

        BiologicalObjectCollection mongoObject = new BiologicalObjectCollection();

        mongoObject.setUserId(sessionUtil.getUserId());
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
