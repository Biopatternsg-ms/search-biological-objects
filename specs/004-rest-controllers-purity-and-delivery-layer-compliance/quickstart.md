# Quickstart Validation Guide: REST Controllers Purity & Delivery Layer Compliance

**Feature**: `004-rest-controllers-purity-and-delivery-layer-compliance` | **Date**: 2026-11-22

## Prerequisites

- Java 21 JDK (`JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64`)
- Maven Wrapper (`./mvnw`)

## Validation Steps

### 1. Compile & Verify All Refactored Controllers, DTOs, and Mappers
```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./mvnw clean compile
```
**Expected**: `BUILD SUCCESS` with zero errors.

### 2. Run Architectural Governance Tests
```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./mvnw test -Dtest=ArchitectureLayerTest
```
**Expected**: All rules in `ArchitectureLayerTest` pass (`0` failures, `0` errors).

### 3. Run Delivery Layer & Mapper Unit Tests
```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./mvnw test -Dtest=BiologicalObjectControllerTest,PipelineControllerTest,DeliveryMappersTest
```
**Expected**: All test cases pass.

### 4. Static Delivery Hygiene Verification
Verify zero `Arc.container()`, zero `try/catch` in `BiologicalObjectController#updateMeshId`, and zero inline `new JasparQuery(` in `TranscriptionFactorController`:
```bash
git grep -n "Arc\.container\|new JasparQuery" src/main/java/com/biopatternsg/infrastructure/adapters/in/restcontrollers/
```
**Expected**: Zero matches returned.
