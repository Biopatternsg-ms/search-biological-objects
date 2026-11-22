# Interface Contract: BiologicalObjectSearchService

## Service Contract Definition

```java
package com.biopatternsg.application.services;

import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.models.pipeline_config.BiologicalObjectConfig;

public interface BiologicalObjectSearchService {

    /**
     * Finds a biological object by type identifier (e.g. symbol, hgnc_id, uniprot_id) and value.
     *
     * @param type  the identifier type (must match a valid BiologicalObjectEnum)
     * @param value the identifier value to resolve
     * @return the resolved BiologicalObject
     * @throws com.biopatternsg.domain.exceptions.ApiException if type is invalid or value cannot be processed
     */
    BiologicalObject findByTypeAndValue(String type, String value);

    /**
     * Resolves a biological object through the chain of responsibility based on pipeline configuration.
     *
     * @param biologicalObjectConfig the search configuration specifying user/global IDs
     * @return the resolved BiologicalObject
     */
    BiologicalObject request(BiologicalObjectConfig biologicalObjectConfig);
}
```

## Contract Guarantees

1. **Strict Layer Boundary**: Clients in `application.usecase.*` depend solely on this public interface without referencing `application.services.impl.*`.
2. **Deterministic Validation**: Passing a null or invalid `type` to `findByTypeAndValue` throws `ApiException(GeneralError.BAD_REQUEST)` with HTTP 400 semantics.
3. **Thread-Safety**: Invocations of `request(BiologicalObjectConfig)` execute statelessly against pre-wired, immutable chain links with zero concurrency interference.
