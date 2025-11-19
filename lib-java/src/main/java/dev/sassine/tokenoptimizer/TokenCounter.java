package dev.sassine.tokenoptimizer;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.ModelType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class responsible for counting the number of tokens in a string.
 * Supports both generic estimation and model-specific token counting using tiktoken.
 */
public final class TokenCounter {
    
    // Token estimation constants
    private static final double CHARS_PER_TOKEN = 4.0;
    private static final double WORDS_PER_TOKEN = 0.75;
    private static final int MIN_TOKEN_COUNT = 0;
    
    // Cache for tiktoken encodings to avoid repeated initialization
    private static final Map<ModelType, Encoding> ENCODING_CACHE = new ConcurrentHashMap<>();
    
    // Prevent instantiation
    private TokenCounter() {
        throw new AssertionError("Utility class should not be instantiated");
    }
    
    /**
     * Counts the approximate number of tokens in a string using generic estimation.
     * This implementation uses an estimation: approximately 1 token = 4 characters
     * or 0.75 words, which is a common approximation for language models.
     * 
     * @param text The text to count tokens in
     * @return The approximate number of tokens
     * @throws IllegalArgumentException if text is null
     */
    public static int countTokens(final String text) {
        return countTokens(text, null);
    }
    
    /**
     * Counts tokens in a string using the specified model's tokenizer.
     * If modelType is null, uses generic estimation algorithm.
     * Otherwise, uses tiktoken for accurate model-specific token counting.
     * 
     * @param text The text to count tokens in
     * @param modelType The tiktoken ModelType to use for counting (null for generic estimation)
     * @return The number of tokens
     * @throws IllegalArgumentException if text is null
     */
    public static int countTokens(final String text, final ModelType modelType) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        
        if (text.isEmpty()) {
            return MIN_TOKEN_COUNT;
        }
        
        // Use tiktoken for model-specific counting if modelType is provided
        if (modelType != null) {
            return countTokensWithTiktoken(text, modelType);
        }
        
        // Use generic estimation if modelType is null
        return countTokensGeneric(text);
    }
    
    /**
     * Counts tokens using tiktoken encoding for accurate model-specific counting.
     * 
     * @param text The text to count tokens in
     * @param modelType The tiktoken ModelType to use
     * @return The number of tokens
     */
    private static int countTokensWithTiktoken(final String text, final ModelType modelType) {
        try {
            // Get encoding from cache or create new one
            final Encoding encoding = ENCODING_CACHE.computeIfAbsent(modelType, type -> {
                try {
                    return Encodings.newDefaultEncodingRegistry().getEncodingForModel(type);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to load encoding for model: " + type, e);
                }
            });
            
            // Encode text and return token count
            return encoding.encode(text).size();
        } catch (Exception e) {
            // Fallback to generic estimation if tiktoken fails
            return countTokensGeneric(text);
        }
    }
    
    /**
     * Counts tokens using generic estimation algorithm.
     * 
     * @param text The text to count tokens in
     * @return The approximate number of tokens
     */
    private static int countTokensGeneric(final String text) {
        // Method 1: Character-based (common approximation: 1 token ≈ 4 characters)
        final int charBasedTokens = (int) Math.ceil(text.length() / CHARS_PER_TOKEN);
        
        // Method 2: Word-based (approximation: 1 token ≈ 0.75 words)
        // Optimized: avoid creating array if not needed
        final int wordCount = countWords(text);
        final int wordBasedTokens = (int) Math.ceil(wordCount / WORDS_PER_TOKEN);
        
        // Return the average of both methods for better accuracy
        return (charBasedTokens + wordBasedTokens) / 2;
    }
    
    /**
     * Counts words in a string efficiently without creating intermediate arrays.
     * 
     * @param text The text to count words in
     * @return The number of words
     */
    private static int countWords(final String text) {
        if (text.isEmpty()) {
            return 0;
        }
        
        int wordCount = 0;
        boolean inWord = false;
        
        for (int i = 0; i < text.length(); i++) {
            final char c = text.charAt(i);
            if (Character.isWhitespace(c)) {
                inWord = false;
            } else if (!inWord) {
                wordCount++;
                inWord = true;
            }
        }
        
        return wordCount;
    }
    
    /**
     * Counts tokens in a more detailed way considering text structure.
     * Takes into account special characters, spaces, and punctuation.
     * 
     * @param text The text to count tokens in
     * @return The approximate number of tokens
     * @throws IllegalArgumentException if text is null
     */
    public static int countTokensDetailed(final String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        
        if (text.isEmpty()) {
            return MIN_TOKEN_COUNT;
        }
        
        int tokenCount = 0;
        boolean inWord = false;
        
        for (int i = 0; i < text.length(); i++) {
            final char c = text.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                if (!inWord) {
                    tokenCount++;
                    inWord = true;
                }
            } else if (Character.isWhitespace(c)) {
                inWord = false;
            } else {
                // Special characters are usually separate tokens
                tokenCount++;
                inWord = false;
            }
        }
        
        return tokenCount;
    }
}
