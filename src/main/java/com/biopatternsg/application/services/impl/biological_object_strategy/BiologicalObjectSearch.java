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

import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.models.pipeline_config.BiologicalObjectConfig;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class BiologicalObjectSearch implements ChainResponsibility {

    @NonNull
    public BiologicalObjectBySymbol biologicalObjectBySymbol;
    @NonNull
    public BiologicalObjectByHgncId biologicalObjectByHgncId;
    @NonNull
    public BiologicalObjectByUniprotId biologicalObjectByUniprotId;
    @NonNull
    public BiologicalObjectByUserUniprotId biologicalObjectByUserUniprotId;
    @NonNull
    public BiologicalObjectByUserHgncId biologicalObjectByUserHgncId;
    @NonNull
    public BiologicalObjectByUserSymbol biologicalObjectByUserSymbol;

    public ChainResponsibility next;

    @Override
    public void setNext(ChainResponsibility chainResponsibility) {
        this.next = chainResponsibility;
    }

    @Override
    public ChainResponsibility getNext() {
        return this.next;
    }

    @Override
    public BiologicalObject request(BiologicalObjectConfig biologicalObjectConfig) {

        setNext(biologicalObjectByUserUniprotId);
        biologicalObjectByUserUniprotId.setNext(biologicalObjectByUserHgncId);
        biologicalObjectByUserHgncId.setNext(biologicalObjectByUserSymbol);
        biologicalObjectByUserSymbol.setNext(biologicalObjectByUniprotId);
        biologicalObjectByUniprotId.setNext(biologicalObjectByHgncId);
        biologicalObjectByHgncId.setNext(biologicalObjectBySymbol);

        return next.request(biologicalObjectConfig);
    }
}
