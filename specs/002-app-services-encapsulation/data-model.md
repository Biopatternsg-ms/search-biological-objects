# Data Model & Service Architecture: Application Services Encapsulation

## Service Interfaces & Implementations

### 1. `BiologicalObjectSearchService` (`com.biopatternsg.application.services.BiologicalObjectSearchService`)

Primary public application service interface for resolving biological objects across use cases and pipelines.

- **Package**: `com.biopatternsg.application.services`
- **Scope**: Public Interface

**Methods**:
| Method Signature | Input | Output | Description |
|------------------|-------|--------|-------------|
| `BiologicalObject findByTypeAndValue(String type, String value)` | `String type`, `String value` | `BiologicalObject` | Resolves biological object by strategy type (symbol, hgnc, uniprot) |
| `BiologicalObject request(BiologicalObjectConfig biologicalObjectConfig)` | `BiologicalObjectConfig` | `BiologicalObject` | Resolves biological object through priority search chain |

---

### 2. `BiologicalObjectSearch` (`com.biopatternsg.application.services.impl.biological_object_strategy.BiologicalObjectSearch`)

Thread-safe `@ApplicationScoped` singleton implementation of `BiologicalObjectSearchService` and `ChainResponsibility`.

- **Package**: `com.biopatternsg.application.services.impl.biological_object_strategy`
- **Encapsulation Rules**:
  - All strategy dependencies (`biologicalObjectBySymbol`, `biologicalObjectByHgncId`, etc.) are `private final`.
  - Injected via constructor (`@RequiredArgsConstructor` or explicit constructor).
  - Chain wiring initialized once during construction; zero per-request mutations.
  - Zero `public` fields.

---

### 3. `BiologicalObjectContext` (`com.biopatternsg.application.services.impl.biological_object_strategy.BiologicalObjectContext`)

Strategy selector responsible for instantiating or selecting the appropriate `BuildBiologicalObjectStrategy`.

- **Package**: `com.biopatternsg.application.services.impl.biological_object_strategy`
- **Validation**:
  - Checks if `BiologicalObjectEnum.getValue(type)` is null.
  - Throws `ApiException(GeneralError.BAD_REQUEST, 400, "Unsupported biological object type: " + type)` when invalid.
  - Zero `assert` keywords.

---

### 4. `DiscoveryObjectContext` (`com.biopatternsg.application.services.impl.discovery_object_strategy.DiscoveryObjectContext`)

Strategy selector for discovery objects.

- **Package**: `com.biopatternsg.application.services.impl.discovery_object_strategy`
- **Encapsulation Rules**:
  - `discoveryObjectByPdb` field is `private final`.
  - Replaces `assert value != null;` with explicit `ApiException(GeneralError.BAD_REQUEST, ...)`.

---

### 5. `DiscoveryObjectService` (`com.biopatternsg.application.services.DiscoveryObjectService`)

Cleaned interface for discovery object operations.

- **Package**: `com.biopatternsg.application.services`
- **Active Method**:
  - `List<String> execute(String id, Integer maxComplexes)`
- **Removed Method**:
  - `void execute(PipelineConfig pipelineConfig, String context)` (dead code overload removed).
