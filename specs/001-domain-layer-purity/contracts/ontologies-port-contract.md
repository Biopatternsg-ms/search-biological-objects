# Ontologies Port Contract

## Outbound Port Definition

```java
package com.biopatternsg.domain.port.out.external_repositories;

import com.biopatternsg.domain.models.BiologicalObject;
import com.biopatternsg.domain.models.GeneOntology;

public interface OntologiesRepository {
    void buildGeneOntologyTree(GeneOntology geneOntology);
    void buildMeshOntologyTree(BiologicalObject biologicalObject);
}
```

## Outbound Adapter Implementation Guarantees

The implementation `OntologiesAdapter` in `infrastructure.adapters.out.external_repositories` guarantees:

1. **Isolation**: No `Response` or HTTP transport primitives escape the adapter boundary.
2. **Resource Safety**: Every `jakarta.ws.rs.core.Response` returned by `QueryOntologies` is managed inside a `try-with-resources` block to prevent socket or buffer leakage.
3. **Graceful Entity Reading**: If an entity payload is present, it is read and logged within the adapter before the response closes.
4. **Exception Containment**: Network and serialization exceptions from the client are handled gracefully without breaking the caller.
