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
}
