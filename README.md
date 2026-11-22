# search-biological-objects

**`search-biological-objects`** (`org.biopatternsg:biological-objects`) is the core microservice responsible for **searching, resolving, enriching, and querying biological objects** (genes, proteins, transcription factors, and protein complexes) within the **BioPatternsG** microservices ecosystem.

Built with **Quarkus (Java 21)** and **MongoDB**, it follows **Hexagonal Architecture (Ports & Adapters)** and **Clean Architecture** principles.

---

## Key Capabilities

- **Biological Object Lookup & Enrichment:** Resolves genes and proteins via a multi-stage resolution strategy across local MongoDB storage and authoritative external APIs (**UniProt**, **HGNC**), supports global search by synonyms, MeSH ID updates, and hierarchical lineage queries (parent, siblings, and children).
- **Mined & Expert Objects:** Queries mined and expert biological objects by `pipelineId` and discovery `level`.
- **Transcription Factors:** Retrieves regulatory transcription factors from **JASPAR**, **TFBIND**, or unified pipeline configurations.
- **Protein Complexes:** Fetches and scores macromolecular complexes from **PDBe** by UniProt identifier.
- **Genomic Alignment (BLAT):** Generates BLAT search options using **UCSC Genome** and **BLAT** APIs.
- **Asynchronous Pipeline Execution:** Orchestrates biological object discovery steps and reports step metrics/status back to `config-and-control`, while coordinating with `ontologies` and `pubmed-integration`.

---

## Tech Stack

- **Language & Runtime:** Java 21
- **Framework:** Quarkus `3.31.4` (`quarkus-rest-jackson`, `quarkus-arc`, `quarkus-rest-client-jackson`)
- **Database:** MongoDB (`quarkus-mongodb-panache`)
- **Resilience & Async:** SmallRye Fault Tolerance (retries), SmallRye Context Propagation
- **API Docs & Observability:** SmallRye OpenAPI, Swagger UI, Micrometer Prometheus
- **Quality & Architecture Gates:** ArchUnit (`archunit-junit5`), JUnit 5, Mockito, REST Assured, PMD, License Maven Plugin

---

## Documentation & Governance References

All architectural guidelines, coding standards, and functional specifications for developers and AI agents are maintained in this repository:

| Document | Description |
|---|---|
| [Architecture Overview](.agents/architecture.md) | Hexagonal layer diagram, package structure, persistence collections, integrations, and Use Case index. |
| [Architectural Rules & Standards](.agents/architecture-rules.md) | Mandatory Hexagonal boundaries, Use Case / Application Service encapsulation, controller rules, and verification gates. |
| [Java 21 & Clean Code Rules](.agents/java-rules.md) | Method parameter limits, lambda cleanliness, constructor injection, null safety, `@ToString`, and static mapper conventions. |
| [Use Cases Catalog](.agents/use_cases/README.md) | Complete index and detailed specifications (contracts, flows, Mermaid sequence diagrams, and failure scenarios) for every Use Case. |
| [Project Constitution (Spec Kit)](.specify/memory/constitution.md) | Authoritative non-negotiable governance principles and semantic versioning policy for SDD workflows. |

---

## Prerequisites

To build and run `search-biological-objects` locally, you need:

1. **JDK 21** (e.g., Eclipse Temurin 21 / OpenJDK 21).
   > **Note:** Ensure `JAVA_HOME` points to JDK 21 (matching `<maven.compiler.release>21</maven.compiler.release>` and Lombok compatibility).
2. **Maven 3.9+** (or use the bundled Maven Wrapper `./mvnw`).
3. **MongoDB** (local or containerized instance accessible via connection string, authenticating against the `admin` auth source).
4. **Docker** *(optional)*, for building and running the service container.
5. **Network / Ecosystem Dependencies:**
   - **Internal BioPatternsG Services:** `config-and-control`, `ontologies`, and `pubmed-integration` (required for full pipeline orchestration and ontology tree generation).
   - **External Biological APIs:** Outbound HTTPS access to UniProt, HGNC, UCSC Genome / BLAT, TFBIND, JASPAR, and PDBe.

---

## Environment Configuration

Configuration is defined in [`src/main/resources/application.properties`](src/main/resources/application.properties) and driven by the following environment variables:

| Variable | Required | Default | Description |
|---|---|---|---|
| `PORT` | No | `8080` | HTTP server port. |
| `DB_URL` | No | `mongodb://localhost:27017` | MongoDB connection string. |
| `DB_USER` | **Yes** | — | MongoDB username (authenticated against `admin`). |
| `DB_PASSWORD` | **Yes** | — | MongoDB password. |
| `DB_NAME` | **Yes** | — | Target MongoDB database name. |
| `CONFIG-AND-CONTROL` | No | `http://config-and-control:8080` | Base URL for the `config-and-control` microservice. |
| `ONTOLOGIES` | No | `http://ontologies:8080` | Base URL for the `ontologies` microservice. |
| `PUBMED-INTEGRATION` | No | `http://pubmed-integration:8080` | Base URL for the `pubmed-integration` microservice. |

### External Biological APIs (Pre-configured Defaults)

- **UniProt:** `https://rest.uniprot.org`
- **HGNC:** `https://rest.genenames.org`
- **UCSC Genome API:** `https://api.genome.ucsc.edu`
- **UCSC BLAT:** `https://genome.ucsc.edu`
- **TFBIND:** `https://tfbind.hgc.jp/cgi-bin/calculate.cgi`
- **PDBe:** `https://www.ebi.ac.uk/pdbe` (default target organism: `Homo sapiens`, configurable via `pdbe.target-organism`)

---

## Running the Application

### 1. Development Mode (Live Coding)

Export the required MongoDB environment variables and launch Quarkus in dev mode:

```bash
export DB_URL="mongodb://localhost:27017"
export DB_USER="your_db_user"
export DB_PASSWORD="your_db_password"
export DB_NAME="biopatternsg"

./mvnw quarkus:dev
```

Useful local endpoints when running:
- **Swagger UI (OpenAPI):** `http://localhost:8080/q/swagger-ui`
- **OpenAPI Spec:** `http://localhost:8080/q/openapi`
- **Prometheus Metrics:** `http://localhost:8080/q/metrics`
- **Quarkus Dev UI** *(dev mode only)*: `http://localhost:8080/q/dev/`

### 2. Packaging and Running as JVM Application

```bash
./mvnw clean package
java -jar target/quarkus-app/quarkus-run.jar
```

To build an über-jar instead:

```bash
./mvnw clean package -Dquarkus.package.jar.type=uber-jar
java -jar target/*-runner.jar
```

### 3. Building and Running with Docker

First package the Quarkus application so `target/quarkus-app` is generated, then build and run using the root [`Dockerfile`](Dockerfile):

```bash
./mvnw clean package -DskipTests
docker build -t biopatternsg/search-biological-objects:latest .
docker run --rm -p 8080:8080 \
  -e DB_URL="mongodb://host.docker.internal:27017" \
  -e DB_USER="your_db_user" \
  -e DB_PASSWORD="your_db_password" \
  -e DB_NAME="biopatternsg" \
  -e CONFIG-AND-CONTROL="http://config-and-control:8080" \
  -e ONTOLOGIES="http://ontologies:8080" \
  -e PUBMED-INTEGRATION="http://pubmed-integration:8080" \
  biopatternsg/search-biological-objects:latest
```

### 4. Native Executable *(Optional)*

```bash
./mvnw package -Dnative
# Or using a container build without a local GraalVM installation:
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

---

## REST API Overview

Full request/response schemas are available via Swagger UI at `/q/swagger-ui`.

| Method | Path | Controller | Description |
|---|---|---|---|
| `GET` | `/biological-object/search/{type}/{value}` | `BiologicalObjectController` | Find a biological object by type and value. |
| `GET` | `/biological-object/search-by-synonym/{synonym}` | `BiologicalObjectController` | Search biological objects globally by synonym. |
| `PATCH` | `/biological-object/update-mesh-id` | `BiologicalObjectController` | Update the MeSH ID of a biological object. |
| `POST` | `/biological-object/name-and-synonyms` | `BiologicalObjectController` | List biological objects with names and synonyms by pipeline and level. |
| `POST` | `/biological-object/get-expert-objects` | `BiologicalObjectController` | List expert biological objects (without transcription factor) by pipeline and level. |
| `POST` | `/biological-object/father-brothers-and-sons` | `BiologicalObjectController` | Retrieve a biological object with its parent, siblings, and children. |
| `GET` | `/biological-object/complex/{uniprotId}` | `ComplexController` | Retrieve PDBe protein complexes by UniProt ID. |
| `POST` | `/biological-object/launch-pipeline` | `PipelineController` | Trigger asynchronous biological pipeline processing (`202 Accepted`). |
| `POST` | `/transcription-factor/by-promoter-region` | `TranscriptionFactorController` | Find transcription factors via TFBIND. |
| `POST` | `/transcription-factor/by-blat-coordinates` | `TranscriptionFactorController` | Find transcription factors via JASPAR genomic coordinates. |
| `POST` | `/transcription-factor/by-config` | `TranscriptionFactorController` | Find transcription factors based on pipeline configuration. |
| `POST` | `/blat/search-options` | `BlatController` | Retrieve BLAT search options *(deprecated)*. |

---

## Testing & Quality Gates

Run the full test suite:

```bash
./mvnw test
```

Run the mandatory **Hexagonal Architecture (ArchUnit)** and **Use Case Documentation Governance** gates:

```bash
./mvnw clean compile
./mvnw test -Dtest=ArchitectureLayerTest,DocumentationGovernanceTest
```

Run static analysis (PMD) and verify Apache 2.0 license headers:

```bash
./mvnw verify
./mvnw license:check
```

---

## License

Copyright © 2026 biopatternsg (`biopatternsg@gmail.com`).  
Licensed under the **Apache License, Version 2.0**.
