package dev.sassine.tokenoptimizer;

/**
 * Policy configuration for token optimization decisions.
 * Controls how the optimizer chooses between JSON and TOON formats.
 */
public final class OptimizationPolicy {
    
    private final PayloadFormat preferFormat;
    private final double minSavingsPercentForSwitch;
    
    /**
     * Creates a new OptimizationPolicy.
     * 
     * @param preferFormat The preferred format strategy
     * @param minSavingsPercentForSwitch Minimum savings percentage required to switch formats (0.0 to 100.0)
     */
    private OptimizationPolicy(final PayloadFormat preferFormat, final double minSavingsPercentForSwitch) {
        if (preferFormat == null) {
            throw new IllegalArgumentException("preferFormat cannot be null");
        }
        if (minSavingsPercentForSwitch < 0.0 || minSavingsPercentForSwitch > 100.0) {
            throw new IllegalArgumentException("minSavingsPercentForSwitch must be between 0.0 and 100.0");
        }
        this.preferFormat = preferFormat;
        this.minSavingsPercentForSwitch = minSavingsPercentForSwitch;
    }
    
    /**
     * Returns the preferred format strategy.
     * 
     * @return The preferred format
     */
    public PayloadFormat getPreferFormat() {
        return preferFormat;
    }
    
    /**
     * Returns the minimum savings percentage required to switch formats.
     * 
     * @return The minimum savings percentage (0.0 to 100.0)
     */
    public double getMinSavingsPercentForSwitch() {
        return minSavingsPercentForSwitch;
    }
    
    /**
     * Creates a new builder for OptimizationPolicy.
     * 
     * @return A new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Builder for creating OptimizationPolicy instances.
     */
    public static final class Builder {
        private PayloadFormat preferFormat = PayloadFormat.AUTO;
        private double minSavingsPercentForSwitch = 0.0;
        
        private Builder() {
        }
        
        /**
         * Sets the preferred format strategy.
         * 
         * @param preferFormat The preferred format (AUTO, JSON_ONLY, or TOON_ONLY)
         * @return This builder instance
         */
        public Builder preferFormat(final PayloadFormat preferFormat) {
            this.preferFormat = preferFormat;
            return this;
        }
        
        /**
         * Sets the minimum savings percentage required to switch formats.
         * Only applies when preferFormat is AUTO.
         * 
         * @param minSavingsPercentForSwitch Minimum savings percentage (0.0 to 100.0)
         * @return This builder instance
         */
        public Builder minSavingsPercentForSwitch(final double minSavingsPercentForSwitch) {
            this.minSavingsPercentForSwitch = minSavingsPercentForSwitch;
            return this;
        }
        
        /**
         * Builds the OptimizationPolicy instance.
         * 
         * @return A new OptimizationPolicy instance
         * @throws IllegalArgumentException if any parameter is invalid
         */
        public OptimizationPolicy build() {
            return new OptimizationPolicy(preferFormat, minSavingsPercentForSwitch);
        }
    }
}

