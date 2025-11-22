package dev.sassine.tokenoptimizer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.knuddels.jtokkit.api.ModelType;
import java.nio.charset.StandardCharsets;

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
        return optimize(obj, null, null);
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
        return optimize(obj, modelType, null);
    }
    
    /**
     * Optimizes an object by comparing JSON vs TOON and returning the format based on the provided policy.
     * Uses generic token estimation algorithm.
     * 
     * @param obj The object to be optimized
     * @param policy The optimization policy to apply (null for default: AUTO with 0% threshold)
     * @return OptimizationResult containing the optimal format and comparison information
     * @throws IllegalArgumentException if obj is null
     * @throws RuntimeException if optimization fails
     */
    public static OptimizationResult optimize(final Object obj, final OptimizationPolicy policy) {
        return optimize(obj, null, policy);
    }
    
    /**
     * Optimizes an object by comparing JSON vs TOON and returning the format based on the provided policy.
     * Uses the specified tiktoken ModelType for accurate token counting.
     * If modelType is null, uses generic estimation algorithm.
     * 
     * @param obj The object to be optimized
     * @param modelType The tiktoken ModelType to use for counting (null for generic estimation)
     * @param policy The optimization policy to apply (null for default: AUTO with 0% threshold)
     * @return OptimizationResult containing the optimal format and comparison information
     * @throws IllegalArgumentException if obj is null
     * @throws RuntimeException if optimization fails
     */
    public static OptimizationResult optimize(final Object obj, final ModelType modelType, final OptimizationPolicy policy) {
        if (obj == null) {
            throw new IllegalArgumentException("Object cannot be null");
        }
        
        try {
            // Convert to JSON using ObjectMapper
            final String jsonContent = OBJECT_MAPPER.writeValueAsString(obj);
            final int jsonTokenCount = TokenCounter.countTokens(jsonContent, modelType);
            final int jsonCharacterCount = jsonContent.length();
            final int jsonByteCount = jsonContent.getBytes(StandardCharsets.UTF_8).length;
            
            // Convert to TOON
            final String toonContent = ToonConverter.toToon(obj);
            final int toonTokenCount = TokenCounter.countTokens(toonContent, modelType);
            final int toonCharacterCount = toonContent.length();
            final int toonByteCount = toonContent.getBytes(StandardCharsets.UTF_8).length;
            
            // Apply policy to determine the optimal format
            final OptimizationResult.FormatType optimalFormat;
            final String optimalContent;
            final int optimalTokenCount;
            final int optimalCharacterCount;
            final int optimalByteCount;
            
            if (policy == null) {
                // Default behavior: choose the format with lowest token count
                if (toonTokenCount <= jsonTokenCount) {
                    optimalFormat = OptimizationResult.FormatType.TOON;
                    optimalContent = toonContent;
                    optimalTokenCount = toonTokenCount;
                    optimalCharacterCount = toonCharacterCount;
                    optimalByteCount = toonByteCount;
                } else {
                    optimalFormat = OptimizationResult.FormatType.JSON;
                    optimalContent = jsonContent;
                    optimalTokenCount = jsonTokenCount;
                    optimalCharacterCount = jsonCharacterCount;
                    optimalByteCount = jsonByteCount;
                }
            } else {
                // Apply policy-based decision
                final Decision decision = applyPolicy(policy, jsonTokenCount, toonTokenCount);
                if (decision.useToon) {
                    optimalFormat = OptimizationResult.FormatType.TOON;
                    optimalContent = toonContent;
                    optimalTokenCount = toonTokenCount;
                    optimalCharacterCount = toonCharacterCount;
                    optimalByteCount = toonByteCount;
                } else {
                    optimalFormat = OptimizationResult.FormatType.JSON;
                    optimalContent = jsonContent;
                    optimalTokenCount = jsonTokenCount;
                    optimalCharacterCount = jsonCharacterCount;
                    optimalByteCount = jsonByteCount;
                }
            }
            
            return new OptimizationResult(
                optimalFormat,
                optimalContent,
                optimalTokenCount,
                optimalCharacterCount,
                optimalByteCount,
                jsonContent,
                jsonTokenCount,
                jsonCharacterCount,
                jsonByteCount,
                toonContent,
                toonTokenCount,
                toonCharacterCount,
                toonByteCount
            );
            
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error optimizing object: " + e.getMessage(), e);
        }
    }
    
    /**
     * Applies the optimization policy to determine which format to use.
     * 
     * @param policy The optimization policy
     * @param jsonTokenCount The token count for JSON format
     * @param toonTokenCount The token count for TOON format
     * @return Decision indicating which format to use
     */
    private static Decision applyPolicy(final OptimizationPolicy policy, final int jsonTokenCount, final int toonTokenCount) {
        final PayloadFormat preferFormat = policy.getPreferFormat();
        
        // If format is forced, use it
        if (preferFormat == PayloadFormat.JSON_ONLY) {
            return new Decision(false); // Use JSON
        }
        if (preferFormat == PayloadFormat.TOON_ONLY) {
            return new Decision(true); // Use TOON
        }
        
        // AUTO mode: compare and check savings threshold
        final int originalTokenCount = jsonTokenCount; // Assume JSON is the original format
        final int alternativeTokenCount = toonTokenCount;
        
        // Calculate savings percentage if switching to TOON
        final double savingsPercent;
        if (originalTokenCount > 0) {
            savingsPercent = ((double) (originalTokenCount - alternativeTokenCount) / originalTokenCount) * 100.0;
        } else {
            savingsPercent = 0.0;
        }
        
        // Only switch if savings meet the threshold
        if (alternativeTokenCount < originalTokenCount && savingsPercent >= policy.getMinSavingsPercentForSwitch()) {
            return new Decision(true); // Use TOON
        } else {
            return new Decision(false); // Keep JSON (original)
        }
    }
    
    /**
     * Internal class to represent the decision result.
     */
    private static final class Decision {
        final boolean useToon;
        
        Decision(final boolean useToon) {
            this.useToon = useToon;
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
        return optimizeFromJson(jsonString, null, null);
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
        return optimizeFromJson(jsonString, modelType, null);
    }
    
    /**
     * Optimizes a JSON string by comparing JSON vs TOON and returning the format based on the provided policy.
     * Uses generic token estimation algorithm.
     * 
     * @param jsonString The JSON string to be optimized
     * @param policy The optimization policy to apply (null for default: AUTO with 0% threshold)
     * @return OptimizationResult containing the optimal format and comparison information
     * @throws IllegalArgumentException if jsonString is null or empty
     * @throws RuntimeException if optimization fails
     */
    public static OptimizationResult optimizeFromJson(final String jsonString, final OptimizationPolicy policy) {
        return optimizeFromJson(jsonString, null, policy);
    }
    
    /**
     * Optimizes a JSON string by comparing JSON vs TOON and returning the format based on the provided policy.
     * Uses the specified tiktoken ModelType for accurate token counting.
     * If modelType is null, uses generic estimation algorithm.
     * 
     * @param jsonString The JSON string to be optimized
     * @param modelType The tiktoken ModelType to use for counting (null for generic estimation)
     * @param policy The optimization policy to apply (null for default: AUTO with 0% threshold)
     * @return OptimizationResult containing the optimal format and comparison information
     * @throws IllegalArgumentException if jsonString is null or empty
     * @throws RuntimeException if optimization fails
     */
    public static OptimizationResult optimizeFromJson(final String jsonString, final ModelType modelType, final OptimizationPolicy policy) {
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
            
            // Use the main optimization method with modelType and policy
            return optimize(obj, modelType, policy);
            
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
     * Returns only the optimized content (without comparison information).
     * Uses the provided optimization policy.
     * 
     * @param obj The object to be optimized
     * @param policy The optimization policy to apply
     * @return String in optimal format (JSON or TOON)
     * @throws IllegalArgumentException if obj is null
     * @throws RuntimeException if optimization fails
     */
    public static String getOptimizedContent(final Object obj, final OptimizationPolicy policy) {
        final OptimizationResult result = optimize(obj, policy);
        return result.getOptimalContent();
    }
    
    /**
     * Returns only the optimized content (without comparison information).
     * Uses the specified tiktoken ModelType and optimization policy.
     * 
     * @param obj The object to be optimized
     * @param modelType The tiktoken ModelType to use for counting (null for generic estimation)
     * @param policy The optimization policy to apply
     * @return String in optimal format (JSON or TOON)
     * @throws IllegalArgumentException if obj is null
     * @throws RuntimeException if optimization fails
     */
    public static String getOptimizedContent(final Object obj, final ModelType modelType, final OptimizationPolicy policy) {
        final OptimizationResult result = optimize(obj, modelType, policy);
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
    
    /**
     * Returns only the optimized content from a JSON string.
     * Uses the provided optimization policy.
     * 
     * @param jsonString The JSON string to be optimized
     * @param policy The optimization policy to apply
     * @return String in optimal format (JSON or TOON)
     * @throws IllegalArgumentException if jsonString is null or empty
     * @throws RuntimeException if optimization fails
     */
    public static String getOptimizedContentFromJson(final String jsonString, final OptimizationPolicy policy) {
        final OptimizationResult result = optimizeFromJson(jsonString, policy);
        return result.getOptimalContent();
    }
    
    /**
     * Returns only the optimized content from a JSON string.
     * Uses the specified tiktoken ModelType and optimization policy.
     * 
     * @param jsonString The JSON string to be optimized
     * @param modelType The tiktoken ModelType to use for counting (null for generic estimation)
     * @param policy The optimization policy to apply
     * @return String in optimal format (JSON or TOON)
     * @throws IllegalArgumentException if jsonString is null or empty
     * @throws RuntimeException if optimization fails
     */
    public static String getOptimizedContentFromJson(final String jsonString, final ModelType modelType, final OptimizationPolicy policy) {
        final OptimizationResult result = optimizeFromJson(jsonString, modelType, policy);
        return result.getOptimalContent();
    }
}

