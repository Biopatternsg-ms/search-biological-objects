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
package com.biopatternsg.domain.port.out.repositories;

import com.biopatternsg.domain.models.MinedObject;

import java.util.List;

public interface MinedObjectRepository {

    MinedObject save(MinedObject minedObject);
    MinedObject find(String biologicalObjectId, String pipelineId);
    List<MinedObject> save(List<MinedObject> minedObjects);
    List<MinedObject> find(List<String> ids, String pipelineId);
    List<MinedObject> findByLevel(int level, String pipelineId);
    List<MinedObject> findByParentId(String biologicalObjectParentId, String pipelineId);
    List<MinedObject> findByPipelineId(String pipelineId);
}
