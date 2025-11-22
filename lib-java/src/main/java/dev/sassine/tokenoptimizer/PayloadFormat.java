package dev.sassine.tokenoptimizer;

/**
 * Enum representing the preferred payload format for optimization.
 */
public enum PayloadFormat {
    /**
     * Automatically choose the format with the lowest token count,
     * respecting the minimum savings percentage threshold.
     */
    AUTO,
    
    /**
     * Always use JSON format, regardless of token count.
     */
    JSON_ONLY,
    
    /**
     * Always use TOON format, regardless of token count.
     */
    TOON_ONLY
}

