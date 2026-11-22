# Java Language and Clean Code Rules

> **Service:** `search-biological-objects`  
> **Language:** Java 21  
> **Framework:** Quarkus | Lombok

This document establishes the mandatory **Java coding standards, method design, lambda practices, and static mapper conventions** that developers and AI agents must follow when writing or refactoring Java code in `search-biological-objects`.

*(For Hexagonal Architecture boundaries, layer responsibilities, and verification gates, see [`architecture-rules.md`](architecture-rules.md)).*

---

## 1. Method Signatures and Parameter Encapsulation

### 1.1. Maximum 3 Parameters Rule
- No method in a Use Case, Application Service, or Adapter should accept more than **3 individual parameters**.
- When an operation requires more than 3 inputs, encapsulate them into a Java `record`, Domain Model, or Command/Query object (e.g., `PipelineConfig`, `JasparQuery`, `BiologicalObjectRequest`, `FatherBrothersAndSonsRequest`).

### 1.2. Single-Responsibility Private Methods
- Break down complex multi-step methods into small, single-responsibility private methods with names that express clear intent (e.g., `resolveUserId(...)`, `buildQuery(...)`, `mapToDomain(...)`).
- Public methods should read top-to-bottom as a clean sequence of high-level steps without deep nesting (keeping Sonar Cognitive Complexity low).

---

## 2. Clean Code in Streams and Lambda Expressions

### 2.1. Zero Business Logic & Zero Multi-line Builders Inside Lambdas
- Never embed multi-line business calculations, conditional `if/else` branching, try/catch blocks, or multi-line object builders inside stream/optional lambda expressions (`.map(...)`, `.filter(...)`, `.forEach(...)`, `.orElseGet(...)`).
- Lambdas must be single-expression calls or method references (`this::helperMethod`, `MapperClass::toModel`, `BiologicalObjectDTO::fromDomain`) that delegate to descriptive private methods or static mappers.

```java
// ❌ INCORRECT: Multi-line object construction or logic inside lambda
List<BiologicalObjectDTO> dtos = objects.stream()
        .map(obj -> {
            BiologicalObjectDTO dto = new BiologicalObjectDTO(...);
            return dto;
        })
        .toList();

// ✅ CORRECT: Lambda delegates via method reference to a helper or mapper
List<BiologicalObjectDTO> dtos = objects.stream()
        .map(BiologicalObjectDTO::fromDomain)
        .toList();
```

### 2.2. Modern Java 21 Idioms
- Prefer immutable stream terminal collection `.toList()` over `Collectors.toList()` when mutability is not required.
- Use Java `record` types for immutable DTOs (`infrastructure.adapters.dtos.*`, `infrastructure.dtos.*`) and value objects.
- Use pattern matching and enhanced `switch` expressions (`switch (enumVal) { case X -> ... }`) for exhaustive enum handling without fall-through bugs.

---

## 3. Dependency Injection and Class Design

### 3.1. Constructor Injection over Field Injection
- Prefer `private final` fields with constructor injection (`@RequiredArgsConstructor` from Lombok or an explicit `@Inject` constructor) over mutable `@Inject` field injection.
- Never leave unused injected fields in any class.

### 3.2. Immutability and Null Safety
- Use `Objects.equals(a, b)` for null-safe equality checks.
- Guard collection parameters and return empty collections (`Collections.emptyList()` or `List.of()`) rather than `null` for list return types.
- Avoid dead/no-op statements (such as standalone `Response.ok().build();` calls whose return value is ignored).

### 3.3. Explicit `toString()` on Domain Models and DTOs (`@ToString`)
- Every class in `domain.models.*`, `infrastructure.adapters.dtos.*`, and `infrastructure.dtos.*` (especially when referenced inside Java `record` types or Lombok `@Builder` classes) **must** define an explicit `toString()` implementation—typically via Lombok's `@ToString` annotation or a Java `record`—instead of relying on `Object.toString()`.
- This prevents `Use of default toString()` static analysis findings (e.g., GitHub Code Quality / CodeQL) and ensures meaningful log/debug representations.
- Exclude sensitive fields (e.g., passwords, tokens, secrets) with `@ToString.Exclude` whenever applicable.

---

## 4. Static Mappers (`infrastructure.mongo_db.mappers` & `infrastructure.adapters.mappers`)

### 4.1. Stateless Utility Classes and Pure Functions
- **Class Definition:** Every mapper class must be declared `public final class` (or utility class with `private` constructor) to prevent instantiation.
- **Pure Functions:**
    - All mapping methods must be `public static`, deterministic, and free of side effects.
    - Zero mutable state or instance fields.
    - No CDI annotations (`@ApplicationScoped`, `@Singleton`, `@RequestScoped`).
    - No runtime reflection or bytecode-generation libraries.
- **Graceful Null Safety:** Every mapping method must safely handle `null` inputs by returning `null` or an empty collection (`List.of()`) without throwing `NullPointerException`.

### 4.2. Standardized Method Naming Conventions
All static mappers should follow clear, consistent naming conventions:

1. **Request DTO → Domain Model:**
    - `requestToModel(XRequest request)`
2. **Database Collection / Entity → Domain Model:**
    - `toBiologicalObject(BiologicalObjectCollection collection)` / `collectionToModel(XCollection collection)`
    - `toBiologicalObjects(List<BiologicalObjectCollection> list)` / `collectionToModelList(List<XCollection> list)`
3. **Domain Model → Database Collection / Entity:**
    - `toBiologicalObjectCollection(BiologicalObject model)` / `modelToCollection(XModel model)`
    - `modelToCollectionList(List<XModel> list)`
4. **Domain Model → Delivery / Response DTO:**
    - `fromDomain(XModel model)` / `modelToResponse(XModel model)`
    - `modelToResponseList(List<XModel> list)`
5. **External Client / Provider DTO → Domain Model:**
    - `<provider>ToModel(ProviderResponse response)` (e.g., `uniprotToModel`, `hgncToModel`)
    - `<provider>ToModelList(List<ProviderResponse> list)`
