package io.github.panjung99.routify.core.model.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * API Key entity representing authentication credentials for vendor APIs.
 * Each API key is associated with a specific vendor.
 */
@Data
public class ApiKey {

    /**
     * Vendor ID that this API key belongs to.
     * References the {@code Vendor} that provides the API service.
     */
    private String vendorId;

    /**
     * The actual API key value used for authentication.
     * This is the secret token provided by the vendor.
     */
    private String apiKey;

    /**
     * Expiration date and time of the API key.
     * After this time, the key becomes invalid and cannot be used.
     */
    private LocalDateTime expiresAt;
}