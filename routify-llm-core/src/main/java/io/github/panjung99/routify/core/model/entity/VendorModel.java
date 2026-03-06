package io.github.panjung99.routify.core.model.entity;

import lombok.Data;

import java.util.List;


/**
 * Vendor Model entity representing a specific model implementation from a vendor.
 * This is the actual model that gets called when routing decisions are made.
 */
@Data
public class VendorModel {

    /**
     * Unique identifier for the vendor model.
     */
    private String vendorModelId;

    /**
     * Model name as defined by the vendor.
     * This is the internal name used by the vendor's API.
     */
    private String name;

    /**
     * Vendor ID that owns this model.
     */
    private String vendorId;

    /**
     * Pricing information for this model.
     * Contains cost details for input and output usage.
     */
    private List<PricingItem> pricingItems;

    /**
     * 路由权重
     */
    private int weight;
}
