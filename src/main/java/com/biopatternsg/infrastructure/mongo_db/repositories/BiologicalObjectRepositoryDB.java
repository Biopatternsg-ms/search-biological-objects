/*
 * Copyright © 2026 biopatternsg (biopatternsg@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.biopatternsg.infrastructure.mongo_db.repositories;

import com.biopatternsg.infrastructure.mongo_db.collections.BiologicalObjectCollection;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;

import java.util.List;

@ApplicationScoped
public class BiologicalObjectRepositoryDB implements PanacheMongoRepository<BiologicalObjectCollection> {

    public BiologicalObjectCollection findByUniprotId(String uniprotId, String userId){

        //return find("{uniprotId: :id, userId: :user}", Parameters.with("id", uniprotId).and("user", userId)).firstResult();
        return find("{'uniprotId': ?1, 'userId': ?2}",uniprotId, userId).firstResult();
    }

}
