package io.github.panjung99.routify.core.apikey;

import io.github.panjung99.routify.core.model.entity.ApiKey;

import java.util.List;

/**
 * API Key Routing Strategy interface for selecting API keys for vendor model calls.
 * Different strategies can be implemented based on quota, load balancing, or other factors.
 */
public interface ApiKeyRoutingStrategy {
    
    /**
     * Selects an API key from the available keys for a vendor model call.
     * 
     * @param availableKeys list of available API keys for the vendor
     * @param vendorModelId the vendor model ID that will be called
     * @return selected ApiKey instance, or null if no suitable key is available
     */
    ApiKey route(List<ApiKey> availableKeys, String vendorModelId);
    
    /**
     * Gets the strategy name for identification and logging purposes.
     * 
     * @return the name of this routing strategy
     */
    String getStrategyName();
}