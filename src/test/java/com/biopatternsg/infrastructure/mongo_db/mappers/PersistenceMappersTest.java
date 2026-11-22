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
package com.biopatternsg.infrastructure.mongo_db.mappers;

import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.models.MinedObject;
import com.biopatternsg.infrastructure.mongo_db.collections.BiologicalObjectCollection;
import com.biopatternsg.infrastructure.mongo_db.collections.MinedObjectCollection;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersistenceMappersTest {

    @Test
    @DisplayName("Should enforce final class and private constructor on persistence mappers")
    void shouldEnforceFinalClassAndPrivateConstructor() throws NoSuchMethodException {
        assertTrue(Modifier.isFinal(MinedObjectMapper.class.getModifiers()));
        assertTrue(Modifier.isPrivate(MinedObjectMapper.class.getDeclaredConstructor().getModifiers()));

        assertTrue(Modifier.isFinal(BiologicalObjectMapper.class.getModifiers()));
        assertTrue(Modifier.isPrivate(BiologicalObjectMapper.class.getDeclaredConstructor().getModifiers()));
    }

    @Test
    @DisplayName("Should handle null inputs and null IDs safely in MinedObjectMapper")
    void shouldHandleNullInputsInMinedObjectMapper() {
        assertNull(MinedObjectMapper.toMinedObject(null));
        assertNull(MinedObjectMapper.toMinedObjectCollection(null));
        assertTrue(MinedObjectMapper.toMinedObjects(null).isEmpty());

        MinedObjectCollection collectionWithoutId = MinedObjectCollection.builder()
                .pipelineId("pipe-1")
                .biologicalObjectId("bio-1")
                .level(1)
                .build();
        MinedObject mapped = MinedObjectMapper.toMinedObject(collectionWithoutId);
        assertNotNull(mapped);
        assertNull(mapped.getId());
        assertEquals("pipe-1", mapped.getPipelineId());

        ObjectId validId = new ObjectId();
        MinedObject withValidId = MinedObject.builder()
                .id(validId.toHexString())
                .pipelineId("pipe-1")
                .biologicalObjectId("bio-1")
                .level(2)
                .build();
        MinedObjectCollection mappedCollection = MinedObjectMapper.toMinedObjectCollection(withValidId);
        assertNotNull(mappedCollection);
        assertEquals(validId, mappedCollection.id);
    }

    @Test
    @DisplayName("Should handle null inputs and null IDs safely in BiologicalObjectMapper")
    void shouldHandleNullInputsInBiologicalObjectMapper() {
        assertNull(BiologicalObjectMapper.toBiologicalObject(null));
        assertNull(BiologicalObjectMapper.toBiologicalObjectCollection(null));
        assertTrue(BiologicalObjectMapper.toBiologicalObjects(null).isEmpty());

        BiologicalObjectCollection collectionWithoutId = BiologicalObjectCollection.builder()
                .symbol("TP53")
                .name("Tumor protein p53")
                .build();
        BiologicalObject mapped = BiologicalObjectMapper.toBiologicalObject(collectionWithoutId);
        assertNotNull(mapped);
        assertNull(mapped.getId());
        assertEquals("TP53", mapped.getSymbol());
    }
}
