# Getting Started

### Reference Documentation

For further reference, please consider the following sections:

* [Token Optimizer README](../README.md) - Main project documentation
* [Java Library Documentation](../lib-java/README.md) - Library source code details
* [Build Guide](../lib-java/BUILD.md) - How to build the library JAR
* [Frontend Comparison Tool](../frontend/README.md) - Visual comparison tool
* [TOON Specification v2.0](https://github.com/toon-format/spec/blob/main/SPEC.md) - Official TOON format specification

### Requirements

- **Java 11+** (library compiled with Java 24, bytecode compatible with Java 11+)
- **Maven 3.6+** (for building from source)
- **Jackson Databind** (included as dependency)
- **jtokkit** (included as dependency) - For tiktoken integration

### Quick Troubleshooting

| Issue | Solution |
|-------|----------|
| `ClassNotFoundException` | Ensure `token-optimizer` is in your classpath / dependency list |
| Token count differs from expected | Use `ModelType.GPT_4` for accurate tiktoken counting instead of generic estimation |
| TOON output differs from official library | Verify you are using the latest version (`1.1.1`) |
| Build fails with Java version error | Ensure you have Java 11 or higher installed |

### Maven Central

The library is published on Maven Central:

```xml
<dependency>
    <groupId>dev.sassine</groupId>
    <artifactId>token-optimizer</artifactId>
    <version>1.1.1</version>
</dependency>
```

[View on Maven Central](https://central.sonatype.com/artifact/dev.sassine/token-optimizer)

