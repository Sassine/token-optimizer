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
        return optimize(obj, null, null, OptimizationCriteria.TOKENS);
    }
    
    /**
     * Optimizes an object by comparing JSON vs TOON based on byte count.
     * Returns the format with the lowest byte count (UTF-8).
     * 
     * @param obj The object to be optimized
     * @return OptimizationResult containing the optimal format and comparison information
     * @throws IllegalArgumentException if obj is null
     * @throws RuntimeException if optimization fails
     */
    public static OptimizationResult optimizeByBytes(final Object obj) {
        return optimize(obj, null, null, OptimizationCriteria.BYTES);
    }
    
    /**
     * Optimizes an object by comparing JSON vs TOON based on character count.
     * Returns the format with the lowest character count.
     * 
     * @param obj The object to be optimized
     * @return OptimizationResult containing the optimal format and comparison information
     * @throws IllegalArgumentException if obj is null
     * @throws RuntimeException if optimization fails
     */
    public static OptimizationResult optimizeByCharacters(final Object obj) {
        return optimize(obj, null, null, OptimizationCriteria.CHARACTERS);
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
        return optimize(obj, modelType, null, OptimizationCriteria.TOKENS);
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
        return optimize(obj, null, policy, OptimizationCriteria.TOKENS);
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
    public static OptimizationResult optimize(final Object obj, final ModelType modelType, final OptimizationPolicy policy, final OptimizationCriteria criteria) {
        if (obj == null) {
            throw new IllegalArgumentException("Object cannot be null");
        }
        if (criteria == null) {
            throw new IllegalArgumentException("OptimizationCriteria cannot be null");
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
            
            // Determine optimal format based on criteria
            final OptimizationResult.FormatType optimalFormat;
            final String optimalContent;
            final int optimalTokenCount;
            final int optimalCharacterCount;
            final int optimalByteCount;
            
            if (policy == null) {
                // Default behavior: choose based on criteria
                final boolean useToon = determineOptimalFormat(criteria, 
                    jsonTokenCount, jsonCharacterCount, jsonByteCount,
                    toonTokenCount, toonCharacterCount, toonByteCount);
                
                if (useToon) {
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
                // Apply policy-based decision (policy uses tokens, but we still compare by criteria)
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
     * Determines the optimal format based on the specified criteria.
     * 
     * @param criteria The optimization criteria
     * @param jsonTokenCount JSON token count
     * @param jsonCharacterCount JSON character count
     * @param jsonByteCount JSON byte count
     * @param toonTokenCount TOON token count
     * @param toonCharacterCount TOON character count
     * @param toonByteCount TOON byte count
     * @return true if TOON is optimal, false if JSON is optimal
     */
    private static boolean determineOptimalFormat(
            final OptimizationCriteria criteria,
            final int jsonTokenCount, final int jsonCharacterCount, final int jsonByteCount,
            final int toonTokenCount, final int toonCharacterCount, final int toonByteCount) {
        
        switch (criteria) {
            case TOKENS:
                return toonTokenCount <= jsonTokenCount;
            case BYTES:
                return toonByteCount <= jsonByteCount;
            case CHARACTERS:
                return toonCharacterCount <= jsonCharacterCount;
            default:
                return toonTokenCount <= jsonTokenCount; // Default to tokens
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
        return optimizeFromJson(jsonString, null, null, OptimizationCriteria.TOKENS);
    }
    
    /**
     * Optimizes a JSON string by comparing JSON vs TOON based on byte count.
     * Returns the format with the lowest byte count (UTF-8).
     * 
     * @param jsonString The JSON string to be optimized
     * @return OptimizationResult containing the optimal format and comparison information
     * @throws IllegalArgumentException if jsonString is null or empty
     * @throws RuntimeException if optimization fails
     */
    public static OptimizationResult optimizeFromJsonByBytes(final String jsonString) {
        return optimizeFromJson(jsonString, null, null, OptimizationCriteria.BYTES);
    }
    
    /**
     * Optimizes a JSON string by comparing JSON vs TOON based on character count.
     * Returns the format with the lowest character count.
     * 
     * @param jsonString The JSON string to be optimized
     * @return OptimizationResult containing the optimal format and comparison information
     * @throws IllegalArgumentException if jsonString is null or empty
     * @throws RuntimeException if optimization fails
     */
    public static OptimizationResult optimizeFromJsonByCharacters(final String jsonString) {
        return optimizeFromJson(jsonString, null, null, OptimizationCriteria.CHARACTERS);
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
        return optimizeFromJson(jsonString, modelType, null, OptimizationCriteria.TOKENS);
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
        return optimizeFromJson(jsonString, null, policy, OptimizationCriteria.TOKENS);
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
        return optimizeFromJson(jsonString, modelType, policy, OptimizationCriteria.TOKENS);
    }
    
    /**
     * Optimizes a JSON string by comparing JSON vs TOON based on the specified criteria.
     * Uses the specified tiktoken ModelType for accurate token counting (only used when criteria is TOKENS).
     * If modelType is null, uses generic estimation algorithm.
     * 
     * @param jsonString The JSON string to be optimized
     * @param modelType The tiktoken ModelType to use for counting (null for generic estimation, only used for TOKENS criteria)
     * @param policy The optimization policy to apply (null for default: AUTO with 0% threshold)
     * @param criteria The optimization criteria (TOKENS, BYTES, or CHARACTERS)
     * @return OptimizationResult containing the optimal format and comparison information
     * @throws IllegalArgumentException if jsonString is null or empty, or criteria is null
     * @throws RuntimeException if optimization fails
     */
    public static OptimizationResult optimizeFromJson(final String jsonString, final ModelType modelType, final OptimizationPolicy policy, final OptimizationCriteria criteria) {
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
            
            // Use the main optimization method with modelType, policy, and criteria
            return optimize(obj, modelType, policy, criteria);
            
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
        final OptimizationResult result = optimize(obj, modelType, policy, OptimizationCriteria.TOKENS);
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
    
    /**
     * Converts a TOON string to a JSON string.
     * 
     * @param toonString The TOON string to be converted
     * @return String in JSON format
     * @throws IllegalArgumentException if toonString is null or empty
     * @throws RuntimeException if conversion fails
     */
    public static String fromToonToJson(final String toonString) {
        return ToonConverter.toJson(toonString);
    }
    
    /**
     * Converts a TOON string to an Object (Map/List structure).
     * 
     * @param toonString The TOON string to be converted
     * @return Object (typically Map or List) representing the TOON data
     * @throws IllegalArgumentException if toonString is null or empty
     * @throws RuntimeException if conversion fails
     */
    public static Object fromToon(final String toonString) {
        return ToonConverter.fromToon(toonString);
    }
    
    /**
     * Converts a TOON string to a specific class type.
     * 
     * @param <T> The target type
     * @param toonString The TOON string to be converted
     * @param clazz The target class
     * @return Object of the specified type
     * @throws IllegalArgumentException if toonString is null or empty, or clazz is null
     * @throws RuntimeException if conversion fails
     */
    public static <T> T fromToon(final String toonString, final Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }
        
        try {
            final Object obj = ToonConverter.fromToon(toonString);
            return OBJECT_MAPPER.convertValue(obj, clazz);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error converting TOON to " + clazz.getSimpleName() + ": " + e.getMessage(), e);
        }
    }
}

