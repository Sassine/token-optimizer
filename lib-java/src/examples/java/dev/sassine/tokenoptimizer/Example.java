package dev.sassine.tokenoptimizer;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Example class demonstrating the use of the TokenOptimizer library.
 */
public final class Example {
    
    private Example() {
        throw new AssertionError("Example class should not be instantiated");
    }
    
    public static void main(final String[] args) {
        // Initialize TOON comparison client
        final ToonComparisonClient comparisonClient = new ToonComparisonClient();
        
        System.out.println("=== TOON Comparison Setup ===");
        if (comparisonClient.isServerAvailable()) {
            System.out.println("✅ Servidor TOON oficial está disponível em http://localhost:3000");
        } else {
            System.out.println("⚠️  Servidor TOON oficial NÃO está disponível.");
            System.out.println("   Inicie o servidor com: cd frontend && npm run server");
            System.out.println("   Continuando sem comparação...\n");
        }
        System.out.println();
        
        // Example 1: Optimize a simple object
        final Map<String, Object> person = new HashMap<>();
        person.put("name", "John");
        person.put("age", 30);
        person.put("city", "New York");
        
        final OptimizationResult result = TokenOptimizer.optimize(person);
        
        System.out.println("=== Example 1: Simple Object ===");
        System.out.println("Optimal format: " + result.getOptimalFormat());
        System.out.println("Optimal content: " + result.getOptimalContent());
        System.out.println("JSON tokens: " + result.getJsonTokenCount());
        System.out.println("TOON tokens: " + result.getToonTokenCount());
        System.out.println("Savings: " + result.getTokenSavings() + " tokens (" + 
                          String.format("%.2f", result.getTokenSavingsPercentage()) + "%)");
        
        // Compare with official library
        if (comparisonClient.isServerAvailable()) {
            final ToonComparisonClient.ComparisonResult comparison = comparisonClient.compare(person);
            if (comparison.hasError()) {
                System.out.println("❌ Erro na comparação: " + comparison.getError());
            } else if (comparison.isEqual()) {
                System.out.println("✅ Nossa implementação está IGUAL à biblioteca oficial!");
            } else {
                System.out.println("⚠️  Diferença encontrada entre nossa implementação e a oficial:");
                System.out.println();
                System.out.println("=== Nossa TOON (COMPLETA) ===");
                System.out.println(comparison.getOurToon());
                System.out.println();
                System.out.println("=== Oficial TOON (COMPLETA) ===");
                System.out.println(comparison.getOfficialToon());
            }
        }
        System.out.println();
        
        // Example 2: Optimize from JSON
        final String jsonString = """
                {
                   "blog": {
                     "posts": [
                       {
                         "id": "post-1",
                         "title": "Getting Started with JSON",
                         "author": "Jane Doe",
                         "publishedAt": "2025-01-10",
                         "tags": [
                           "tutorial",
                           "json",
                           "beginner"
                         ],
                         "comments": 15,
                         "likes": 42
                       },
                       {
                         "id": "post-2",
                         "title": "Advanced Data Structures",
                         "author": "John Smith",
                         "publishedAt": "2025-01-12",
                         "tags": [
                           "advanced",
                           "algorithms",
                           "data-structures"
                         ],
                         "comments": 8,
                         "likes": 28
                       }
                     ],
                     "analytics": {
                       "totalViews": 1250,
                       "uniqueVisitors": 890
                     }
                   }
                 }
                """;
        final OptimizationResult result2 = TokenOptimizer.optimizeFromJson(jsonString);
        
        System.out.println("=== Example 2: From JSON ===");
        System.out.println("Optimal format: " + result2.getOptimalFormat());
        System.out.println("Optimal content: " + result2.getOptimalContent());
        System.out.println("Savings: " + result2.getTokenSavings() + " tokens (" +
                String.format("%.2f", result2.getTokenSavingsPercentage()) + "%)");
        
        // Compare with official library
        if (comparisonClient.isServerAvailable()) {
            try {
                final ObjectMapper objectMapper = new ObjectMapper();
                @SuppressWarnings("unchecked")
                final Map<String, Object> jsonObject = (Map<String, Object>) 
                    objectMapper.readValue(jsonString, Map.class);
                final ToonComparisonClient.ComparisonResult comparison = comparisonClient.compare(jsonObject);
                if (comparison.hasError()) {
                    System.out.println("❌ Erro na comparação: " + comparison.getError());
                } else if (comparison.isEqual()) {
                    System.out.println("✅ Nossa implementação está IGUAL à biblioteca oficial!");
                } else {
                    System.out.println("⚠️  Diferença encontrada entre nossa implementação e a oficial:");
                    System.out.println();
                    System.out.println("=== Nossa TOON (COMPLETA) ===");
                    System.out.println(comparison.getOurToon());
                    System.out.println();
                    System.out.println("=== Oficial TOON (COMPLETA) ===");
                    System.out.println(comparison.getOfficialToon());
                }
            } catch (Exception e) {
                System.out.println("❌ Erro ao comparar: " + e.getMessage());
            }
        }
        System.out.println();
        
        // Example 3: Complex object
        final Map<String, Object> address = new HashMap<>();
        address.put("street", "Main Street");
        address.put("number", 123);
        address.put("zipCode", "12345-678");
        
        final Map<String, Object> person2 = new HashMap<>();
        person2.put("name", "Carl");
        person2.put("age", 35);
        person2.put("address", address);
        
        final OptimizationResult result3 = TokenOptimizer.optimize(person2);
        
        System.out.println("=== Example 3: Complex Object ===");
        System.out.println("Optimal format: " + result3.getOptimalFormat());
        System.out.println("Optimal content: " + result3.getOptimalContent());
        System.out.println("JSON tokens: " + result3.getJsonTokenCount());
        System.out.println("TOON tokens: " + result3.getToonTokenCount());
        System.out.println("Savings: " + result3.getTokenSavings() + " tokens (" + 
                          String.format("%.2f", result3.getTokenSavingsPercentage()) + "%)");
        
        // Compare with official library
        if (comparisonClient.isServerAvailable()) {
            final ToonComparisonClient.ComparisonResult comparison = comparisonClient.compare(person2);
            if (comparison.hasError()) {
                System.out.println("❌ Erro na comparação: " + comparison.getError());
            } else if (comparison.isEqual()) {
                System.out.println("✅ Nossa implementação está IGUAL à biblioteca oficial!");
            } else {
                System.out.println("⚠️  Diferença encontrada entre nossa implementação e a oficial:");
                System.out.println();
                System.out.println("=== Nossa TOON (COMPLETA) ===");
                System.out.println(comparison.getOurToon());
                System.out.println();
                System.out.println("=== Oficial TOON (COMPLETA) ===");
                System.out.println(comparison.getOfficialToon());
            }
        }
        System.out.println();
        
        // Example 4: Array of objects with MANY items, VERY long property names, and short values
        // TOON advantage: hierarchical structure avoids repeating long property names
        // Strategy: Many objects (20+) with very long property names but single-char values
        final List<Map<String, Object>> metrics = new ArrayList<>();
        for (int i = 1; i <= 25; i++) {
            final Map<String, Object> metric = new HashMap<>();
            metric.put("applicationPerformanceMonitoringMetricIdentifier", String.valueOf(i));
            metric.put("applicationPerformanceMonitoringMetricValue", i);
            metric.put("applicationPerformanceMonitoringMetricTimestamp", i);
            metric.put("applicationPerformanceMonitoringMetricType", "T");
            metric.put("applicationPerformanceMonitoringMetricStatus", "A");
            metric.put("applicationPerformanceMonitoringMetricCategory", "C");
            metrics.add(metric);
        }
        
        final Map<String, Object> monitoring = new HashMap<>();
        monitoring.put("metrics", metrics);
        
        final OptimizationResult result4 = TokenOptimizer.optimize(monitoring);
        
        System.out.println("=== Example 4: Array of Objects (TOON Advantage) ===");
        System.out.println("Optimal format: " + result4.getOptimalFormat());
        System.out.println("JSON tokens: " + result4.getJsonTokenCount());
        System.out.println("TOON tokens: " + result4.getToonTokenCount());
        System.out.println("Savings: " + result4.getTokenSavings() + " tokens (" + 
                          String.format("%.2f", result4.getTokenSavingsPercentage()) + "%)");
        if (result4.getOptimalFormat() == OptimizationResult.FormatType.TOON) {
            System.out.println("✅ TOON WINS! More efficient for large arrays with long property names.");
            System.out.println("\nTOON Content (first 500 chars):");
            final String toonPreview = result4.getToonContent().length() > 500 
                ? result4.getToonContent().substring(0, 500) + "..." 
                : result4.getToonContent();
            System.out.println(toonPreview);
        } else {
            System.out.println("ℹ️ JSON still wins. TOON format adds overhead with newlines/indentation.");
            System.out.println("   For TOON to win, you need: more objects (50+), longer property names, or shorter values.");
        }
        
        // Compare with official library
        if (comparisonClient.isServerAvailable()) {
            final ToonComparisonClient.ComparisonResult comparison = comparisonClient.compare(monitoring);
            if (comparison.hasError()) {
                System.out.println("❌ Erro na comparação: " + comparison.getError());
            } else if (comparison.isEqual()) {
                System.out.println("✅ Nossa implementação está IGUAL à biblioteca oficial!");
            } else {
                System.out.println("\n⚠️  Diferença encontrada entre nossa implementação e a oficial:");
                System.out.println();
                System.out.println("=== Nossa TOON (COMPLETA) ===");
                System.out.println(comparison.getOurToon());
                System.out.println();
                System.out.println("=== Oficial TOON (COMPLETA) ===");
                System.out.println(comparison.getOfficialToon());
            }
        }
        System.out.println();
    }
}
