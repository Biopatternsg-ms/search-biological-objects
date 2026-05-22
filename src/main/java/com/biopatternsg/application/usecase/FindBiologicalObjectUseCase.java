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
package com.biopatternsg.application.usecase;

import com.biopatternsg.application.services.impl.biological_object_strategy.BiologicalObjectContext;
import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.port.in.FindBiologicalObject;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ApplicationScoped
public class FindBiologicalObjectUseCase implements FindBiologicalObject {

    private final BiologicalObjectContext biologicalObjectContext;

    @Override
    public BiologicalObject execute(String type, String value) {

        var context = biologicalObjectContext.load(type);
        return context.execute(value);
    }
}
