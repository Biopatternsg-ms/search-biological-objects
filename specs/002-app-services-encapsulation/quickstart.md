# Quickstart & Verification Guide: Application Services Encapsulation

## Prerequisites

- Java 21 OpenJDK (`/usr/lib/jvm/java-21-openjdk-amd64`)
- Maven Wrapper (`./mvnw`)

Ensure `JAVA_HOME` is set to Java 21 for all commands:
```bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
```

---

## Verification Scenarios

### Scenario 1: Verify Zero Use Case Dependencies on `services.impl`

Verify that no classes in `com.biopatternsg.application.usecase` depend on `com.biopatternsg.application.services.impl`:

```bash
# Must return 0 matches
grep -rn "application\.services\.impl" src/main/java/com/biopatternsg/application/usecase/
```

**Expected Outcome**: Zero matches (exit code 1).

---

### Scenario 2: Verify No Public Fields in Strategy Beans

Verify that `BiologicalObjectSearch` and `DiscoveryObjectContext` declare only `private final` fields:

```bash
# Must return 0 lines declaring public fields
grep -rn "public.*BiologicalObjectBy" src/main/java/com/biopatternsg/application/services/impl/biological_object_strategy/BiologicalObjectSearch.java
grep -rn "public.*discoveryObjectByPdb" src/main/java/com/biopatternsg/application/services/impl/discovery_object_strategy/DiscoveryObjectContext.java
```

**Expected Outcome**: Zero matches.

---

### Scenario 3: Verify Elimination of Assertions in Context Classes

Verify that `BiologicalObjectContext` and `DiscoveryObjectContext` do not contain `assert` statements:

```bash
# Must return 0 matches
grep -rn "assert " src/main/java/com/biopatternsg/application/services/impl/biological_object_strategy/BiologicalObjectContext.java
grep -rn "assert " src/main/java/com/biopatternsg/application/services/impl/discovery_object_strategy/DiscoveryObjectContext.java
```

**Expected Outcome**: Zero matches.

---

### Scenario 4: Verify Method Visibility & Dead Code Removal

Verify that `executeTFBind` is `private`, `PipelineServiceImpl.execute` has `@Override`, and dead overloads are removed:

```bash
# executeTFBind must be private
grep -n "private.*executeTFBind" src/main/java/com/biopatternsg/application/services/impl/TranscriptionFactorsServiceImpl.java

# execute(PipelineConfig, String) must not exist
grep -rn "void execute.*PipelineConfig.*String" src/main/java/com/biopatternsg/application/services/
```

**Expected Outcome**: `executeTFBind` is private; dead overload returns zero matches.

---

### Scenario 5: Full Compilation & Architecture Layer Tests

Execute ArchUnit architecture tests and the complete test suite:

```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./mvnw test -Dtest=ArchitectureLayerTest
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./mvnw test
```

**Expected Outcome**: All ArchUnit tests pass with zero violations, and all unit tests succeed (`BUILD SUCCESS`).
