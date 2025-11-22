package dev.sassine.tokenoptimizer;

/**
 * Represents the result of token optimization.
 * Contains information about both JSON and TOON formats, and which one is optimal.
 * Includes metrics for tokens, characters, and bytes to evaluate optimization for both
 * LLM usage and data persistence scenarios.
 */
public final class OptimizationResult {
    
    private final FormatType optimalFormat;
    private final String optimalContent;
    private final int optimalTokenCount;
    private final int optimalCharacterCount;
    private final int optimalByteCount;
    private final String jsonContent;
    private final int jsonTokenCount;
    private final int jsonCharacterCount;
    private final int jsonByteCount;
    private final String toonContent;
    private final int toonTokenCount;
    private final int toonCharacterCount;
    private final int toonByteCount;
    
    /**
     * Creates a new OptimizationResult.
     * 
     * @param optimalFormat The optimal format (JSON or TOON)
     * @param optimalContent The content in optimal format
     * @param optimalTokenCount The token count of optimal format
     * @param optimalCharacterCount The character count of optimal format
     * @param optimalByteCount The byte count of optimal format
     * @param jsonContent The JSON content
     * @param jsonTokenCount The JSON token count
     * @param jsonCharacterCount The JSON character count
     * @param jsonByteCount The JSON byte count
     * @param toonContent The TOON content
     * @param toonTokenCount The TOON token count
     * @param toonCharacterCount The TOON character count
     * @param toonByteCount The TOON byte count
     */
    public OptimizationResult(
            final FormatType optimalFormat,
            final String optimalContent,
            final int optimalTokenCount,
            final int optimalCharacterCount,
            final int optimalByteCount,
            final String jsonContent,
            final int jsonTokenCount,
            final int jsonCharacterCount,
            final int jsonByteCount,
            final String toonContent,
            final int toonTokenCount,
            final int toonCharacterCount,
            final int toonByteCount) {
        this.optimalFormat = optimalFormat;
        this.optimalContent = optimalContent;
        this.optimalTokenCount = optimalTokenCount;
        this.optimalCharacterCount = optimalCharacterCount;
        this.optimalByteCount = optimalByteCount;
        this.jsonContent = jsonContent;
        this.jsonTokenCount = jsonTokenCount;
        this.jsonCharacterCount = jsonCharacterCount;
        this.jsonByteCount = jsonByteCount;
        this.toonContent = toonContent;
        this.toonTokenCount = toonTokenCount;
        this.toonCharacterCount = toonCharacterCount;
        this.toonByteCount = toonByteCount;
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
     * Returns the character count of the optimal format.
     * 
     * @return The optimal character count
     */
    public int getOptimalCharacterCount() {
        return optimalCharacterCount;
    }
    
    /**
     * Returns the byte count of the optimal format.
     * 
     * @return The optimal byte count
     */
    public int getOptimalByteCount() {
        return optimalByteCount;
    }
    
    /**
     * Returns the character count of JSON format.
     * 
     * @return The JSON character count
     */
    public int getJsonCharacterCount() {
        return jsonCharacterCount;
    }
    
    /**
     * Returns the byte count of JSON format.
     * 
     * @return The JSON byte count
     */
    public int getJsonByteCount() {
        return jsonByteCount;
    }
    
    /**
     * Returns the character count of TOON format.
     * 
     * @return The TOON character count
     */
    public int getToonCharacterCount() {
        return toonCharacterCount;
    }
    
    /**
     * Returns the byte count of TOON format.
     * 
     * @return The TOON byte count
     */
    public int getToonByteCount() {
        return toonByteCount;
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
    
    /**
     * Returns the character savings compared to the non-optimal format.
     * 
     * @return The number of characters saved
     */
    public int getCharacterSavings() {
        final int maxCharacters = Math.max(jsonCharacterCount, toonCharacterCount);
        return maxCharacters - optimalCharacterCount;
    }
    
    /**
     * Returns the percentage of character savings.
     * 
     * @return The percentage of characters saved (0.0 to 100.0)
     */
    public double getCharacterSavingsPercentage() {
        final int maxCharacters = Math.max(jsonCharacterCount, toonCharacterCount);
        if (maxCharacters == 0) {
            return 0.0;
        }
        return ((double) getCharacterSavings() / maxCharacters) * 100.0;
    }
    
    /**
     * Returns the byte savings compared to the non-optimal format.
     * 
     * @return The number of bytes saved
     */
    public int getByteSavings() {
        final int maxBytes = Math.max(jsonByteCount, toonByteCount);
        return maxBytes - optimalByteCount;
    }
    
    /**
     * Returns the percentage of byte savings.
     * 
     * @return The percentage of bytes saved (0.0 to 100.0)
     */
    public double getByteSavingsPercentage() {
        final int maxBytes = Math.max(jsonByteCount, toonByteCount);
        if (maxBytes == 0) {
            return 0.0;
        }
        return ((double) getByteSavings() / maxBytes) * 100.0;
    }
    
    @Override
    public String toString() {
        return String.format(
            "OptimizationResult{optimalFormat=%s, tokens=%d/%d/%d (savings: %d, %.2f%%), chars=%d/%d/%d (savings: %d, %.2f%%), bytes=%d/%d/%d (savings: %d, %.2f%%)}",
            optimalFormat,
            optimalTokenCount, jsonTokenCount, toonTokenCount, getTokenSavings(), getTokenSavingsPercentage(),
            optimalCharacterCount, jsonCharacterCount, toonCharacterCount, getCharacterSavings(), getCharacterSavingsPercentage(),
            optimalByteCount, jsonByteCount, toonByteCount, getByteSavings(), getByteSavingsPercentage()
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
