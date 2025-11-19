package dev.sassine.tokenoptimizer;

/**
 * Represents the result of token optimization.
 * Contains information about both JSON and TOON formats, and which one is optimal.
 */
public final class OptimizationResult {
    
    private final FormatType optimalFormat;
    private final String optimalContent;
    private final int optimalTokenCount;
    private final String jsonContent;
    private final int jsonTokenCount;
    private final String toonContent;
    private final int toonTokenCount;
    
    /**
     * Creates a new OptimizationResult.
     * 
     * @param optimalFormat The optimal format (JSON or TOON)
     * @param optimalContent The content in optimal format
     * @param optimalTokenCount The token count of optimal format
     * @param jsonContent The JSON content
     * @param jsonTokenCount The JSON token count
     * @param toonContent The TOON content
     * @param toonTokenCount The TOON token count
     */
    public OptimizationResult(
            final FormatType optimalFormat,
            final String optimalContent,
            final int optimalTokenCount,
            final String jsonContent,
            final int jsonTokenCount,
            final String toonContent,
            final int toonTokenCount) {
        this.optimalFormat = optimalFormat;
        this.optimalContent = optimalContent;
        this.optimalTokenCount = optimalTokenCount;
        this.jsonContent = jsonContent;
        this.jsonTokenCount = jsonTokenCount;
        this.toonContent = toonContent;
        this.toonTokenCount = toonTokenCount;
    }
    
    /**
     * Returns the optimal format (JSON or TOON).
     * 
     * @return The optimal format type
     */
    public FormatType getOptimalFormat() {
        return optimalFormat;
    }
    
    /**
     * Returns the content in optimal format.
     * 
     * @return The optimal content string
     */
    public String getOptimalContent() {
        return optimalContent;
    }
    
    /**
     * Returns the token count of the optimal format.
     * 
     * @return The optimal token count
     */
    public int getOptimalTokenCount() {
        return optimalTokenCount;
    }
    
    /**
     * Returns the content in JSON format.
     * 
     * @return The JSON content string
     */
    public String getJsonContent() {
        return jsonContent;
    }
    
    /**
     * Returns the token count of JSON format.
     * 
     * @return The JSON token count
     */
    public int getJsonTokenCount() {
        return jsonTokenCount;
    }
    
    /**
     * Returns the content in TOON format.
     * 
     * @return The TOON content string
     */
    public String getToonContent() {
        return toonContent;
    }
    
    /**
     * Returns the token count of TOON format.
     * 
     * @return The TOON token count
     */
    public int getToonTokenCount() {
        return toonTokenCount;
    }
    
    /**
     * Returns the token savings compared to the non-optimal format.
     * 
     * @return The number of tokens saved
     */
    public int getTokenSavings() {
        final int maxTokens = Math.max(jsonTokenCount, toonTokenCount);
        return maxTokens - optimalTokenCount;
    }
    
    /**
     * Returns the percentage of token savings.
     * 
     * @return The percentage of tokens saved (0.0 to 100.0)
     */
    public double getTokenSavingsPercentage() {
        final int maxTokens = Math.max(jsonTokenCount, toonTokenCount);
        if (maxTokens == 0) {
            return 0.0;
        }
        return ((double) getTokenSavings() / maxTokens) * 100.0;
    }
    
    @Override
    public String toString() {
        return String.format(
            "OptimizationResult{optimalFormat=%s, optimalTokenCount=%d, jsonTokenCount=%d, toonTokenCount=%d, savings=%d tokens (%.2f%%)}",
            optimalFormat, optimalTokenCount, jsonTokenCount, toonTokenCount, 
            getTokenSavings(), getTokenSavingsPercentage()
        );
    }
    
    /**
     * Enum representing the supported format types.
     */
    public enum FormatType {
        JSON,
        TOON
    }
}
