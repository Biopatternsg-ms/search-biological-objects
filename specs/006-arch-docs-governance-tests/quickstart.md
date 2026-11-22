# Quickstart Validation Guide: Architecture & Documentation Governance Tests

**Feature**: `006-arch-docs-governance-tests` | **Date**: 2026-11-22

## Prerequisites

- Java 21 JDK (`JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64`)
- Maven Wrapper (`./mvnw`)

## Validation Steps

### 1. Run Governance Tests
```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./mvnw clean test -Dtest=ArchitectureLayerTest,DocumentationGovernanceTest
```
**Expected**: `BUILD SUCCESS` with 11 ArchUnit rules passing in `ArchitectureLayerTest` and all documentation governance checks passing in `DocumentationGovernanceTest`.

### 2. Run Full Test Suite
```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./mvnw test
```
**Expected**: All tests in the repository pass with 0 failures and 0 errors.
