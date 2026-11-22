# Architectural Rules and Design Standards

> **Service:** `search-biological-objects`  
> **Package Base:** `com.biopatternsg.*`  
> **Framework:** Quarkus (Java 21) | MongoDB Panache  
> **Architecture:** Hexagonal (Ports & Adapters) / Clean Architecture

This document establishes the mandatory **architectural rules, layer boundaries, and component responsibilities** that developers and AI agents must follow when creating or modifying features in the `search-biological-objects` service.

*(For Java language, clean code, lambdas, and static mapper coding rules, see [`java-rules.md`](java-rules.md)).*

---

## 1. Hexagonal Layer Boundaries & Isolation

1. **Domain Layer (`com.biopatternsg.domain.*`):**
    - Pure business core (`enums`, `exceptions`, `models`, `port/in`, `port/out`).
    - **MUST NOT** depend on `application..`, `infrastructure..`, or external delivery/persistence frameworks (e.g., `jakarta.ws.rs.*`, MongoDB Panache).
2. **Application Layer (`com.biopatternsg.application.*`):**
    - Contains Use Cases (`application.usecase.*`) and Application Services (`application.services.*`, `application.services.impl.*`).
    - **MUST** depend only on `domain..` and **MUST NOT** depend on `infrastructure..`.
    - Can only be accessed from outer layers through Inbound Ports (`domain.port.in.*`).
3. **Infrastructure Layer (`com.biopatternsg.infrastructure.*`):**
    - Contains inbound delivery adapters (`adapters.in.restcontrollers.*`), outbound persistence/external adapters (`adapters.out.repositories.*`, `adapters.out.external_repositories.*`), DTOs (`adapters.dtos.*`, `dtos.*`), HTTP clients (`clients.*`, `external_services.*`, `internal_services.*`), exception mappers (`exceptions.*`), session utilities (`session.*`), and MongoDB collections/mappers/repositories (`mongo_db.*`).

---

## 2. Use Cases (`com.biopatternsg.application.usecase`)

### 2.1. Single Entry-Point Responsibility
- Each Use Case represents a single business interaction and implements its dedicated Inbound Port interface defined under `com.biopatternsg.domain.port.in.*`.
- Use Cases must implement only the contract methods defined by their inbound port interface (typically `execute(...)`).
- Concrete Use Case classes (`*UseCase.java`) must never be directly injected or instantiated across layers; callers must always depend on the Inbound Port interface.

### 2.2. Use Cases as Lightweight Orchestrators
Implementations (`*UseCase.java`) must remain lean and act purely as orchestrators:
- Validate input preconditions and business constraints, throwing domain exceptions (`ApiException`) when required.
- Coordinate execution by invoking outbound repository ports (`domain.port.out.repositories.*`, `domain.port.out.external_repositories.*`) or application services (`application.services.*`).
- Delegate multi-step biological resolution strategies, synonym matching, transcription factor queries, PDB/BLAT lookups, or pipeline step updates to **Application Services** (e.g., `PipelineService`, `DiscoveryObjectService`, `ExpertObjectService`, `UnmatchedSynonymService`, `OntologiesService`).

### 2.3. Mandatory Use Case Documentation (`.agents/use_cases/`)
- **Mandatory English Language:** All Use Case documentation files under `.agents/use_cases/` (including `README.md` and every `<UseCaseName>.md` file) **must be written strictly in English**.
- **New Use Cases:** Whenever a new Use Case is created, a corresponding markdown document must be created in English under `.agents/use_cases/<UseCaseName>.md` describing:
    1. Business problem and purpose.
    2. Input and output contracts (Inbound port, Outbound ports, Application Services).
    3. Step-by-step execution flow.
    4. Sequence diagram using Mermaid format (`sequenceDiagram`).
    5. Exceptions and validation failure scenarios.
- **Modifications:** Whenever an existing Use Case signature, flow, or rule is modified, its documentation in `.agents/use_cases/<UseCaseName>.md` must be updated synchronously in English.
- **Mandatory Index References (`README.md` & `architecture.md`):** Whenever a new Use Case documentation file is added (or an existing one is renamed/repurposed), its reference and link **must** be added/updated in both:
    1. `.agents/use_cases/README.md` (the Use Cases catalog index).
    2. Section 5 of `.agents/architecture.md`.

---

## 3. Application Services (`com.biopatternsg.application.services`)

### 3.1. Encapsulating Complex Business Logic and Strategies
- Multi-step biological object resolution chains, strategy patterns, synonym matching, and pipeline step coordination belong in **Application Services**.
- Examples:
    - `PipelineService`: Coordinates pipeline configuration retrieval and step status updates with `config-and-control`.
    - `ExpertObjectService` & `DiscoveryObjectService`: Filter and construct expert, mined, and discovery biological objects across pipeline levels.
    - `UnmatchedSynonymService`: Tracks and resolves unmatched synonyms during object discovery.
    - `TranscriptionFactorsService`, `BuildTranscriptionFactorsService`, `BuildPdbService`, `BuildBlatSearchOptionsService`, `OntologiesService`: Encapsulate domain-specific enrichment and query orchestration.

### 3.2. Strict Encapsulation and Interface-Implementation Separation
For any new or refactored Application Service:
1. **Interface Contract:** The public contract must be defined as an interface under `com.biopatternsg.application.services.*`.
2. **Hidden Implementation:** The implementation class must reside in `com.biopatternsg.application.services.impl.*` annotated with `@ApplicationScoped`.
3. **Encapsulation:** Concrete implementation classes must not expose internal helper methods publicly. Only the interface contract methods should be visible outside the package.
4. **CDI Injection:** Always inject the **interface** into Use Cases or other Application Services, never the concrete implementation class.

### 3.3. Service Injection and Dependencies
- Application Services may coordinate multiple Outbound Ports (`domain.port.out.repositories.*` and `domain.port.out.external_repositories.*`).
- **Prohibition:** Application Services must **never** be injected directly into REST Controllers; all external traffic must enter via Inbound Ports (`domain.port.in.*`) implemented by Use Cases.

---

## 4. REST Controllers (`com.biopatternsg.infrastructure.adapters.in.restcontrollers`)

### 4.1. Zero Business Logic in Controllers
- Controllers (`BiologicalObjectController`, `BlatController`, `ComplexController`, `PipelineController`, `TranscriptionFactorController`) are strictly delivery adapters:
    1. Receive HTTP request / path params / payload DTO (`com.biopatternsg.infrastructure.adapters.dtos.*` or shared request DTOs).
    2. Convert request DTOs to Domain Models via static mappers in `com.biopatternsg.infrastructure.adapters.mappers.*`.
    3. Invoke the Use Case through its Inbound Port interface (`domain.port.in.*`) or `AsyncPipelineDispatcher` for async execution.
    4. Convert the Domain Model result to a Response DTO via static mappers (e.g., `BiologicalObjectDtoMapper::toDetailDto`, `ComplexDtoMapper::toDtoList`) and return it.
- **No** business branching (`if/else`), existence queries, or domain calculations are allowed inside controller methods.

### 4.2. Business Exceptions in Domain/Application Layers
- Existence checks and business rule validations belong in Use Cases or Application Services.
- Controllers must never manually query repositories or throw operational business exceptions.
- Domain exceptions (`com.biopatternsg.domain.exceptions.ApiException`) are mapped centrally to HTTP status codes by `ApiExceptionMapper` in `infrastructure.exceptions`.

---

## 5. Persistence & Outbound Adapters (`com.biopatternsg.infrastructure.adapters.out.*`)

### 5.1. Repository Adapters (`infrastructure.adapters.out.repositories.*`)
- Implement repository interfaces defined in `com.biopatternsg.domain.port.out.repositories.*` (`BiologicalObjectRepository`, `MinedObjectRepository`, `ConfigAndControlRepository`, `UserRepository`).
- Separate concerns: MongoDB collections (`infrastructure.mongo_db.collections.*`) must be converted to Domain Models (`domain.models.*`) via mappers (`infrastructure.mongo_db.mappers.*`) before returning to the application layer.

### 5.2. External & Internal Service Adapters (`infrastructure.adapters.out.external_repositories.*`)
- External biological APIs (`UniProt`, `HGNC`, `UCSC Genome / BLAT`, `TFBIND`, `JASPAR`, `PDBe`) and internal microservices (`config-and-control`, `ontologies`, `pubmed-integration`) are consumed via MicroProfile REST Clients (`infrastructure.clients.*`) and service wrappers (`infrastructure.external_services.*`, `infrastructure.internal_services.*`), and exposed to the domain/application layers exclusively through `*Adapter` classes implementing `domain.port.out.*` interfaces.

---

## 6. Architectural Naming & Verification Gates

### 6.1. Mandatory Naming Conventions (Enforced by ArchUnit)
- Classes in `application.usecase..` must end with `UseCase` (e.g., `FindBiologicalObjectUseCase`).
- Classes in `infrastructure.adapters.in.restcontrollers..` must end with `Controller` (e.g., `BiologicalObjectController`).
- Classes in `infrastructure.adapters.out..` must end with `Adapter` (e.g., `BiologicalObjectRepositoryAdapter`, `UniprotAdapter`).

### 6.2. Mandatory Verification Commands
Before completing any task, verify compilation and architectural compliance:
```bash
# 1. Compile classes (required before ArchUnit imports target/classes)
./mvnw clean compile

# 2. Run Hexagonal Architecture and Documentation Governance validation tests
./mvnw test -Dtest=ArchitectureLayerTest,DocumentationGovernanceTest
```
