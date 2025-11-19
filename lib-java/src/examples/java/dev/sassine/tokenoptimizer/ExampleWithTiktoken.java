package dev.sassine.tokenoptimizer;

import com.knuddels.jtokkit.api.ModelType;
import java.util.HashMap;
import java.util.Map;

/**
 * Example demonstrating the use of TokenOptimizer with tiktoken ModelType for model-specific token counting.
 */
public class ExampleWithTiktoken {
    
    public static void main(final String[] args) {
        System.out.println("=== Token Optimizer with Tiktoken Examples ===\n");
        
        // Create a simple object
        final Map<String, Object> person = new HashMap<>();
        person.put("name", "John");
        person.put("age", 30);
        person.put("city", "New York");
        
        // Example 1: Using generic estimation (default - no ModelType)
        System.out.println("=== Example 1: Generic Estimation (Default) ===");
        final OptimizationResult result1 = TokenOptimizer.optimize(person);
        System.out.println("Optimal format: " + result1.getOptimalFormat());
        System.out.println("JSON tokens: " + result1.getJsonTokenCount());
        System.out.println("TOON tokens: " + result1.getToonTokenCount());
        System.out.println("Savings: " + result1.getTokenSavings() + " tokens (" +
                String.format("%.2f", result1.getTokenSavingsPercentage()) + "%)");
        System.out.println();
        
        // Example 2: Using GPT-4 model (tiktoken)
        System.out.println("=== Example 2: GPT-4 Model (Tiktoken) ===");
        final OptimizationResult result2 = TokenOptimizer.optimize(person, ModelType.GPT_4);
        System.out.println("Optimal format: " + result2.getOptimalFormat());
        System.out.println("JSON tokens: " + result2.getJsonTokenCount());
        System.out.println("TOON tokens: " + result2.getToonTokenCount());
        System.out.println("Savings: " + result2.getTokenSavings() + " tokens (" +
                String.format("%.2f", result2.getTokenSavingsPercentage()) + "%)");
        System.out.println();
        
        // Example 3: Using GPT-3.5 Turbo model (tiktoken)
        System.out.println("=== Example 3: GPT-3.5 Turbo Model (Tiktoken) ===");
        final OptimizationResult result3 = TokenOptimizer.optimize(person, ModelType.GPT_3_5_TURBO);
        System.out.println("Optimal format: " + result3.getOptimalFormat());
        System.out.println("JSON tokens: " + result3.getJsonTokenCount());
        System.out.println("TOON tokens: " + result3.getToonTokenCount());
        System.out.println("Savings: " + result3.getTokenSavings() + " tokens (" +
                String.format("%.2f", result3.getTokenSavingsPercentage()) + "%)");
        System.out.println();
        
        // Example 4: Using Claude model (tiktoken) - Claude uses same encoding as GPT-4
        System.out.println("=== Example 4: Text Davinci 003 Model (Tiktoken) ===");
        final OptimizationResult result4 = TokenOptimizer.optimize(person, ModelType.TEXT_DAVINCI_003);
        System.out.println("Optimal format: " + result4.getOptimalFormat());
        System.out.println("JSON tokens: " + result4.getJsonTokenCount());
        System.out.println("TOON tokens: " + result4.getToonTokenCount());
        System.out.println("Savings: " + result4.getTokenSavings() + " tokens (" +
                String.format("%.2f", result4.getTokenSavingsPercentage()) + "%)");
        System.out.println();
        
        // Example 5: Direct token counting comparison
        System.out.println("=== Example 5: Direct Token Counting Comparison ===");
        final String testText = "Hello, this is a test string for token counting!";
        System.out.println("Text: " + testText);
        System.out.println("Generic estimation: " + TokenCounter.countTokens(testText) + " tokens");
        System.out.println("GPT-4 (tiktoken): " + TokenCounter.countTokens(testText, ModelType.GPT_4) + " tokens");
        System.out.println("GPT-3.5 Turbo (tiktoken): " + TokenCounter.countTokens(testText, ModelType.GPT_3_5_TURBO) + " tokens");
        System.out.println("Text Davinci 003 (tiktoken): " + TokenCounter.countTokens(testText, ModelType.TEXT_DAVINCI_003) + " tokens");
    }
}

