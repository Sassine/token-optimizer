package dev.sassine.tokenoptimizer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.knuddels.jtokkit.api.ModelType;

/**
 * Main class of the TokenOptimizer library.
 * Compares JSON and TOON representations of an object and returns the one with the lowest token count.
 * Supports both generic token estimation and model-specific token counting using tiktoken.
 */
public final class TokenOptimizer {
    
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(SerializationFeature.INDENT_OUTPUT, false);
    
    // Prevent instantiation
    private TokenOptimizer() {
        throw new AssertionError("Utility class should not be instantiated");
    }
    
    /**
     * Optimizes an object by comparing JSON vs TOON and returning the format with the lowest token count.
     * Uses generic token estimation algorithm.
     * 
     * @param obj The object to be optimized
     * @return OptimizationResult containing the optimal format and comparison information
     * @throws IllegalArgumentException if obj is null
     * @throws RuntimeException if optimization fails
     */
    public static OptimizationResult optimize(final Object obj) {
        return optimize(obj, null);
    }
    
    /**
     * Optimizes an object by comparing JSON vs TOON and returning the format with the lowest token count.
     * Uses the specified tiktoken ModelType for accurate token counting.
     * If modelType is null, uses generic estimation algorithm.
     * 
     * @param obj The object to be optimized
     * @param modelType The tiktoken ModelType to use for counting (null for generic estimation)
     * @return OptimizationResult containing the optimal format and comparison information
     * @throws IllegalArgumentException if obj is null
     * @throws RuntimeException if optimization fails
     */
    public static OptimizationResult optimize(final Object obj, final ModelType modelType) {
        if (obj == null) {
            throw new IllegalArgumentException("Object cannot be null");
        }
        
        try {
            // Convert to JSON using ObjectMapper
            final String jsonContent = OBJECT_MAPPER.writeValueAsString(obj);
            final int jsonTokenCount = TokenCounter.countTokens(jsonContent, modelType);
            
            // Convert to TOON
            final String toonContent = ToonConverter.toToon(obj);
            final int toonTokenCount = TokenCounter.countTokens(toonContent, modelType);
            
            // Compare and determine the optimal format
            final OptimizationResult.FormatType optimalFormat;
            final String optimalContent;
            final int optimalTokenCount;
            
            if (toonTokenCount <= jsonTokenCount) {
                optimalFormat = OptimizationResult.FormatType.TOON;
                optimalContent = toonContent;
                optimalTokenCount = toonTokenCount;
            } else {
                optimalFormat = OptimizationResult.FormatType.JSON;
                optimalContent = jsonContent;
                optimalTokenCount = jsonTokenCount;
            }
            
            return new OptimizationResult(
                optimalFormat,
                optimalContent,
                optimalTokenCount,
                jsonContent,
                jsonTokenCount,
                toonContent,
                toonTokenCount
            );
            
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error optimizing object: " + e.getMessage(), e);
        }
    }
    
    /**
     * Optimizes a JSON string by comparing JSON vs TOON and returning the format with the lowest token count.
     * Uses generic token estimation algorithm.
     * 
     * @param jsonString The JSON string to be optimized
     * @return OptimizationResult containing the optimal format and comparison information
     * @throws IllegalArgumentException if jsonString is null or empty
     * @throws RuntimeException if optimization fails
     */
    public static OptimizationResult optimizeFromJson(final String jsonString) {
        return optimizeFromJson(jsonString, null);
    }
    
    /**
     * Optimizes a JSON string by comparing JSON vs TOON and returning the format with the lowest token count.
     * Uses the specified tiktoken ModelType for accurate token counting.
     * If modelType is null, uses generic estimation algorithm.
     * 
     * @param jsonString The JSON string to be optimized
     * @param modelType The tiktoken ModelType to use for counting (null for generic estimation)
     * @return OptimizationResult containing the optimal format and comparison information
     * @throws IllegalArgumentException if jsonString is null or empty
     * @throws RuntimeException if optimization fails
     */
    public static OptimizationResult optimizeFromJson(final String jsonString, final ModelType modelType) {
        if (jsonString == null) {
            throw new IllegalArgumentException("JSON string cannot be null");
        }
        
        final String trimmed = jsonString.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("JSON string cannot be empty");
        }
        
        try {
            // Parse JSON to object
            final Object obj = OBJECT_MAPPER.readValue(trimmed, Object.class);
            
            // Use the main optimization method with modelType
            return optimize(obj, modelType);
            
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error optimizing JSON: " + e.getMessage(), e);
        }
    }
    
    /**
     * Returns only the optimized content (without comparison information).
     * Uses generic token estimation algorithm.
     * 
     * @param obj The object to be optimized
     * @return String in optimal format (JSON or TOON)
     * @throws IllegalArgumentException if obj is null
     * @throws RuntimeException if optimization fails
     */
    public static String getOptimizedContent(final Object obj) {
        final OptimizationResult result = optimize(obj);
        return result.getOptimalContent();
    }
    
    /**
     * Returns only the optimized content (without comparison information).
     * Uses the specified tiktoken ModelType for accurate token counting.
     * If modelType is null, uses generic estimation algorithm.
     * 
     * @param obj The object to be optimized
     * @param modelType The tiktoken ModelType to use for counting (null for generic estimation)
     * @return String in optimal format (JSON or TOON)
     * @throws IllegalArgumentException if obj is null
     * @throws RuntimeException if optimization fails
     */
    public static String getOptimizedContent(final Object obj, final ModelType modelType) {
        final OptimizationResult result = optimize(obj, modelType);
        return result.getOptimalContent();
    }
    
    /**
     * Returns only the optimized content from a JSON string.
     * Uses generic token estimation algorithm.
     * 
     * @param jsonString The JSON string to be optimized
     * @return String in optimal format (JSON or TOON)
     * @throws IllegalArgumentException if jsonString is null or empty
     * @throws RuntimeException if optimization fails
     */
    public static String getOptimizedContentFromJson(final String jsonString) {
        final OptimizationResult result = optimizeFromJson(jsonString);
        return result.getOptimalContent();
    }
    
    /**
     * Returns only the optimized content from a JSON string.
     * Uses the specified tiktoken ModelType for accurate token counting.
     * If modelType is null, uses generic estimation algorithm.
     * 
     * @param jsonString The JSON string to be optimized
     * @param modelType The tiktoken ModelType to use for counting (null for generic estimation)
     * @return String in optimal format (JSON or TOON)
     * @throws IllegalArgumentException if jsonString is null or empty
     * @throws RuntimeException if optimization fails
     */
    public static String getOptimizedContentFromJson(final String jsonString, final ModelType modelType) {
        final OptimizationResult result = optimizeFromJson(jsonString, modelType);
        return result.getOptimalContent();
    }
}

