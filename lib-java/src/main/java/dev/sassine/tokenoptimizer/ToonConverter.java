package dev.sassine.tokenoptimizer;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class responsible for converting objects to TOON format.
 * TOON is a compact format that maintains hierarchy with explicit schema for arrays.
 */
public final class ToonConverter {
    
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    // TOON format constants
    private static final String NEWLINE = "\n";
    private static final String INDENT = "  ";
    private static final String TOON_SEPARATOR = ",";
    private static final String TOON_KEY_VALUE_SEPARATOR = ":";
    private static final String TOON_ARRAY_START = "[";
    private static final String TOON_ARRAY_END = "]";
    private static final String TOON_OBJECT_START = "{";
    private static final String TOON_OBJECT_END = "}";
    private static final String TOON_NULL = "null";
    private static final String QUOTE = "\"";
    
    // Character constants for quote checking
    private static final char SPACE = ' ';
    private static final char COMMA = ',';
    private static final char COLON = ':';
    private static final char ARRAY_START = '[';
    private static final char ARRAY_END = ']';
    private static final char OBJECT_START = '{';
    private static final char OBJECT_END = '}';
    
    // Prevent instantiation
    private ToonConverter() {
        throw new AssertionError("Utility class should not be instantiated");
    }
    
    /**
     * Converts an object to TOON format.
     * 
     * @param obj The object to be converted
     * @return String in TOON format
     * @throws IllegalArgumentException if obj is null
     */
    public static String toToon(final Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Object cannot be null");
        }
        
        try {
            // First, convert to Map for manipulation
            @SuppressWarnings("unchecked")
            final Map<String, Object> map = (Map<String, Object>) OBJECT_MAPPER.convertValue(obj, Map.class);
            return convertMapToToon(map, "");
        } catch (Exception e) {
            // If it fails, try to convert string directly using pattern matching
            if (obj instanceof String) {
                final String jsonString = (String) obj;
                try {
                    return jsonToToon(jsonString);
                } catch (Exception ex) {
                    return obj.toString();
                }
            }
            return obj.toString();
        }
    }
    
    /**
     * Converts a JSON string to TOON format.
     * 
     * @param jsonString The JSON string to be converted
     * @return String in TOON format
     * @throws IllegalArgumentException if jsonString is null or empty
     * @throws RuntimeException if conversion fails
     */
    public static String jsonToToon(final String jsonString) {
        if (jsonString == null) {
            throw new IllegalArgumentException("JSON string cannot be null");
        }
        
        final String trimmed = jsonString.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("JSON string cannot be empty");
        }
        
        try {
            @SuppressWarnings("unchecked")
            final Map<String, Object> map = (Map<String, Object>) OBJECT_MAPPER.readValue(trimmed, Map.class);
            return convertMapToToon(map, "");
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to TOON: " + e.getMessage(), e);
        }
    }
    
    /**
     * Converts a Map to TOON format with proper hierarchy and indentation.
     * 
     * @param map The map to be converted
     * @param indent Current indentation level
     * @return String in TOON format
     */
    private static String convertMapToToon(final Map<String, Object> map, final String indent) {
        if (map == null || map.isEmpty()) {
            return "";
        }
        
        final StringBuilder toon = new StringBuilder();
        boolean first = true;
        
        for (final Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) {
                toon.append(NEWLINE);
            }
            first = false;
            
            final String key = entry.getKey();
            final Object value = entry.getValue();
            
            // Add the key with indentation
            toon.append(indent).append(key);
            
            // Check if value is an array
            if (value instanceof Iterable) {
                final Iterable<?> iterable = (Iterable<?>) value;
                final List<?> list = iterableToList(iterable);
                if (isArrayOfObjects(list)) {
                    // Array of objects: key[count]{...}: or key[count]:
                    // No : before array, it's added inside convertArrayOfObjectsToToon
                    convertArrayOfObjectsToToon(toon, list, indent);
                } else {
                    // Simple array: key[count]: value1,value2,value3
                    // No : before array, it's added inside convertSimpleArrayToToon
                    convertSimpleArrayToToon(toon, list);
                }
            } else {
                // Non-array values need : separator
                toon.append(TOON_KEY_VALUE_SEPARATOR);
                appendValueToToon(toon, value, indent);
            }
        }
        
        return toon.toString();
    }
    
    /**
     * Appends a value to the TOON string builder with proper formatting.
     * 
     * @param toon The string builder to append to
     * @param value The value to append
     * @param indent Current indentation level
     */
    @SuppressWarnings("unchecked")
    private static void appendValueToToon(final StringBuilder toon, final Object value, final String indent) {
        if (value == null) {
            toon.append(" ").append(TOON_NULL);
        } else if (value instanceof Map) {
            // Nested object - add newline and indent
            toon.append(NEWLINE);
            toon.append(convertMapToToon((Map<String, Object>) value, indent + INDENT));
        } else if (value instanceof Iterable) {
            final Iterable<?> iterable = (Iterable<?>) value;
            // Array - check if it's an array of objects
            final List<?> list = iterableToList(iterable);
            if (isArrayOfObjects(list)) {
                // Arrays of objects inside objects use expanded format
                convertArrayOfObjectsToToon(toon, list, indent);
            } else {
                // Simple array - no space before, convertSimpleArrayToToon adds the format
                convertSimpleArrayToToon(toon, list);
            }
        } else if (value instanceof String) {
            toon.append(" ").append((String) value);
        } else if (value instanceof Boolean) {
            toon.append(" ").append(value);
        } else if (value instanceof Number) {
            toon.append(" ").append(value.toString());
        } else {
            toon.append(" ").append(value.toString());
        }
    }
    
    /**
     * Converts an array of objects to TOON format.
     * Format according to official library:
     * For arrays with same structure: key[count]{prop1,prop2,prop3}:
     *                                   val1,val2,val3
     *                                   val4,val5,val6
     * For arrays with different structures: key[count]:
     *                                        - prop1: val1
     *                                          prop2: val2
     * 
     * @param toon The string builder to append to
     * @param list The list of objects
     * @param indent Current indentation level
     */
    @SuppressWarnings("unchecked")
    private static void convertArrayOfObjectsToToon(final StringBuilder toon, final List<?> list, final String indent) {
        if (list.isEmpty()) {
            toon.append(TOON_ARRAY_START).append(0).append(TOON_ARRAY_END).append(TOON_KEY_VALUE_SEPARATOR);
            return;
        }
        
        // Get first object to determine schema
        final Object firstItem = list.get(0);
        if (!(firstItem instanceof Map)) {
            convertSimpleArrayToToon(toon, list);
            return;
        }
        
        final int count = list.size();
        final String itemIndent = indent + INDENT;
        final String propIndent = itemIndent + INDENT;
        
        // Check if all objects have the same structure (same keys in same order)
        final Map<String, Object> firstMap = (Map<String, Object>) firstItem;
        final List<String> schemaKeys = new ArrayList<>(firstMap.keySet());
        final boolean hasUniformStructure = list.stream()
            .allMatch(item -> item instanceof Map && 
                    hasSameKeys((Map<String, Object>) item, schemaKeys));
        
        // Check if any object has nested arrays or objects (use expanded format in that case)
        final boolean hasNestedStructures = schemaKeys.stream()
            .anyMatch(key -> {
                final Object value = firstMap.get(key);
                return value instanceof Iterable || value instanceof Map;
            });
        
        // Use compact format only if: uniform structure, more than 1 item, and no nested structures
        if (hasUniformStructure && count > 1 && !hasNestedStructures) {
            // Use compact format: key[count]{prop1,prop2,prop3}:
            //                      val1,val2,val3
            //                      val4,val5,val6
            toon.append(TOON_ARRAY_START).append(count).append(TOON_ARRAY_END).append(TOON_OBJECT_START);
            boolean firstKey = true;
            for (final String key : schemaKeys) {
                if (!firstKey) {
                    toon.append(TOON_SEPARATOR);
                }
                firstKey = false;
                toon.append(key);
            }
            toon.append(TOON_OBJECT_END).append(TOON_KEY_VALUE_SEPARATOR);
            toon.append(NEWLINE).append(itemIndent);
            
            // Add values for each object
            boolean isFirstItem = true;
            for (final Object item : list) {
                if (item instanceof Map) {
                    if (!isFirstItem) {
                        toon.append(NEWLINE).append(itemIndent);
                    }
                    isFirstItem = false;
                    
                    final Map<String, Object> itemMap = (Map<String, Object>) item;
                    boolean firstVal = true;
                    for (final String key : schemaKeys) {
                        if (!firstVal) {
                            toon.append(TOON_SEPARATOR);
                        }
                        firstVal = false;
                        final Object value = itemMap.get(key);
                        appendValueInline(toon, value);
                    }
                }
            }
        } else {
            // Use expanded format: key[count]:
            //                       - prop1: val1
            //                         prop2: val2
            toon.append(TOON_ARRAY_START).append(count).append(TOON_ARRAY_END).append(TOON_KEY_VALUE_SEPARATOR);
            
            // Add each object with - prefix and properties indented
            boolean isFirstItem = true;
            for (final Object item : list) {
                if (item instanceof Map) {
                    if (!isFirstItem) {
                        toon.append(NEWLINE);
                    }
                    isFirstItem = false;
                    
                    final Map<String, Object> itemMap = (Map<String, Object>) item;
                    
                    // Start item with - prefix
                    toon.append(NEWLINE).append(itemIndent).append("- ");
                    
                    // Add properties: first on same line as -, others on new lines
                    boolean firstProp = true;
                    for (final Map.Entry<String, Object> entry : itemMap.entrySet()) {
                        if (!firstProp) {
                            toon.append(NEWLINE).append(propIndent);
                        }
                        
                        final String propKey = entry.getKey();
                        final Object propValue = entry.getValue();
                        
                        toon.append(propKey);
                        // For arrays, don't add : before, the array format includes it
                        if (propValue instanceof Iterable<?>) {
                            final List<?> propList = iterableToList((Iterable<?>) propValue);
                            if (isArrayOfObjects(propList)) {
                                // Arrays of objects inside array items use expanded format
                                convertArrayOfObjectsToToon(toon, propList, propIndent);
                            } else {
                                // Simple array - no : before, convertSimpleArrayToToon adds [count]:
                                convertSimpleArrayToToon(toon, propList);
                            }
                        } else {
                            // Non-array values need : separator
                            toon.append(TOON_KEY_VALUE_SEPARATOR);
                            appendValueToToon(toon, propValue, propIndent);
                        }
                        
                        firstProp = false;
                    }
                }
            }
        }
    }
    
    /**
     * Checks if a map has the same keys as the provided list (in any order).
     * 
     * @param map The map to check
     * @param keys The list of expected keys
     * @return true if map has exactly the same keys
     */
    private static boolean hasSameKeys(final Map<String, Object> map, final List<String> keys) {
        if (map.size() != keys.size()) {
            return false;
        }
        return keys.stream().allMatch(map::containsKey);
    }
    
    /**
     * Appends a value inline (for array values, without property names).
     * Handles nested arrays, objects, and primitives according to TOON spec.
     * 
     * @param toon The string builder to append to
     * @param value The value to append
     */
    private static void appendValueInline(final StringBuilder toon, final Object value) {
        if (value == null) {
            toon.append(TOON_NULL);
        } else if (value instanceof Iterable) {
            // Nested array - convert to TOON format inline
            final Iterable<?> iterable = (Iterable<?>) value;
            final List<?> list = iterableToList(iterable);
            // Arrays inside array values are always treated as simple arrays
            convertSimpleArrayToToon(toon, list);
        } else if (value instanceof Map) {
            // Nested object - this shouldn't happen in array values according to TOON spec
            // But handle it gracefully by converting to compact representation
            @SuppressWarnings("unchecked")
            final Map<String, Object> map = (Map<String, Object>) value;
            toon.append(TOON_OBJECT_START);
            boolean first = true;
            for (final Map.Entry<String, Object> entry : map.entrySet()) {
                if (!first) {
                    toon.append(TOON_SEPARATOR);
                }
                first = false;
                toon.append(entry.getKey()).append(TOON_KEY_VALUE_SEPARATOR);
                appendValueInline(toon, entry.getValue());
            }
            toon.append(TOON_OBJECT_END);
        } else if (value instanceof String) {
            final String str = (String) value;
            // Strings need quotes if they contain special characters or are pure numbers
            if (needsQuotesInArray(str)) {
                toon.append(QUOTE).append(str).append(QUOTE);
            } else {
                toon.append(str);
            }
        } else if (value instanceof Number || value instanceof Boolean) {
            toon.append(value.toString());
        } else {
            toon.append(value.toString());
        }
    }
    
    /**
     * Checks if a string needs quotes when used in an array value.
     * According to TOON spec: strings that are pure numbers need quotes to differentiate from numbers.
     * 
     * @param str The string to check
     * @return true if quotes are needed
     */
    private static boolean needsQuotesInArray(final String str) {
        if (str.isEmpty()) {
            return true;
        }
        
        // Check if string is a pure number (needs quotes to differentiate from number type)
        boolean isNumeric = true;
        for (int i = 0; i < str.length(); i++) {
            final char c = str.charAt(i);
            if (!Character.isDigit(c) && c != '.' && c != '-' && c != '+') {
                isNumeric = false;
                break;
            }
        }
        if (isNumeric && str.length() > 0) {
            return true; // Pure numeric strings need quotes
        }
        
        // Check for special characters that require quotes in array values
        for (int i = 0; i < str.length(); i++) {
            final char c = str.charAt(i);
            if (c == SPACE || c == COMMA || c == COLON || c == ARRAY_START || c == ARRAY_END 
                || c == OBJECT_START || c == OBJECT_END || c == '\n' || c == '\r') {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Appends a property value to TOON format (for properties inside array items).
     * 
     * @param toon The string builder to append to
     * @param value The value to append
     * @param indent Current indentation level
     */
    @SuppressWarnings("unchecked")
    private static void appendPropertyValueToToon(final StringBuilder toon, final Object value, final String indent) {
        if (value == null) {
            toon.append(" ").append(TOON_NULL);
        } else if (value instanceof Map) {
            // Nested object - add newline and indent
            toon.append(NEWLINE);
            toon.append(convertMapToToon((Map<String, Object>) value, indent + INDENT));
        } else if (value instanceof Iterable) {
            final Iterable<?> iterable = (Iterable<?>) value;
            // Array - check if it's an array of objects
            final List<?> list = iterableToList(iterable);
            if (isArrayOfObjects(list)) {
                // For arrays of objects inside array items, use nested format
                convertArrayOfObjectsToToon(toon, list, indent);
            } else {
                // Simple array
                convertSimpleArrayToToon(toon, list);
            }
        } else if (value instanceof String) {
            toon.append(" ").append((String) value);
        } else if (value instanceof Boolean) {
            toon.append(" ").append(value);
        } else if (value instanceof Number) {
            toon.append(" ").append(value.toString());
        } else {
            toon.append(" ").append(value.toString());
        }
    }
    
    /**
     * Converts a simple array (primitives) to TOON format.
     * Format: [count]: value1,value2,value3
     * 
     * @param toon The string builder to append to
     * @param list The list of values
     */
    private static void convertSimpleArrayToToon(final StringBuilder toon, final List<?> list) {
        final int count = list.size();
        toon.append(TOON_ARRAY_START).append(count).append(TOON_ARRAY_END).append(TOON_KEY_VALUE_SEPARATOR);
        
        if (!list.isEmpty()) {
            toon.append(" ");
            boolean first = true;
            for (final Object item : list) {
                if (!first) {
                    toon.append(TOON_SEPARATOR);
                }
                first = false;
                appendValueInline(toon, item);
            }
        }
    }
    
    /**
     * Checks if a list contains only Map objects (array of objects).
     * 
     * @param list The list to check
     * @return true if all items are Maps
     */
    private static boolean isArrayOfObjects(final List<?> list) {
        if (list.isEmpty()) {
            return false;
        }
        return list.stream().allMatch(item -> item instanceof Map);
    }
    
    /**
     * Converts an Iterable to a List for easier manipulation.
     * 
     * @param iterable The iterable to convert
     * @return List containing all items
     */
    private static List<?> iterableToList(final Iterable<?> iterable) {
        final List<Object> list = new ArrayList<>();
        for (final Object item : iterable) {
            list.add(item);
        }
        return list;
    }
    
    /**
     * Converts a TOON string to a JSON string.
     * 
     * @param toonString The TOON string to be converted
     * @return String in JSON format
     * @throws IllegalArgumentException if toonString is null or empty
     * @throws RuntimeException if conversion fails
     */
    public static String toJson(final String toonString) {
        if (toonString == null) {
            throw new IllegalArgumentException("TOON string cannot be null");
        }
        
        final String trimmed = toonString.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("TOON string cannot be empty");
        }
        
        try {
            final Object obj = fromToon(trimmed);
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Error converting TOON to JSON: " + e.getMessage(), e);
        }
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
        if (toonString == null) {
            throw new IllegalArgumentException("TOON string cannot be null");
        }
        
        final String trimmed = toonString.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("TOON string cannot be empty");
        }
        
        try {
            final ToonParser parser = new ToonParser(trimmed);
            return parser.parse();
        } catch (Exception e) {
            throw new RuntimeException("Error parsing TOON: " + e.getMessage(), e);
        }
    }
    
    /**
     * Internal parser class for converting TOON strings to objects.
     */
    private static final class ToonParser {
        private final String toon;
        private int pos;
        private final int length;
        
        ToonParser(final String toon) {
            this.toon = toon;
            this.pos = 0;
            this.length = toon.length();
        }
        
        /**
         * Main parse method that returns the root object.
         */
        Object parse() {
            skipWhitespace();
            if (pos >= length) {
                return new java.util.HashMap<String, Object>();
            }
            
            // Check if it starts with an array indicator
            if (peek() == ARRAY_START) {
                return parseArray();
            }
            
            // Otherwise parse as object
            return parseObject("");
        }
        
        /**
         * Parses a TOON object (key-value pairs with indentation).
         */
        @SuppressWarnings("unchecked")
        private Map<String, Object> parseObject(final String baseIndent) {
            final Map<String, Object> map = new java.util.HashMap<>();
            
            while (pos < length) {
                skipWhitespace();
                if (pos >= length) {
                    break;
                }
                
                // Check if we've moved to a different indentation level (end of this object)
                final String currentIndent = getCurrentIndent();
                if (!currentIndent.startsWith(baseIndent) || currentIndent.length() < baseIndent.length()) {
                    break;
                }
                
                // Check for array item marker (-)
                if (peek() == '-') {
                    // This is an array item, not a property - should not happen at object level
                    break;
                }
                
                // Parse key
                final String key = parseKey();
                if (key == null || key.isEmpty()) {
                    break;
                }
                
                skipWhitespace();
                
                // Check for array notation: key[count] or key[count]{...}
                if (peek() == ARRAY_START) {
                    final Object arrayValue = parseArrayValue(key, currentIndent);
                    map.put(key, arrayValue);
                } else if (peek() == COLON) {
                    // Regular key: value
                    pos++; // consume ':'
                    skipWhitespace();
                    final Object value = parseValue(currentIndent);
                    map.put(key, value);
                } else {
                    // No separator, might be end of line
                    break;
                }
                
                // Skip to next line
                skipToNextLine();
            }
            
            return map;
        }
        
        /**
         * Parses an array value (handles both simple arrays and arrays of objects).
         */
        private Object parseArrayValue(final String key, final String indent) {
            // Parse [count] or [count]{prop1,prop2,prop3}
            pos++; // consume '['
            final int count = parseInt();
            if (peek() != ARRAY_END) {
                throw new RuntimeException("Expected ']' after array count at position " + pos);
            }
            pos++; // consume ']'
            
            // Check for compact format: {prop1,prop2,prop3}
            List<String> schemaKeys = null;
            if (peek() == OBJECT_START) {
                pos++; // consume '{'
                schemaKeys = parseSchemaKeys();
                if (peek() != OBJECT_END) {
                    throw new RuntimeException("Expected '}' after schema keys at position " + pos);
                }
                pos++; // consume '}'
            }
            
            skipWhitespace();
            
            // Check for ':'
            if (peek() == COLON) {
                pos++; // consume ':'
                skipWhitespace();
            }
            
            // Skip to next line
            skipToNextLine();
            
            if (count == 0) {
                return new ArrayList<>();
            }
            
            // Determine array type
            if (schemaKeys != null) {
                // Compact format: array of objects with uniform structure
                return parseCompactArrayOfObjects(count, schemaKeys, indent);
            } else {
                // Check if it's a simple array or expanded array of objects
                final String nextLineIndent = getCurrentIndent();
                if (nextLineIndent.length() > indent.length() && peek() == '-') {
                    // Expanded format: array of objects
                    return parseExpandedArrayOfObjects(count, nextLineIndent);
                } else {
                    // Simple array: [count]: value1,value2,value3
                    return parseSimpleArray();
                }
            }
        }
        
        /**
         * Parses a compact array of objects (uniform structure).
         */
        private List<Map<String, Object>> parseCompactArrayOfObjects(
                final int count, final List<String> schemaKeys, final String baseIndent) {
            final List<Map<String, Object>> list = new ArrayList<>();
            
            for (int i = 0; i < count; i++) {
                skipWhitespace();
                final String lineIndent = getCurrentIndent();
                if (lineIndent.length() <= baseIndent.length()) {
                    break; // Moved back to parent level
                }
                
                final Map<String, Object> item = new java.util.HashMap<>();
                final String[] values = parseCommaSeparatedValues();
                
                if (values.length != schemaKeys.size()) {
                    throw new RuntimeException(
                        String.format("Expected %d values but got %d at line %d", 
                            schemaKeys.size(), values.length, i + 1));
                }
                
                for (int j = 0; j < schemaKeys.size(); j++) {
                    item.put(schemaKeys.get(j), parseValueFromString(values[j]));
                }
                
                list.add(item);
                skipToNextLine();
            }
            
            return list;
        }
        
        /**
         * Parses an expanded array of objects (non-uniform structure).
         */
        private List<Map<String, Object>> parseExpandedArrayOfObjects(
                final int count, final String itemIndent) {
            final List<Map<String, Object>> list = new ArrayList<>();
            
            for (int i = 0; i < count; i++) {
                skipWhitespace();
                final String currentIndent = getCurrentIndent();
                if (currentIndent.length() < itemIndent.length()) {
                    break; // Moved back to parent level
                }
                
                // Expect '-' marker
                if (peek() != '-') {
                    break;
                }
                pos++; // consume '-'
                skipWhitespace();
                
                // Parse object properties
                final Map<String, Object> item = parseObject(itemIndent);
                list.add(item);
                
                skipToNextLine();
            }
            
            return list;
        }
        
        /**
         * Parses a simple array: value1,value2,value3.
         */
        private List<Object> parseSimpleArray() {
            final List<Object> list = new ArrayList<>();
            
            skipWhitespace();
            if (pos >= length || peek() == '\n' || peek() == '\r') {
                return list; // Empty array
            }
            
            final String[] values = parseCommaSeparatedValues();
            for (final String value : values) {
                list.add(parseValueFromString(value));
            }
            
            return list;
        }
        
        /**
         * Parses comma-separated values from current position.
         */
        private String[] parseCommaSeparatedValues() {
            final List<String> values = new ArrayList<>();
            final StringBuilder current = new StringBuilder();
            boolean inQuotes = false;
            
            while (pos < length) {
                final char c = peek();
                
                if (c == '"' && (pos == 0 || toon.charAt(pos - 1) != '\\')) {
                    inQuotes = !inQuotes;
                    current.append(c);
                    pos++;
                } else if (c == COMMA && !inQuotes) {
                    if (current.length() > 0) {
                        values.add(current.toString().trim());
                        current.setLength(0);
                    }
                    pos++;
                    skipWhitespace();
                } else if ((c == '\n' || c == '\r') && !inQuotes) {
                    break;
                } else {
                    current.append(c);
                    pos++;
                }
            }
            
            if (current.length() > 0) {
                values.add(current.toString().trim());
            }
            
            return values.toArray(new String[0]);
        }
        
        /**
         * Parses schema keys from {prop1,prop2,prop3}.
         */
        private List<String> parseSchemaKeys() {
            final List<String> keys = new ArrayList<>();
            final StringBuilder current = new StringBuilder();
            
            while (pos < length) {
                final char c = peek();
                if (c == OBJECT_END) {
                    if (current.length() > 0) {
                        keys.add(current.toString().trim());
                    }
                    break;
                } else if (c == COMMA) {
                    if (current.length() > 0) {
                        keys.add(current.toString().trim());
                        current.setLength(0);
                    }
                    pos++;
                } else {
                    current.append(c);
                    pos++;
                }
            }
            
            return keys;
        }
        
        /**
         * Parses a value (can be primitive, object, or array).
         */
        private Object parseValue(final String currentIndent) {
            skipWhitespace();
            
            if (pos >= length) {
                return null;
            }
            
            final char c = peek();
            
            if (c == ARRAY_START) {
                return parseArray();
            } else if (c == '\n' || c == '\r') {
                // Value on next line - might be nested object
                skipToNextLine();
                final String nextIndent = getCurrentIndent();
                if (nextIndent.length() > currentIndent.length()) {
                    return parseObject(currentIndent);
                }
                return null;
            } else {
                // Primitive value on same line
                return parsePrimitiveValue();
            }
        }
        
        /**
         * Parses an array (simple or of objects).
         */
        private Object parseArray() {
            pos++; // consume '['
            final int count = parseInt();
            if (peek() != ARRAY_END) {
                throw new RuntimeException("Expected ']' after array count");
            }
            pos++; // consume ']'
            
            skipWhitespace();
            if (peek() == COLON) {
                pos++; // consume ':'
                skipWhitespace();
            }
            
            skipToNextLine();
            
            if (count == 0) {
                return new ArrayList<>();
            }
            
            // Check if it's an array of objects by looking at next line
            final String nextIndent = getCurrentIndent();
            if (nextIndent.length() > 0 && peek() == '-') {
                // Expanded array of objects
                return parseExpandedArrayOfObjects(count, nextIndent);
            } else {
                // Simple array
                return parseSimpleArray();
            }
        }
        
        /**
         * Parses a primitive value from string.
         */
        private Object parseValueFromString(final String str) {
            if (str == null || str.isEmpty()) {
                return null;
            }
            
            final String trimmed = str.trim();
            
            if (trimmed.equals(TOON_NULL)) {
                return null;
            }
            
            // Remove quotes if present
            String unquoted = trimmed;
            if (trimmed.startsWith(QUOTE) && trimmed.endsWith(QUOTE) && trimmed.length() >= 2) {
                unquoted = trimmed.substring(1, trimmed.length() - 1);
            }
            
            // Try to parse as number
            try {
                if (unquoted.contains(".")) {
                    return Double.parseDouble(unquoted);
                } else {
                    return Long.parseLong(unquoted);
                }
            } catch (NumberFormatException e) {
                // Not a number
            }
            
            // Try boolean
            if (unquoted.equalsIgnoreCase("true")) {
                return true;
            }
            if (unquoted.equalsIgnoreCase("false")) {
                return false;
            }
            
            // String
            return unquoted;
        }
        
        /**
         * Parses a primitive value from current position.
         */
        private Object parsePrimitiveValue() {
            final StringBuilder sb = new StringBuilder();
            
            while (pos < length) {
                final char c = peek();
                if (c == '\n' || c == '\r' || c == COMMA) {
                    break;
                }
                sb.append(c);
                pos++;
            }
            
            return parseValueFromString(sb.toString());
        }
        
        /**
         * Parses a key (property name).
         */
        private String parseKey() {
            final StringBuilder sb = new StringBuilder();
            
            while (pos < length) {
                final char c = peek();
                if (c == COLON || c == ARRAY_START || c == '\n' || c == '\r') {
                    break;
                }
                sb.append(c);
                pos++;
            }
            
            return sb.toString().trim();
        }
        
        /**
         * Parses an integer from current position.
         */
        private int parseInt() {
            final StringBuilder sb = new StringBuilder();
            
            while (pos < length) {
                final char c = peek();
                if (Character.isDigit(c)) {
                    sb.append(c);
                    pos++;
                } else {
                    break;
                }
            }
            
            if (sb.length() == 0) {
                throw new RuntimeException("Expected integer at position " + pos);
            }
            
            return Integer.parseInt(sb.toString());
        }
        
        /**
         * Gets the current indentation level.
         */
        private String getCurrentIndent() {
            final int startPos = pos;
            final StringBuilder indent = new StringBuilder();
            
            while (pos < length) {
                final char c = peek();
                if (c == ' ') {
                    indent.append(c);
                    pos++;
                } else if (c == '\t') {
                    indent.append("  "); // Convert tab to 2 spaces
                    pos++;
                } else {
                    break;
                }
            }
            
            final String result = indent.toString();
            pos = startPos; // Reset position
            return result;
        }
        
        /**
         * Skips whitespace (spaces and tabs, but not newlines).
         */
        private void skipWhitespace() {
            while (pos < length) {
                final char c = peek();
                if (c == ' ' || c == '\t') {
                    pos++;
                } else {
                    break;
                }
            }
        }
        
        /**
         * Skips to the next line.
         */
        private void skipToNextLine() {
            while (pos < length) {
                final char c = peek();
                if (c == '\n') {
                    pos++;
                    break;
                } else if (c == '\r') {
                    pos++;
                    if (pos < length && peek() == '\n') {
                        pos++;
                    }
                    break;
                } else {
                    pos++;
                }
            }
        }
        
        /**
         * Peeks at the current character without advancing.
         */
        private char peek() {
            if (pos >= length) {
                return '\0';
            }
            return toon.charAt(pos);
        }
    }
}
