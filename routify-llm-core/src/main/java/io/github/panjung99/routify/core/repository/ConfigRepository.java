package io.github.panjung99.routify.core.repository;

import io.github.panjung99.routify.core.model.entity.*;

import java.util.List;
import java.util.Optional;

/**
 * Configuration Repository interface for managing LLM routing configurations.
 * Implementations can choose data sources (memory, database, config center, etc.)
 * and add caching as needed.
 */
public interface ConfigRepository {
    
    /**
     * Retrieves basic information for a logical model by its name.
     * 
     * @param modelName the name of the logical model to retrieve
     * @return Optional containing the LogicModel if found, empty otherwise
     */
    Optional<LogicModel> getModel(String modelName);

    /**
     * Retrieves all available vendor models for a specific logical model.
     * These are the actual model implementations that can handle requests
     * for the given logical model.
     * 
     * @param logicModelName the name of the logical model
     * @return list of VendorModel instances that can serve the logical model
     */
    List<VendorModel> getVendorModels(String logicModelName);

    /**
     * Retrieves complete vendor configuration by vendor ID.
     * 
     * @param vendorId the unique identifier of the vendor
     * @return Optional containing the Vendor if found, empty otherwise
     */
    Optional<Vendor> getVendor(String vendorId);

    /**
     * Retrieves all API keys for a specific vendor.
     * 
     * @param vendorId the unique identifier of the vendor
     * @return list of ApiKey instances belonging to the vendor
     */
    List<ApiKey> getApiKeysByVendor(String vendorId);
}
