package com.biopatternsg.infrastructure.mongo_db.repositories;

import com.biopatternsg.infrastructure.mongo_db.collections.BiologicalObjectCollection;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BiologicalObjectRepositoryDB implements PanacheMongoRepository<BiologicalObjectCollection> {

    /*public Optional<BiologicalObjectCollection> findById(String id) {
        return find("_id = ?1", id).firstResultOptional();
    }*/

    public Optional<BiologicalObjectCollection> findByUniprotId(String uniprotId, String userId) {
        return find("{uniprotId: ?1, userId: ?2}", uniprotId, userId).firstResultOptional();
    }

    public Optional<BiologicalObjectCollection> findByHgncId(String hgncId, String userId) {
        return find("{hgncId: ?1, userId: ?2}", hgncId, userId).firstResultOptional();
    }

    public Optional<BiologicalObjectCollection> findBySymbol(String symbol, String userId) {
        return find("{symbol: ?1, userId: ?2}", symbol, userId).firstResultOptional();
    }

    public List<BiologicalObjectCollection> findByIds(List<String> ids) {
        return find("{_id IN ?1}", ids).list();
    }
}
