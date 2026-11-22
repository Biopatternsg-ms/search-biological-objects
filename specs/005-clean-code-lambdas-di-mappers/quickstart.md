# Quickstart Validation Guide: Clean Code Lambdas, Constructor DI, & Static Mappers

**Feature**: `005-clean-code-lambdas-di-mappers` | **Date**: 2026-11-22

## Prerequisites

- Java 21 JDK (`JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64`)
- Maven Wrapper (`./mvnw`)

## Validation Steps

### 1. Compile & Run Full Test Suite
```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./mvnw clean compile && JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./mvnw test
```
**Expected**: `BUILD SUCCESS` with zero test failures or errors (including `ArchitectureLayerTest`, `PersistenceMappersTest`, `QueryUniprotImplTest`, `UniprotAdapterTest`, `TranscriptionFactorsServiceImplTest`, etc.).

### 2. Static Hygiene Verification
Verify zero `@Inject` field injections in `application.*` and `infrastructure.adapters.*`, and zero `Collectors.toList()` in `PubmedIntegrationAdapter`:
```bash
git grep -n "@Inject" src/main/java/com/biopatternsg/application/ src/main/java/com/biopatternsg/infrastructure/adapters/
git grep -n "Collectors.toList()" src/main/java/com/biopatternsg/infrastructure/adapters/out/external_repositories/PubmedIntegrationAdapter.java
```
**Expected**: `@Inject` appears only on constructors (e.g., `PdbAdapter`), never on fields; zero `Collectors.toList()` matches in `PubmedIntegrationAdapter`.
