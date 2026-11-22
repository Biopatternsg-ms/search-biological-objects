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
package com.biopatternsg.application.services.impl.biological_object_strategy;

import com.biopatternsg.application.services.BiologicalObjectSearchService;
import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.models.pipeline_config.BiologicalObjectConfig;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BiologicalObjectSearch implements BiologicalObjectSearchService, ChainResponsibility {

    private final BiologicalObjectBySymbol biologicalObjectBySymbol;
    private final BiologicalObjectByHgncId biologicalObjectByHgncId;
    private final BiologicalObjectByUniprotId biologicalObjectByUniprotId;
    private final BiologicalObjectByUserUniprotId biologicalObjectByUserUniprotId;
    private final BiologicalObjectByUserHgncId biologicalObjectByUserHgncId;
    private final BiologicalObjectByUserSymbol biologicalObjectByUserSymbol;
    private final BiologicalObjectContext biologicalObjectContext;

    public BiologicalObjectSearch(
            BiologicalObjectBySymbol biologicalObjectBySymbol,
            BiologicalObjectByHgncId biologicalObjectByHgncId,
            BiologicalObjectByUniprotId biologicalObjectByUniprotId,
            BiologicalObjectByUserUniprotId biologicalObjectByUserUniprotId,
            BiologicalObjectByUserHgncId biologicalObjectByUserHgncId,
            BiologicalObjectByUserSymbol biologicalObjectByUserSymbol,
            BiologicalObjectContext biologicalObjectContext) {
        this.biologicalObjectBySymbol = biologicalObjectBySymbol;
        this.biologicalObjectByHgncId = biologicalObjectByHgncId;
        this.biologicalObjectByUniprotId = biologicalObjectByUniprotId;
        this.biologicalObjectByUserUniprotId = biologicalObjectByUserUniprotId;
        this.biologicalObjectByUserHgncId = biologicalObjectByUserHgncId;
        this.biologicalObjectByUserSymbol = biologicalObjectByUserSymbol;
        this.biologicalObjectContext = biologicalObjectContext;

        this.biologicalObjectByUserUniprotId.setNext(this.biologicalObjectByUserHgncId);
        this.biologicalObjectByUserHgncId.setNext(this.biologicalObjectByUserSymbol);
        this.biologicalObjectByUserSymbol.setNext(this.biologicalObjectByUniprotId);
        this.biologicalObjectByUniprotId.setNext(this.biologicalObjectByHgncId);
        this.biologicalObjectByHgncId.setNext(this.biologicalObjectBySymbol);
    }

    @Override
    public BiologicalObject findByTypeAndValue(String type, String value) {
        return biologicalObjectContext.load(type).execute(value);
    }

    @Override
    public void setNext(ChainResponsibility chainResponsibility) {
    }

    @Override
    public ChainResponsibility getNext() {
        return null;
    }

    @Override
    public BiologicalObject request(BiologicalObjectConfig biologicalObjectConfig) {
        return biologicalObjectByUserUniprotId.request(biologicalObjectConfig);
    }
}
