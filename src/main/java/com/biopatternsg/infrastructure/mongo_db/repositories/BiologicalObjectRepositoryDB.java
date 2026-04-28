package com.biopatternsg.infrastructure.mongo_db.repositories;

import com.biopatternsg.infrastructure.mongo_db.collections.BiologicalObjectCollection;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.List;

@ApplicationScoped
public class BiologicalObjectRepositoryDB implements PanacheMongoRepository<BiologicalObjectCollection> {

    public BiologicalObjectCollection findByUniprotId(String uniprotId, String userId){

        //return find("{uniprotId: :id, userId: :user}", Parameters.with("id", uniprotId).and("user", userId)).firstResult();
        return find("{'uniprotId': ?1, 'userId': ?2}",uniprotId, userId).firstResult();
    }


    public List<BiologicalObjectCollection> findByIds(List<String> ids) {
        return find("{'_id': {'$in': ?1}}", ids.stream().map(ObjectId::new).toList()).list();
    }


}
