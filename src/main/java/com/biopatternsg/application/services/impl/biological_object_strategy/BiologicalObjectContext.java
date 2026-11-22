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

import com.biopatternsg.domain.exceptions.ApiException;
import com.biopatternsg.domain.exceptions.GeneralError;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ApplicationScoped
public class BiologicalObjectContext{

    private final BiologicalObjectBySymbol biologicalObjectBySymbol;
    private final BiologicalObjectByHgncId biologicalObjectByHgncId;
    private final BiologicalObjectByUniprotId biologicalObjectByUniprotId;
    private final BiologicalObjectByUserUniprotId biologicalObjectByUserUniprotId;
    private final BiologicalObjectByUserHgncId biologicalObjectByUserHgncId;
    private final BiologicalObjectByUserSymbol biologicalObjectByUserSymbol;

    public BuildBiologicalObjectStrategy load(String type){
        if (type == null) {
            throw new ApiException(GeneralError.BAD_REQUEST, 400, "Biological object type must not be null");
        }
        BiologicalObjectEnum value = BiologicalObjectEnum.getValue(type);
        if (value == null) {
            throw new ApiException(GeneralError.BAD_REQUEST, 400, "Unsupported biological object type: " + type);
        }
        return switch (value){
            case SYMBOL -> biologicalObjectBySymbol;
            case HGNC_ID -> biologicalObjectByHgncId;
            case UNIPROT_ID -> biologicalObjectByUniprotId;
            case USER_SYMBOL -> biologicalObjectByUserSymbol;
            case USER_HGNC_ID -> biologicalObjectByUserHgncId;
            case USER_UNIPROT_ID -> biologicalObjectByUserUniprotId;
        };
    }
}
