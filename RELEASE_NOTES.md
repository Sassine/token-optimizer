# Release Notes - v1.0.0

## 🎉 First Stable Release

This is the first stable release of Token Optimizer, a Java library to optimize payload format by comparing JSON vs TOON and automatically returning the format that consumes fewer tokens for LLM API calls.

## ✨ Features

### Core Functionality
- **Automatic Format Selection**: Compares JSON and TOON formats and returns the one with fewer tokens
- **Generic Token Estimation**: Fast approximation algorithm that works for any LLM model
- **Tiktoken Integration**: Accurate, model-specific token counting using jtokkit library
- **100% TOON Spec Compliant**: Implements official TOON specification v2.0

### API Methods
- `TokenOptimizer.optimize(Object obj)` - Optimize with generic estimation
- `TokenOptimizer.optimize(Object obj, ModelType modelType)` - Optimize with tiktoken
- `TokenOptimizer.optimizeFromJson(String jsonString)` - Optimize from JSON string
- `TokenOptimizer.getOptimizedContent(Object obj)` - Get only the optimized content

### Documentation
- Complete documentation site with dark/light theme
- API reference
- Code examples
- Benchmarks and comparisons

## 📊 Benchmarks

Based on real-world test results:
- Simple objects: TOON saves ~20% tokens
- Complex nested structures: TOON saves ~25% tokens
- Uniform arrays of objects: TOON saves up to ~89% tokens

## 🔧 Technical Details

- **Java Version**: Compiled with Java 24, compatible with Java 17+
- **Dependencies**:
  - Jackson 2.15.2 (JSON serialization)
  - jtokkit 1.1.0 (tiktoken implementation)
  - JUnit 5.10.0 (testing)

## 📦 Installation

Add to your `pom.xml`:

```xml
<dependency>
    <groupId>dev.sassine</groupId>
    <artifactId>token-optimizer</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 🔗 Links

- **GitHub**: https://github.com/Sassine/token-optimizer
- **Documentation**: https://github.com/Sassine/token-optimizer/tree/main/docs
- **TOON Specification**: https://github.com/toon-format/spec

## 📝 License

MIT License - see LICENSE file for details

## 🙏 Acknowledgments

- TOON Format specification and community
- jtokkit library for tiktoken implementation
- Jackson library for JSON processing

