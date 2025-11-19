# Building the Library

This document explains how to build the Token Optimizer library.

## Library JAR

The library JAR contains **only** the core library classes:

- `TokenOptimizer` - Main API
- `TokenCounter` - Token counting utilities
- `ToonConverter` - TOON format conversion
- `OptimizationResult` - Result data class

## Building

```bash
# Build library JAR (excludes example classes)
mvn clean package -DskipTests

# The JAR will be in: target/token-optimizer-0.0.1-SNAPSHOT.jar
```

## What's Excluded from the JAR

The following classes are **NOT included** in the library JAR:

- `Example.java` - Basic usage examples
- `ExampleWithTiktoken.java` - Tiktoken usage examples  
- `ToonComparisonClient.java` - Client for frontend comparison

These classes are located in `src/examples/` and are for demonstration/testing purposes only.

## Verifying the JAR Contents

```bash
# List classes in the JAR
jar -tf target/token-optimizer-0.0.1-SNAPSHOT.jar | grep "\.class$"

# Should only show:
# - OptimizationResult.class
# - OptimizationResult$FormatType.class
# - TokenCounter.class
# - TokenOptimizer.class
# - ToonConverter.class
```

## Installing to Local Maven Repository

```bash
mvn clean install -DskipTests
```

This will install the library to your local Maven repository (`~/.m2/repository/dev/sassine/token-optimizer/`).

## Using as a Dependency

Once installed, other projects can use it as a dependency:

```xml
<dependency>
    <groupId>dev.sassine</groupId>
    <artifactId>token-optimizer</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

