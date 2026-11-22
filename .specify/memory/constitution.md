<!--
Sync Impact Report
- Version change: template (unversioned) → 1.0.0
- Modified principles:
  - [PRINCIPLE_1_NAME] → I. Hexagonal Layer Isolation (NON-NEGOTIABLE)
  - [PRINCIPLE_2_NAME] → II. Use Cases & Application Services Encapsulation
  - [PRINCIPLE_3_NAME] → III. Zero Business Logic in Delivery & Outbound Adapters
  - [PRINCIPLE_4_NAME] → IV. Java 21 Clean Code & Method Discipline
  - [PRINCIPLE_5_NAME] → V. Stateless Pure Mappers & Naming Conventions
- Added sections:
  - Technology & Integration Constraints (from [SECTION_2_NAME])
  - Development Workflow & Quality Gates (from [SECTION_3_NAME])
- Removed sections: None
- Follow-up TODOs: None
-->

# search-biological-objects Constitution

## Core Principles

### I. Hexagonal Layer Isolation (NON-NEGOTIABLE)

The service MUST maintain strict Hexagonal Architecture (Ports & Adapters) boundaries across `com.biopatternsg.domain.*`, `com.biopatternsg.application.*`, and `com.biopatternsg.infrastructure.*`:
- **Domain (`domain.*`):** MUST remain a pure business core (`enums`, `exceptions`, `models`, `port.in`, `port.out`) and MUST NOT depend on `application..`, `infrastructure..`, or delivery/persistence frameworks (e.g., `jakarta.ws.rs.*`, MongoDB Panache).
- **Application (`application.*`):** MUST depend only on `domain..` and MUST NOT depend on `infrastructure..`.
- **Infrastructure (`infrastructure.*`):** MUST interact with Application exclusively through Inbound Ports (`domain.port.in.*`); direct access to `application..` or `application.usecase..` from outside Application is prohibited.

### II. Use Cases & Application Services Encapsulation

Every business interaction MUST enter through a dedicated Inbound Port (`domain.port.in.*`) implemented by a single-responsibility `*UseCase` in `application.usecase.*`:
- Use Cases MUST act purely as lightweight orchestrators: validate preconditions, throw domain exceptions (`ApiException`), and coordinate Outbound Ports or Application Services.
- Multi-step biological resolution strategies, synonym matching, and workflow coordination MUST reside in Application Services (`application.services.*` interfaces with `@ApplicationScoped` implementations in `application.services.impl.*`).
- Application Services MUST NEVER be injected directly into REST Controllers; controllers MUST depend strictly on Inbound Port interfaces.

### III. Zero Business Logic in Delivery & Outbound Adapters

Delivery and persistence adapters MUST remain thin and free of business rules:
- **REST Controllers (`infrastructure.adapters.in.restcontrollers.*`):** MUST only receive HTTP requests/DTOs, invoke Inbound Ports, and return mapped Response DTOs. Business branching (`if/else`), direct repository queries, and operational business exceptions are prohibited in controllers.
- **Exception Handling:** Business validations belong in Domain/Application layers using `ApiException` and MUST be mapped centrally to HTTP responses by `ApiExceptionMapper` in `infrastructure.exceptions`.
- **Outbound Adapters (`infrastructure.adapters.out.*`):** MUST implement `domain.port.out.*` interfaces and convert MongoDB collections (`infrastructure.mongo_db.collections.*`) or external client DTOs into Domain Models before returning to Application.

### IV. Java 21 Clean Code & Method Discipline

All Java code in `search-biological-objects` MUST adhere to clean, low-complexity standards:
- **Maximum 3 Parameters:** No method in a Use Case, Application Service, or Adapter may accept more than 3 individual parameters; larger inputs MUST be encapsulated in a Java `record`, Domain Model, or Command/Query object.
- **Streams & Private Helpers:** Complex logic MUST be extracted into small single-responsibility private methods. Stream/Optional lambdas MUST be single-expression calls or method references and MUST NOT embed multi-line builders, branching, or try/catch blocks.
- **Injection & Null Safety:** Dependencies MUST use constructor injection (`private final` with `@RequiredArgsConstructor` or explicit `@Inject` constructor). Use `Objects.equals(a, b)` and return empty collections (`List.of()`) instead of `null` for list return types.
- **Explicit `toString()`:** Every class in `domain.models.*`, `infrastructure.adapters.dtos.*`, and `infrastructure.dtos.*` MUST define an explicit `toString()` (via `@ToString` or Java `record`), excluding sensitive fields with `@ToString.Exclude`.

### V. Stateless Pure Mappers & Naming Conventions

Mapping and structural naming conventions MUST be strictly enforced:
- **Static Mappers:** Mapper classes (`infrastructure.mongo_db.mappers.*`, `infrastructure.adapters.mappers.*`) MUST be `public final` utility classes with private constructors, `public static` deterministic pure functions, zero mutable state, zero CDI annotations, and null-safe handling.
- **ArchUnit Naming Conventions:** Classes in `application.usecase..` MUST end with `UseCase`, classes in `infrastructure.adapters.in.restcontrollers..` MUST end with `Controller`, and classes in `infrastructure.adapters.out..` MUST end with `Adapter`.

## Technology & Integration Constraints

- **Stack:** Java 21, Quarkus 3.31.4, Lombok, MongoDB with Panache (`BiologicalObjectCollection`, `MinedObjectCollection`).
- **Internal Microservice Integrations:** Consumed via MicroProfile REST Clients and wrapped by outbound adapters for `config-and-control`, `ontologies`, and `pubmed-integration`.
- **External Biological APIs:** Consumed via MicroProfile REST Clients and outbound adapters for `UniProt`, `HGNC`, `UCSC Genome / BLAT`, `TFBIND`, `JASPAR`, and `PDBe`.

## Development Workflow & Quality Gates

Before completing any feature, refactoring, or bug fix:
1. Run `./mvnw clean compile` to verify compilation and refresh `target/classes`.
2. Run `./mvnw test -Dtest=ArchitectureLayerTest,DocumentationGovernanceTest` to verify all ArchUnit hexagonal layer, naming, and documentation governance rules pass.
3. Update `.agents/architecture.md` whenever new Use Cases, Ports, or integrations are added or modified.

## Governance

This Constitution supersedes ad-hoc development practices in `search-biological-objects`. Detailed implementation guidance is maintained in `.agents/architecture-rules.md`, `.agents/java-rules.md`, and `.agents/architecture.md`.

- **Versioning:** Amendments follow semantic versioning (`MAJOR` for incompatible principle removals or redefinitions, `MINOR` for new principles or materially expanded guidance, `PATCH` for clarifications and non-semantic refinements).
- **Compliance:** All pull requests and AI-assisted implementations MUST verify compliance with these principles and pass `ArchitectureLayerTest`.

**Version**: 1.0.0 | **Ratified**: 2026-11-20 | **Last Amended**: 2026-11-20
