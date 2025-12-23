package com.biopatternsg.infrastructure.mongo_db.repositories;

import com.biopatternsg.infrastructure.mongo_db.collections.BiologicalObjectCollection;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BiologicalObjectRepositoryDB implements PanacheMongoRepository<BiologicalObjectCollection> {

    public BiologicalObjectCollection findByUniprotId(String uniprotId, Long userId){

        //return find("{uniprotId: :id, userId: :user}", Parameters.with("id", uniprotId).and("user", userId)).firstResult();
        return find("{'uniprotId': ?1, 'userId': ?2}",uniprotId, userId).firstResult();
    }


}
