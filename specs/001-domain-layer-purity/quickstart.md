# Quickstart & Verification Guide: Domain Layer Purity

## Prerequisites

- Java 21 OpenJDK (`/usr/lib/jvm/java-21-openjdk-amd64`)
- Maven Wrapper (`./mvnw`)

Ensure `JAVA_HOME` is set to Java 21 for all commands:
```bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
```

---

## Verification Scenarios

### Scenario 1: Zero Web Framework Imports in Domain & Application

Verify that no files under `domain` or `application` import `jakarta.ws.rs`:

```bash
# Must return 0 matches
grep -rn "jakarta.ws.rs" src/main/java/com/biopatternsg/domain/ src/main/java/com/biopatternsg/application/
```

**Expected Outcome**: Zero matches (exit code 1).

---

### Scenario 2: Verification of `CustomStatus.java` Deletion

Verify that the broken `CustomStatus.java` file is deleted:

```bash
test ! -f src/main/java/com/biopatternsg/domain/exceptions/CustomStatus.java && echo "CustomStatus is deleted"
```

**Expected Outcome**: "CustomStatus is deleted".

---

### Scenario 3: Clean Compilation

Verify the entire codebase compiles cleanly with Java 21:

```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./mvnw clean compile
```

**Expected Outcome**: `BUILD SUCCESS`.

---

### Scenario 4: Architectural Rules & Layer Isolation Tests

Execute ArchUnit tests to verify layer isolation, naming conventions, and third-party framework isolation:

```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./mvnw test -Dtest=ArchitectureLayerTest
```

**Expected Outcome**: All ArchUnit tests pass with zero failures.

---

### Scenario 5: Unit Tests Execution

Execute all existing unit tests to verify no regressions:

```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./mvnw test
```

**Expected Outcome**: All unit tests pass with zero failures.
