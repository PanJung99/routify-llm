package io.github.panjung99.routify.core.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class for JSON serialization and deserialization.
 */
public class JsonUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            // Ignore unknown properties to improve SDK compatibility.
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private JsonUtil() {
    }

    /**
     * Deserializes JSON string to an object of the specified class.
     * @param json The JSON string to parse.
     * @param clazz The target class.
     * @return The deserialized object.
     * @param <T> The type of the resulting object.
     */
    public static <T> T readValue(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Routify-LLM SDK JSON parse error", e);
        }
    }

    /**
     * Serializes an object into its JSON string representation.
     * @param obj The object to serialize.
     * @return The serialized JSON string.
     */
    public static String toJson(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Routify-LLM SDK JSON serialize error", e);
        }
    }
}
