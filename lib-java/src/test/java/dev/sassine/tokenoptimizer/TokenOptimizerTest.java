package dev.sassine.tokenoptimizer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

class TokenOptimizerTest {
    
    @Test
    void testOptimizeWithSimpleObject() {
        final Map<String, Object> obj = new HashMap<>();
        obj.put("name", "John");
        obj.put("age", 30);
        obj.put("city", "New York");
        
        final OptimizationResult result = TokenOptimizer.optimize(obj);
        
        assertNotNull(result);
        assertNotNull(result.getOptimalContent());
        assertTrue(result.getOptimalTokenCount() > 0);
        assertTrue(result.getJsonTokenCount() > 0);
        assertTrue(result.getToonTokenCount() > 0);
        
        // The optimal format should have the lowest token count
        assertTrue(result.getOptimalTokenCount() <= result.getJsonTokenCount());
        assertTrue(result.getOptimalTokenCount() <= result.getToonTokenCount());
    }
    
    @Test
    void testOptimizeFromJson() {
        final String jsonString = "{\"name\":\"Mary\",\"age\":25,\"email\":\"mary@example.com\"}";
        
        final OptimizationResult result = TokenOptimizer.optimizeFromJson(jsonString);
        
        assertNotNull(result);
        assertNotNull(result.getOptimalContent());
        assertTrue(result.getOptimalTokenCount() > 0);
    }
    
    @Test
    void testGetOptimizedContent() {
        final Map<String, Object> obj = new HashMap<>();
        obj.put("test", "value");
        
        final String optimized = TokenOptimizer.getOptimizedContent(obj);
        
        assertNotNull(optimized);
        assertFalse(optimized.isEmpty());
    }
    
    @Test
    void testGetOptimizedContentFromJson() {
        final String jsonString = "{\"key\":\"value\"}";
        
        final String optimized = TokenOptimizer.getOptimizedContentFromJson(jsonString);
        
        assertNotNull(optimized);
        assertFalse(optimized.isEmpty());
    }
    
    @Test
    void testOptimizeWithNullObject() {
        assertThrows(IllegalArgumentException.class, () -> {
            TokenOptimizer.optimize(null);
        });
    }
    
    @Test
    void testOptimizeWithNullJson() {
        assertThrows(IllegalArgumentException.class, () -> {
            TokenOptimizer.optimizeFromJson(null);
        });
    }
    
    @Test
    void testOptimizeWithEmptyJson() {
        assertThrows(IllegalArgumentException.class, () -> {
            TokenOptimizer.optimizeFromJson("   ");
        });
    }
    
    @Test
    void testOptimizeWithComplexObject() {
        final Map<String, Object> address = new HashMap<>();
        address.put("street", "Main Street");
        address.put("number", 123);
        address.put("zipCode", "12345-678");
        
        final Map<String, Object> person = new HashMap<>();
        person.put("name", "Carl");
        person.put("age", 35);
        person.put("address", address);
        
        final OptimizationResult result = TokenOptimizer.optimize(person);
        
        assertNotNull(result);
        assertTrue(result.getOptimalTokenCount() > 0);
        assertTrue(result.getJsonTokenCount() > 0);
        assertTrue(result.getToonTokenCount() > 0);
    }
    
    @Test
    void testTokenCounterWithNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            TokenCounter.countTokens(null);
        });
    }
    
    @Test
    void testTokenCounterWithEmptyString() {
        assertEquals(0, TokenCounter.countTokens(""));
    }
    
    @Test
    void testToonConverterWithNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            ToonConverter.toToon(null);
        });
    }
    
    @Test
    void testOptimizeWithPolicyJsonOnly() {
        final Map<String, Object> obj = new HashMap<>();
        obj.put("name", "John");
        obj.put("age", 30);
        
        final OptimizationPolicy policy = OptimizationPolicy.builder()
                .preferFormat(PayloadFormat.JSON_ONLY)
                .build();
        
        final OptimizationResult result = TokenOptimizer.optimize(obj, policy);
        
        assertNotNull(result);
        assertEquals(OptimizationResult.FormatType.JSON, result.getOptimalFormat());
        assertEquals(result.getJsonContent(), result.getOptimalContent());
    }
    
    @Test
    void testOptimizeWithPolicyToonOnly() {
        final Map<String, Object> obj = new HashMap<>();
        obj.put("name", "John");
        obj.put("age", 30);
        
        final OptimizationPolicy policy = OptimizationPolicy.builder()
                .preferFormat(PayloadFormat.TOON_ONLY)
                .build();
        
        final OptimizationResult result = TokenOptimizer.optimize(obj, policy);
        
        assertNotNull(result);
        assertEquals(OptimizationResult.FormatType.TOON, result.getOptimalFormat());
        assertEquals(result.getToonContent(), result.getOptimalContent());
    }
    
    @Test
    void testOptimizeWithPolicyAutoAndHighThreshold() {
        final Map<String, Object> obj = new HashMap<>();
        obj.put("name", "John");
        obj.put("age", 30);
        
        // Set a very high threshold (100%) so it should always keep JSON
        final OptimizationPolicy policy = OptimizationPolicy.builder()
                .preferFormat(PayloadFormat.AUTO)
                .minSavingsPercentForSwitch(100.0)
                .build();
        
        final OptimizationResult result = TokenOptimizer.optimize(obj, policy);
        
        assertNotNull(result);
        // With 100% threshold, it should keep JSON (original format)
        assertEquals(OptimizationResult.FormatType.JSON, result.getOptimalFormat());
    }
    
    @Test
    void testOptimizeWithPolicyAutoAndZeroThreshold() {
        final Map<String, Object> obj = new HashMap<>();
        obj.put("name", "John");
        obj.put("age", 30);
        
        // Set zero threshold, should choose the format with lowest tokens
        final OptimizationPolicy policy = OptimizationPolicy.builder()
                .preferFormat(PayloadFormat.AUTO)
                .minSavingsPercentForSwitch(0.0)
                .build();
        
        final OptimizationResult result = TokenOptimizer.optimize(obj, policy);
        
        assertNotNull(result);
        // Should choose the format with lowest token count
        assertTrue(result.getOptimalTokenCount() <= result.getJsonTokenCount());
        assertTrue(result.getOptimalTokenCount() <= result.getToonTokenCount());
    }
    
    @Test
    void testOptimizationPolicyBuilder() {
        final OptimizationPolicy policy = OptimizationPolicy.builder()
                .preferFormat(PayloadFormat.AUTO)
                .minSavingsPercentForSwitch(5.0)
                .build();
        
        assertEquals(PayloadFormat.AUTO, policy.getPreferFormat());
        assertEquals(5.0, policy.getMinSavingsPercentForSwitch(), 0.001);
    }
    
    @Test
    void testOptimizationPolicyWithNullPreferFormat() {
        assertThrows(IllegalArgumentException.class, () -> {
            OptimizationPolicy.builder()
                    .preferFormat(null)
                    .build();
        });
    }
    
    @Test
    void testOptimizationPolicyWithInvalidSavingsPercent() {
        assertThrows(IllegalArgumentException.class, () -> {
            OptimizationPolicy.builder()
                    .minSavingsPercentForSwitch(-1.0)
                    .build();
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            OptimizationPolicy.builder()
                    .minSavingsPercentForSwitch(101.0)
                    .build();
        });
    }
    
    @Test
    void testGetOptimizedContentWithPolicy() {
        final Map<String, Object> obj = new HashMap<>();
        obj.put("test", "value");
        
        final OptimizationPolicy policy = OptimizationPolicy.builder()
                .preferFormat(PayloadFormat.JSON_ONLY)
                .build();
        
        final String optimized = TokenOptimizer.getOptimizedContent(obj, policy);
        
        assertNotNull(optimized);
        assertFalse(optimized.isEmpty());
    }
    
    @Test
    void testCharacterCountMetrics() {
        final Map<String, Object> obj = new HashMap<>();
        obj.put("name", "John");
        obj.put("age", 30);
        
        final OptimizationResult result = TokenOptimizer.optimize(obj);
        
        assertNotNull(result);
        assertTrue(result.getOptimalCharacterCount() > 0);
        assertTrue(result.getJsonCharacterCount() > 0);
        assertTrue(result.getToonCharacterCount() > 0);
        
        // Character count should match the content length
        assertEquals(result.getJsonContent().length(), result.getJsonCharacterCount());
        assertEquals(result.getToonContent().length(), result.getToonCharacterCount());
        assertEquals(result.getOptimalContent().length(), result.getOptimalCharacterCount());
    }
    
    @Test
    void testByteCountMetrics() {
        final Map<String, Object> obj = new HashMap<>();
        obj.put("name", "John");
        obj.put("age", 30);
        
        final OptimizationResult result = TokenOptimizer.optimize(obj);
        
        assertNotNull(result);
        assertTrue(result.getOptimalByteCount() > 0);
        assertTrue(result.getJsonByteCount() > 0);
        assertTrue(result.getToonByteCount() > 0);
        
        // Byte count should match the UTF-8 byte representation
        assertEquals(result.getJsonContent().getBytes(java.nio.charset.StandardCharsets.UTF_8).length, result.getJsonByteCount());
        assertEquals(result.getToonContent().getBytes(java.nio.charset.StandardCharsets.UTF_8).length, result.getToonByteCount());
        assertEquals(result.getOptimalContent().getBytes(java.nio.charset.StandardCharsets.UTF_8).length, result.getOptimalByteCount());
    }
    
    @Test
    void testCharacterSavings() {
        final Map<String, Object> obj = new HashMap<>();
        obj.put("name", "John");
        obj.put("age", 30);
        
        final OptimizationResult result = TokenOptimizer.optimize(obj);
        
        assertNotNull(result);
        final int savings = result.getCharacterSavings();
        assertTrue(savings >= 0);
        
        final double savingsPercent = result.getCharacterSavingsPercentage();
        assertTrue(savingsPercent >= 0.0);
        assertTrue(savingsPercent <= 100.0);
    }
    
    @Test
    void testByteSavings() {
        final Map<String, Object> obj = new HashMap<>();
        obj.put("name", "John");
        obj.put("age", 30);
        
        final OptimizationResult result = TokenOptimizer.optimize(obj);
        
        assertNotNull(result);
        final int savings = result.getByteSavings();
        assertTrue(savings >= 0);
        
        final double savingsPercent = result.getByteSavingsPercentage();
        assertTrue(savingsPercent >= 0.0);
        assertTrue(savingsPercent <= 100.0);
    }
    
    @Test
    void testAllMetricsConsistency() {
        final Map<String, Object> obj = new HashMap<>();
        obj.put("test", "value");
        obj.put("number", 42);
        
        final OptimizationResult result = TokenOptimizer.optimize(obj);
        
        assertNotNull(result);
        
        // Optimal metrics should match the chosen format
        if (result.getOptimalFormat() == OptimizationResult.FormatType.JSON) {
            assertEquals(result.getOptimalTokenCount(), result.getJsonTokenCount());
            assertEquals(result.getOptimalCharacterCount(), result.getJsonCharacterCount());
            assertEquals(result.getOptimalByteCount(), result.getJsonByteCount());
        } else {
            assertEquals(result.getOptimalTokenCount(), result.getToonTokenCount());
            assertEquals(result.getOptimalCharacterCount(), result.getToonCharacterCount());
            assertEquals(result.getOptimalByteCount(), result.getToonByteCount());
        }
    }
}
