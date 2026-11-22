# Arquitectura del Servicio `search-biological-objects`

> **Documento para Agentes de IA y Desarrolladores**  
> **Servicio:** `search-biological-objects` (`artifactId: biological-objects`)  
> **Ecosistema:** BioPatternsG Microservices  
> **Framework:** Quarkus 3.31.4 (Java 21)  
> **Estilo de Arquitectura:** Arquitectura Hexagonal (Ports & Adapters) / Clean Architecture

---

## 1. Propósito del Servicio

`search-biological-objects` es el microservicio encargado de la **búsqueda, resolución, enriquecimiento y consulta de objetos biológicos** dentro del ecosistema **BioPatternsG**. Sus responsabilidades principales incluyen:

1. **Búsqueda y Gestión de Objetos Biológicos (`BiologicalObject`):** Resolución de genes y proteínas mediante estrategias en cadena (base de datos local por usuario/global y fuentes externas como **UniProt** y **HGNC**), búsqueda por sinónimos, consulta por pipeline/nivel, actualización de `meshId` y resolución de jerarquías (padre, hermanos e hijos) mediante ontologías.
2. **Objetos Minados y Descubrimiento (`MinedObject`):** Consulta de objetos expertos y objetos minados en literatura científica asociados a un pipeline y nivel.
3. **Factores de Transcripción (`TranscriptionFactor`):** Consulta e integración de sitios de unión y factores de transcripción desde fuentes externas (**TFBIND**, **JASPAR**).
4. **Complejos Proteicos (`Complex`):** Búsqueda de complejos macromoleculares en **PDBe**.
5. **Alineación Genómica (`BLAT`):** Generación de opciones de búsqueda BLAT integrando **UCSC Genome API** y **BLAT**.
6. **Orquestación de Pasos de Pipeline:** Ejecución de la fase correspondiente del pipeline (`LaunchPipelineUseCase`) coordinando con `config-and-control`, `ontologies` y `pubmed-integration`.

---

## 2. Diagrama Conceptual de Capas (Arquitectura Hexagonal)

```
+---------------------------------------------------------------------------------------+
|                                    INFRASTRUCTURE                                     |
|                                                                                       |
|   [Inbound Adapters (REST Controllers)]                                               |
|   BiologicalObjectController, BlatController, ComplexController,                      |
|   PipelineController, TranscriptionFactorController                                   |
|                                         |                                             |
|                                         v (invoca a través de Ports IN)               |
|   +-------------------------------------+-----------------------------------------+   |
|   |                                     |                                         |   |
|   |         +---------------------------v---------------------------+             |   |
|   |         |                      APPLICATION                      |             |   |
|   |         |                                                       |             |   |
|   |         |   [Application Services & Use Cases]                  |             |   |
|   |         |   - Services: PipelineService, ExpertObjectService,   |             |   |
|   |         |     DiscoveryObjectService, OntologiesService, etc.   |             |   |
|   |         |   - Use Cases (application.usecase):                  |             |   |
|   |         |     Implementan los puertos de entrada (port.in)      |             |   |
|   |         |                               |                       |             |   |
|   |         |                               v (utiliza)             |             |   |
|   |         |   +---------------------------v-------------------+   |             |   |
|   |         |   |                     DOMAIN                    |   |             |   |
|   |         |   |                                               |   |             |   |
|   |         |   |   - Models: BiologicalObject, MinedObject,    |   |             |   |
|   |         |   |     Complex, TranscriptionFactor, Pipeline*   |   |             |   |
|   |         |   |   - Exceptions de Negocio (ApiException)      |   |             |   |
|   |         |   |   - Ports IN (Interfaces de Casos de Uso)     |   |             |   |
|   |         |   |   - Ports OUT (repositories &                 |   |             |   |
|   |         |   |                external_repositories)         |   |             |   |
|   |         |   +---------------------------^-------------------+   |             |   |
|   |         |                               |                       |             |   |
|   |         +-------------------------------+-----------------------+             |   |
|   |                                         ^                                     |   |
|   |                                         | (implementan Ports OUT)             |   |
|   +-----------------------------------------+-------------------------------------+   |
|                                         ^                                             |
|                                         |                                             |
|   [Outbound Adapters (Persistence, Internal & External Services)]                     |
|   - MongoDB Panache: BiologicalObjectRepositoryAdapter, MinedObjectRepositoryAdapter  |
|   - Internal Microservices: ConfigAndControlAdapter, OntologiesAdapter,               |
|                             PubmedIntegrationAdapter                                  |
|   - External APIs: UniprotAdapter, HGNCAdapter, BlatAdapter, PdbAdapter,              |
|                    TFBindAdapter, JasparAdapter                                       |
+---------------------------------------------------------------------------------------+
```

---

## 3. Estructura de Paquetes (`com.biopatternsg.*`)

```text
com.biopatternsg/
├── domain/                      <-- Capa de Dominio (Pura, sin dependencias de frameworks)
│   ├── enums/                   <-- Tipos enumerados (Genome, PipelineSteps, Status, Strand, TranscriptionFactorSource)
│   ├── exceptions/              <-- Excepciones de negocio (ApiException, ApiResponseError, CustomStatus, GeneralError)
│   ├── models/                  <-- Entidades y modelos del dominio (BiologicalObject, MinedObject, Complex, TranscriptionFactor, etc.)
│   │   ├── external_entities/   <-- Modelos de respuesta externa (HGNCResponse, UniprotResponse, BlatSearchOptionsResponse)
│   │   └── pipeline_config/     <-- Modelos de configuración de pipeline
│   └── port/
│       ├── in/                  <-- Puertos de Entrada (Interfaces de Use Cases)
│       └── out/                 <-- Puertos de Salida
│           ├── repositories/           <-- Puertos de persistencia y config-and-control/user
│           └── external_repositories/  <-- Puertos hacia APIs externas y servicios internos (Ontologies, Pubmed, UniProt, HGNC, etc.)
│
├── application/                 <-- Capa de Aplicación
│   ├── services/                <-- Interfaces de servicios de aplicación
│   │   └── impl/                <-- Implementaciones de servicios y estrategias (biological_object_strategy, discovery_object_strategy)
│   └── usecase/                 <-- Casos de uso concretos (*UseCase.java)
│
└── infrastructure/              <-- Capa de Infraestructura (Quarkus, MongoDB, HTTP)
    ├── adapters/
    │   ├── dtos/                <-- DTOs de entrada/salida REST (records)
    │   ├── mappers/             <-- Mappers estáticos puros entre DTOs REST y Dominio
    │   ├── in/
    │   │   ├── AsyncPipelineDispatcher.java <-- Propagación de contexto y ejecución asíncrona de pipelines
    │   │   └── restcontrollers/ <-- Controladores REST (*Controller.java)
    │   └── out/
    │       ├── repositories/           <-- Adaptadores de persistencia y config/user (*Adapter.java)
    │       └── external_repositories/  <-- Adaptadores hacia APIs externas y servicios internos (*Adapter.java)
    ├── boot_functions/          <-- Inicialización de ciclo de vida (AppLifecycleBean)
    ├── clients/                 <-- Clientes HTTP MicroProfile RestClient (external_clients, internal_clients)
    ├── config/                  <-- Filtros e interceptores
    ├── dtos/                    <-- DTOs de clientes HTTP internos/externos e infraestructura
    ├── exceptions/              <-- ExceptionMappers de Jakarta/Quarkus (ApiExceptionMapper)
    ├── external_services/       <-- Wrappers/DTOs de servicios externos (BLAT, HGNC, JASPAR, PDBe, TFBIND, UniProt)
    ├── internal_services/       <-- Wrappers de servicios internos (ConfigAndControl, Ontologies, PubmedIntegration)
    ├── mongo_db/                <-- Colecciones, Mappers y Repositorios PanacheMongoRepository
    └── session/                 <-- Contexto de sesión de usuario (SessionUtil)
```

---

## 4. Reglas Arquitectónicas Estrictas (Enforced by ArchUnit)

Cualquier cambio de código realizado por un agente **DEBE** respetar las reglas validadas en `src/test/java/archtest/ArchitectureLayerTest.java`:

| Regla | Descripción |
|---|---|
| **Aislamiento de Dominio** | `com.biopatternsg.domain..` **NO** puede depender de `application..` ni de `infrastructure..`. |
| **Aislamiento de Aplicación** | `com.biopatternsg.application..` **NO** puede depender de `infrastructure..`. |
| **Encapsulamiento de Aplicación (`layerApplicationTest`)** | `com.biopatternsg.application..` solo puede ser accedida por clases de la misma capa Application (la comunicación desde Infrastructure se realiza exclusivamente mediante interfaces en `domain.port.in`). |
| **Acceso a Casos de Uso (`layerApplicationUsesCasesTest`)** | Las implementaciones en `application.usecase..` no pueden ser accedidas por ninguna capa; se interactúa exclusivamente a través de sus puertos `domain.port.in`. |
| **Nomenclatura UseCase** | Toda clase en `application.usecase..` debe terminar en `UseCase` (ej. `FindBiologicalObjectUseCase`). |
| **Nomenclatura Controller** | Toda clase en `infrastructure.adapters.in.restcontrollers..` debe terminar en `Controller` (ej. `BiologicalObjectController`). |
| **Nomenclatura Adapter** | Toda clase en `infrastructure.adapters.out..` debe terminar en `Adapter` (ej. `BiologicalObjectRepositoryAdapter`, `UniprotAdapter`). |

---

## 5. Casos de Uso Principales

Ubicación: `com.biopatternsg.application.usecase.*` (Catálogo e índice general disponible en [`.agents/use_cases/README.md`](use_cases/README.md)):

| Caso de Uso | Puerto In | Responsabilidad | Documento |
|---|---|---|---|
| `FindBiologicalObjectUseCase` | `FindBiologicalObject` | Busca un objeto biológico por tipo y valor usando cadena de responsabilidad (DB local y APIs externas). | [`FindBiologicalObjectUseCase.md`](use_cases/FindBiologicalObjectUseCase.md) |
| `FindBiologicalObjectsBySynonymUseCase` | `FindBiologicalObjectsBySynonym` | Busca objetos biológicos en base de datos filtrando por coincidencias en `synonyms`. | [`FindBiologicalObjectsBySynonymUseCase.md`](use_cases/FindBiologicalObjectsBySynonymUseCase.md) |
| `FindBiologicalObjectsByPipelineIdAndLevelUseCase` | `FindBiologicalObjectsByPipelineAndLevel` | Obtiene objetos biológicos (nombres y sinónimos) asociados a un `pipelineId` y nivel. | [`FindBiologicalObjectsByPipelineIdAndLevelUseCase.md`](use_cases/FindBiologicalObjectsByPipelineIdAndLevelUseCase.md) |
| `FindBiologicalObjectFatherBrothersAndSonsUseCase` | `FindBiologicalObjectFatherBrothersAndSons` | Obtiene objetos biológicos relacionados jerárquicamente (padre, hermanos e hijos). | [`FindBiologicalObjectFatherBrothersAndSonsUseCase.md`](use_cases/FindBiologicalObjectFatherBrothersAndSonsUseCase.md) |
| `GetExpertObjectsUseCase` | `GetExpertObjectsByPipelineAndLevel` | Obtiene objetos expertos por `pipelineId` y nivel filtrando los que no tienen factor de transcripción. | [`GetExpertObjectsUseCase.md`](use_cases/GetExpertObjectsUseCase.md) |
| `UpdateBiologicalObjectMeshIdUseCase` | `UpdateBiologicalObjectMeshId` | Actualiza el identificador `meshId` de un objeto biológico. | [`UpdateBiologicalObjectMeshIdUseCase.md`](use_cases/UpdateBiologicalObjectMeshIdUseCase.md) |
| `FindComplexUseCase` | `FindComplex` | Consulta complejos proteicos asociados en PDBe. | [`FindComplexUseCase.md`](use_cases/FindComplexUseCase.md) |
| `GetBlatSearchOptionsUseCase` | `FindBlatOption` | Obtiene opciones de búsqueda genómica BLAT para una región promotora. | [`GetBlatSearchOptionsUseCase.md`](use_cases/GetBlatSearchOptionsUseCase.md) |
| `GetTranscriptionFactorsUseCase` | `FindTranscriptionFactor` | Obtiene factores de transcripción desde fuentes configuradas (JASPAR / TFBIND / Config). | [`GetTranscriptionFactorsUseCase.md`](use_cases/GetTranscriptionFactorsUseCase.md) |
| `LaunchPipelineUseCase` | `LaunchPipeline` | Ejecuta el procesamiento del pipeline biológico sobre objetos configurados, minados y de descubrimiento. | [`LaunchPipelineUseCase.md`](use_cases/LaunchPipelineUseCase.md) |

---

## 6. Persistencia y Almacenamiento

- **Motor:** MongoDB.
- **Acceso a Datos:** Quarkus MongoDB con Panache (`PanacheMongoRepository` / `PanacheMongoEntity`).
- **Colecciones Principales (`infrastructure/mongo_db/collections/`):**
    - `BiologicalObjectCollection`: Almacena objetos biológicos (`symbol`, `name`, `hgncId`, `uniprotId`, `synonyms`, `geneOntology`, `tissues`, `geneFamilies`, `transcriptionFactor`, `userId`).
    - `MinedObjectCollection`: Almacena objetos minados extraídos en pipelines.

---

## 7. Integraciones Internas y Externas (MicroProfile REST Clients)

Configuradas en `src/main/resources/application.properties`:

### Servicios Internos del Ecosistema
- **`config-and-control` (`config-and-control-api`):** Consulta configuración de pipelines y reporta actualización de pasos (`ConfigAndControlAdapter`).
- **`ontologies` (`ontologies-api`):** Consulta relaciones ontológicas entre objetos biológicos (`OntologiesAdapter`).
- **`pubmed-integration` (`pubmed-integration-api`):** Dispara búsquedas y minería en literatura médica (`PubmedIntegrationAdapter`).

### APIs Biológicas Externas
- **`uniprot-api` (`https://rest.uniprot.org`):** Metadatos de proteínas (`UniprotAdapter`).
- **`hgnc-api` (`https://rest.genenames.org`):** Nomenclatura estándar de genes humanos (`HGNCAdapter`).
- **`ucsc-genome-api` (`https://api.genome.ucsc.edu`) & `blat-api` (`https://genome.ucsc.edu`):** Secuencias genómicas y alineación BLAT (`BlatAdapter`).
- **`tfbind-api` (`https://tfbind.hgc.jp/cgi-bin/calculate.cgi`):** Predicción de sitios de unión de factores de transcripción (`TFBindAdapter`).
- **`pdbe-api` (`https://www.ebi.ac.uk/pdbe`):** Información estructural y complejos proteicos (`PdbAdapter`).
- **JASPAR (`JasparAdapter`):** Consulta de matrices de factores de transcripción.

---

## 8. Guía de Intervención y Estándares de Código

Para el conjunto completo de reglas mandatorias de arquitectura y estándares de lenguaje Java, consulte los documentos:
- 👉 [**`.agents/architecture-rules.md`**](architecture-rules.md) — Reglas de Arquitectura Hexagonal, límites de capas, casos de uso y servicios.
- 👉 [**`.agents/java-rules.md`**](java-rules.md) — Reglas de Java 21, Clean Code, lambdas, límite de parámetros y mappers estáticos.

### Resumen Rápido para Nuevas Funcionalidades:
1. **Nuevo Caso de Uso:**
    1. Crear la interfaz del puerto de entrada en `domain/port/in/MiOperacion.java`.
    2. Implementar la clase en `application/usecase/MiOperacionUseCase.java` anotada con `@ApplicationScoped`.
    3. Actualizar la tabla de casos de uso en la sección 5 de `.agents/architecture.md` si aplica.
    4. Exponer el endpoint en el controlador respectivo (`infrastructure/adapters/in/restcontrollers/`) inyectando únicamente la interfaz `MiOperacion`.
2. **Nueva Integración / Repositorio:**
    1. Crear la interfaz del puerto de salida en `domain/port/out/repositories/` o `domain/port/out/external_repositories/`.
    2. Implementar el adaptador en `infrastructure/adapters/out/repositories/` o `infrastructure/adapters/out/external_repositories/` terminando en `Adapter`.
3. **Verificación Obligatoria:**
    - Ejecutar `./mvnw clean compile` para asegurar compilación exitosa y actualizar `target/classes`.
    - Ejecutar `./mvnw test -Dtest=ArchitectureLayerTest,DocumentationGovernanceTest` para comprobar que las reglas de arquitectura y gobernanza de documentación no se hayan violado.
