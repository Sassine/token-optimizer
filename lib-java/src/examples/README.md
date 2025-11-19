# Examples

This directory contains example classes that demonstrate how to use the Token Optimizer library.

## Important Note

⚠️ **These classes are NOT included in the library JAR.** They are for demonstration and testing purposes only.

## Classes

- **Example.java** - Basic usage examples with generic token estimation
- **ExampleWithTiktoken.java** - Examples using tiktoken for model-specific token counting
- **ToonComparisonClient.java** - Client for comparing our implementation with the official TOON library (requires frontend server)

## Running Examples

### Using Scripts

```bash
# Linux/Mac
./run-example.sh

# Windows
run-example.bat
```

### Manual Compilation and Execution

```bash
# 1. Compile the library
mvn clean compile

# 2. Compile examples
javac -cp "target/classes:$(mvn dependency:build-classpath -q -DincludeScope=compile | tail -1)" \
    -d target/classes \
    src/examples/java/dev/sassine/tokenoptimizer/*.java

# 3. Run example
java -cp "target/classes:$(mvn dependency:build-classpath -q -DincludeScope=compile | tail -1)" \
    dev.sassine.tokenoptimizer.Example
```

## Library Classes

The actual library classes (included in the JAR) are:
- `TokenOptimizer` - Main API
- `TokenCounter` - Token counting utilities
- `ToonConverter` - TOON format conversion
- `OptimizationResult` - Result data class

See the main [README.md](../../README.md) for library usage documentation.

