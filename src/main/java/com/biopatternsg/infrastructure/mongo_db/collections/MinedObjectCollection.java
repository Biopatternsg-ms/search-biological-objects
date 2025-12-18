package com.biopatternsg.infrastructure.mongo_db.collections;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@MongoEntity(collection = "minedObjects")
public class MinedObjectCollection extends PanacheMongoEntity {

    private String userId;
    private String pipelineId;
    private String biologicalObjectId;
    private String biologicalObjectParentId;
    private int level;
}
