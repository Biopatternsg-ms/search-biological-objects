# Quickstart Validation Guide: Use Cases Hygiene & Parameter Encapsulation

**Feature**: `003-use-cases-hygiene-and-parameter-encapsulation` | **Date**: 2026-11-22

## Prerequisites

- Java 21 JDK
- Maven Wrapper (`./mvnw`) in repository root `/home/yacson/Proyectos/biopatternsg-ms/services/search-biological-objects`

## Validation Steps

### 1. Compile & Refresh Classpath
Verify that all refactored signatures (`PipelineStepUpdate`, `MinedObjectCreationContext`, use cases, and adapters) compile cleanly:
```bash
./mvnw clean compile
```
**Expected**: `BUILD SUCCESS` with zero compilation errors.

### 2. Run Architectural Governance Tests
Verify hexagonal layer boundaries and naming conventions using ArchUnit:
```bash
./mvnw test -Dtest=ArchitectureLayerTest
```
**Expected**: All rules in `ArchitectureLayerTest` pass (`0` failures, `0` errors).

### 3. Run Unit Tests for Refactored Use Cases & Services
Execute unit tests covering `LaunchPipelineUseCase`, `FindBiologicalObjectFatherBrothersAndSonsUseCase` (missing node, root node, full lineage), `UpdateBiologicalObjectMeshIdUseCase` (success & `NOT_FOUND` exception), and `PipelineServiceImpl`:
```bash
./mvnw test -Dtest=LaunchPipelineUseCaseTest,FindBiologicalObjectFatherBrothersAndSonsUseCaseTest,UpdateBiologicalObjectMeshIdUseCaseTest
```
**Expected**: All test cases pass.

### 4. Static Hygiene Checks
Verify zero remaining `System.out.println` calls in `LaunchPipelineUseCase` and zero methods with >3 parameters across `application.*`, `domain.port.*`, and `infrastructure.adapters.*`:
```bash
git grep -n "System\.out" src/main/java/com/biopatternsg/application/usecase/LaunchPipelineUseCase.java
```
**Expected**: No matches returned.
