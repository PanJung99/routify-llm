package io.github.panjung99.routify.core.model.entity;

import io.github.panjung99.routify.core.model.enums.VendorType;
import lombok.Data;

/**
 * Vendor entity representing an AI service provider.
 * Contains basic information about the vendor including API endpoints and type.
 */
@Data
public class Vendor {

    /**
     * Unique identifier for the vendor.
     */
    private String vendorId;

    /**
     * Type of vendor defining the service provider.
     * Examples: OPEN_AI, DEEP_SEEK, GLM, DOU_BAO, XUN_FEI, GEMINI
     */
    private VendorType vendorType;

    /**
     * Base URL for the vendor's API endpoints.
     */
    private String apiBaseUrl;

}