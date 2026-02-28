package io.github.panjung99.routify.core.apikey;

import io.github.panjung99.routify.core.model.entity.ApiKey;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Abstract base class for API Key routing strategies.
 * Provides common filtering logic for expired and invalid keys.
 */
public abstract class AbstractApiKeyRoutingStrategy implements ApiKeyRoutingStrategy {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final ApiKey route(List<ApiKey> availableKeys, String vendorModelId) {
        // Filter out expired and invalid keys
        List<ApiKey> validKeys = filterValidKeys(availableKeys);
        
        if (validKeys.isEmpty()) {
            return null; // No valid keys available
        }
        
        // Delegate to concrete strategy implementation
        return doRoute(validKeys, vendorModelId);
    }
    
    /**
     * Concrete strategy implementations should override this method.
     * 
     * @param validKeys list of valid API keys (already filtered)
     * @param vendorModelId the vendor model ID that will be called
     * @return selected ApiKey instance
     */
    protected abstract ApiKey doRoute(List<ApiKey> validKeys, String vendorModelId);
    
    /**
     * Filters out expired and invalid API keys.
     * 
     * @param keys list of all available API keys
     * @return list of valid API keys
     */
    protected List<ApiKey> filterValidKeys(List<ApiKey> keys) {
        LocalDateTime now = LocalDateTime.now();
        
        return keys.stream()
                .filter(Objects::nonNull)
                .filter(key -> key.getApiKey() != null && !key.getApiKey().trim().isEmpty())
                .filter(key -> key.getExpiresAt() == null || key.getExpiresAt().isAfter(now))
                .collect(Collectors.toList());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public abstract String getStrategyName();
}