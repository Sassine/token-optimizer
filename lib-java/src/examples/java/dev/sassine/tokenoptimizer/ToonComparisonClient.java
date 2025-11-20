package dev.sassine.tokenoptimizer;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

/**
 * Client to compare our TOON implementation with the official TOON library.
 * Connects to a local server running the official TOON library.
 */
public final class ToonComparisonClient {
    
    private static final String DEFAULT_SERVER_URL = "http://localhost:3000";
    private static final String CONVERT_ENDPOINT = "/api/toon/convert";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    
    private final String serverUrl;
    
    /**
     * Creates a new comparison client with default server URL.
     */
    public ToonComparisonClient() {
        this(DEFAULT_SERVER_URL);
    }
    
    /**
     * Creates a new comparison client with custom server URL.
     * 
     * @param serverUrl The base URL of the TOON comparison server
     */
    public ToonComparisonClient(final String serverUrl) {
        this.serverUrl = serverUrl;
    }
    
    /**
     * Converts a JSON object to TOON format using the official library.
     * 
     * @param jsonObject The object to convert
     * @return The TOON string from the official library
     * @throws IOException if the HTTP request fails
     * @throws InterruptedException if the request is interrupted
     */
    public String convertToToonOfficial(final Object jsonObject) throws IOException, InterruptedException {
        final String requestBody = OBJECT_MAPPER.writeValueAsString(
            new HashMap<String, Object>() {{
                put("json", jsonObject);
            }}
        );
        
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + CONVERT_ENDPOINT))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .timeout(Duration.ofSeconds(10))
                .build();
        
        final HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new IOException("Server returned status " + response.statusCode() + ": " + response.body());
        }
        
        @SuppressWarnings("unchecked")
        final Map<String, Object> responseMap = OBJECT_MAPPER.readValue(response.body(), Map.class);
        
        if (!Boolean.TRUE.equals(responseMap.get("success"))) {
            throw new IOException("Conversion failed: " + responseMap.get("error"));
        }
        
        return (String) responseMap.get("toon");
    }
    
    /**
     * Checks if the server is available.
     * 
     * @return true if the server is reachable
     */
    public boolean isServerAvailable() {
        try {
            final HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl + "/health"))
                    .GET()
                    .timeout(Duration.ofSeconds(2))
                    .build();
            
            final HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Compares our TOON implementation with the official library.
     * 
     * @param jsonObject The object to compare
     * @return Comparison result
     */
    public ComparisonResult compare(final Object jsonObject) {
        try {
            final String ourToon = ToonConverter.toToon(jsonObject);
            final String officialToon = convertToToonOfficial(jsonObject);
            
            final boolean isEqual = normalizeToon(ourToon).equals(normalizeToon(officialToon));
            
            return new ComparisonResult(ourToon, officialToon, isEqual);
        } catch (Exception e) {
            return new ComparisonResult(null, null, false, e.getMessage());
        }
    }
    
    /**
     * Normalizes TOON string for comparison (removes extra whitespace).
     */
    private String normalizeToon(final String toon) {
        if (toon == null) {
            return "";
        }
        return toon.replaceAll("\\s+", " ").trim();
    }
    
    /**
     * Result of TOON comparison.
     */
    public static final class ComparisonResult {
        private final String ourToon;
        private final String officialToon;
        private final boolean isEqual;
        private final String error;
        
        public ComparisonResult(final String ourToon, final String officialToon, final boolean isEqual) {
            this(ourToon, officialToon, isEqual, null);
        }
        
        public ComparisonResult(final String ourToon, final String officialToon, final boolean isEqual, final String error) {
            this.ourToon = ourToon;
            this.officialToon = officialToon;
            this.isEqual = isEqual;
            this.error = error;
        }
        
        public String getOurToon() {
            return ourToon;
        }
        
        public String getOfficialToon() {
            return officialToon;
        }
        
        public boolean isEqual() {
            return isEqual;
        }
        
        public String getError() {
            return error;
        }
        
        public boolean hasError() {
            return error != null;
        }
    }
}

