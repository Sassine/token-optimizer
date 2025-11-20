# Token Optimizer - Java Library

This directory contains the Java library source code.

## Structure

```
lib-java/
├── src/
│   ├── main/java/dev/sassine/tokenoptimizer/
│   │   ├── TokenOptimizer.java      # Main API (included in JAR)
│   │   ├── ToonConverter.java       # TOON conversion logic (included in JAR)
│   │   ├── TokenCounter.java        # Token counting (generic + tiktoken) (included in JAR)
│   │   └── OptimizationResult.java  # Result data class (included in JAR)
│   ├── examples/java/dev/sassine/tokenoptimizer/
│   │   ├── Example.java             # Basic usage examples (NOT in JAR)
│   │   ├── ExampleWithTiktoken.java # Tiktoken usage examples (NOT in JAR)
│   │   └── ToonComparisonClient.java # Client for official TOON comparison (NOT in JAR)
│   └── test/java/...                # Unit tests
├── pom.xml                          # Maven configuration
└── run-example.sh / run-example.bat # Scripts to run examples
```

**Note:** Example classes and `ToonComparisonClient` are **NOT included** in the library JAR. They are for demonstration purposes only.

## Features

- **Generic token estimation** - Fast approximation for any model
- **Tiktoken integration** - Accurate, model-specific token counting using jtokkit
- **Automatic format selection** - Chooses JSON or TOON based on token count
- **100% TOON spec compliant** - Validated against official TOON library

## Building

```bash
mvn clean compile
```

## Building the Library

```bash
# Build library JAR (excludes example classes)
mvn clean package -DskipTests

# The JAR will be in target/token-optimizer-0.0.1-SNAPSHOT.jar
# Contains only: TokenOptimizer, TokenCounter, ToonConverter, OptimizationResult
```

## Running Examples

**Note:** Example classes are in `src/examples/` and are NOT included in the library JAR.

```bash
# Using scripts (compiles and runs examples)
./run-example.sh    # Linux/Mac
run-example.bat     # Windows

# Manual execution (after compiling library)
mvn clean compile
javac -cp "target/classes:$(mvn dependency:build-classpath -q -DincludeScope=compile | tail -1)" \
    -d target/classes src/examples/java/dev/sassine/tokenoptimizer/*.java
java -cp "target/classes:$(mvn dependency:build-classpath -q -DincludeScope=compile | tail -1)" \
    dev.sassine.tokenoptimizer.Example
```

## Usage Examples

### Generic Token Estimation

```java
import dev.sassine.tokenoptimizer.TokenOptimizer;
import dev.sassine.tokenoptimizer.OptimizationResult;

// Simple usage - uses generic estimation
OptimizationResult result = TokenOptimizer.optimize(obj);
```

### Model-Specific Token Counting (Tiktoken)

```java
import dev.sassine.tokenoptimizer.TokenOptimizer;
import dev.sassine.tokenoptimizer.OptimizationResult;
import com.knuddels.jtokkit.api.ModelType;

// Use GPT-4 tokenizer for accurate counting
OptimizationResult result = TokenOptimizer.optimize(obj, ModelType.GPT_4);

// Use GPT-3.5 Turbo
OptimizationResult result = TokenOptimizer.optimize(obj, ModelType.GPT_3_5_TURBO);

// Use null for generic estimation
OptimizationResult result = TokenOptimizer.optimize(obj, null);
```

## Running Tests

```bash
mvn test
```

## Requirements

- Java 11+ (compiled with Java 24, bytecode compatible with Java 11+)
- Maven 3.6+

See main [README.md](../README.md) for full documentation.

