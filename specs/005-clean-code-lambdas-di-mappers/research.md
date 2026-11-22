# Research & Design Decisions: Clean Code Lambdas, Constructor DI, & Static Mappers

**Feature**: `005-clean-code-lambdas-di-mappers` | **Date**: 2026-11-22

## 1. Constructor Injection in `BiologicalObjectRepositoryAdapter` & `PdbAdapter`

- **Decision**:
  - In `BiologicalObjectRepositoryAdapter`: Check all fields (lines 30-45 & end of class if any) and convert `biologicalObjectRepositoryDB` and `userRepository` from `@Inject private` to `private final`, allowing `@RequiredArgsConstructor` to generate the single constructor.
  - In `PdbAdapter`: Convert `targetOrganism` to `private final String targetOrganism` and add `@ConfigProperty(name = "pdbe.target-organism", defaultValue = "Homo sapiens") String targetOrganism` as a parameter of the existing `@Inject` constructor.
- **Rationale**: Quarkus ArC supports `@ConfigProperty` on `@Inject` constructor parameters natively, enabling direct constructor instantiation in unit tests without reflection.

## 2. Persistence Static Mapper Standardization (`MinedObjectMapper` & `BiologicalObjectMapper`)

- **Decision**:
  - Declare both `MinedObjectMapper` and `BiologicalObjectMapper` as `public final class` with an explicit `private` no-arg constructor (removing `@UtilityClass` from `BiologicalObjectMapper` for consistency with `infrastructure.adapters.mappers.*` and `MinedObjectMapper`).
  - Add null checks:
    - Single-object methods (`toMinedObject`, `toMinedObjectCollection`, `toBiologicalObject`, `toBiologicalObjectCollection`): `if (input == null) { return null; }`.
    - Collection ID mapping: `collection.id != null ? collection.id.toString() : null`.
    - List methods (`toMinedObjects`, `toBiologicalObjects`): `if (list == null) { return List.of(); }`.
    - In `MinedObjectMapper.toMinedObjectCollection`: check `if (minedObject.getId() != null && ObjectId.isValid(minedObject.getId()))` to set `.id(new ObjectId(minedObject.getId()))`, resolving `//TODO chequearID` cleanly.
- **Rationale**: Prevents `NullPointerException` on null inputs or transient domain objects without an assigned MongoDB `ObjectId`, and ensures consistent mapper conventions across the service.

## 3. Lambda Extraction & Stream Cleanup

- **Decision**:
  - Extract multi-line `.map(...)` and `.forEach(...)` blocks into private single-responsibility helper methods (using method references `this::helper` or clean `for` loops where side effects like persistence/mutation occur, since `forEach` with branching/mutation is clearer as an explicit helper call).
  - Remove the duplicate `mongoObject.setBiologicalObjectId(minedObject.getBiologicalObjectId());` in `MinedObjectRepositoryAdapter.saveMinedCollection`.
  - Replace `.collect(Collectors.toList())` with `.toList()` in `PubmedIntegrationAdapter`.
- **Rationale**: Eliminates multi-line lambdas and side-effecting stream builders while keeping `PdbAdapter#getComplexesDeprecated` intact for backward compatibility.

## 4. Explicit `toString()` on Domain Models and DTOs

- **Decision**: Add Lombok `@ToString` to the 11 domain model classes (`BiologicalObject`, `Complex`, `GeneOntology`, `MinedObject`, `HGNCResponse`, `UniprotResponse`, `BiologicalObjectConfig`, `DiscoveryObjectConfig`, `MinedObjectConfig`, `PipelineConfig`, `TranscriptionFactorConfig`) and `infrastructure/dtos/JasparRegion.java`.
- **Rationale**: Satisfies Constitution Principle IV without altering domain logic or serialization.
