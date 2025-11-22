package dev.sassine.tokenoptimizer;

/**
 * Enum representing the criteria used for optimization comparison.
 * Determines which metric is used to select the optimal format.
 */
public enum OptimizationCriteria {
    /**
     * Compare formats based on token count (default).
     * Best for LLM API usage where token costs matter.
     */
    TOKENS,
    
    /**
     * Compare formats based on byte count (UTF-8).
     * Best for data persistence and storage optimization.
     */
    BYTES,
    
    /**
     * Compare formats based on character count.
     * Best for size analysis and text processing.
     */
    CHARACTERS
}

