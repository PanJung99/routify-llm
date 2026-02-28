package io.github.panjung99.routify.core.apikey.strategy;

import io.github.panjung99.routify.core.apikey.AbstractApiKeyRoutingStrategy;
import io.github.panjung99.routify.core.model.entity.ApiKey;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Random API Key routing strategy.
 * Randomly selects an API key from the available keys.
 */
public class RandomApiKeyRoutingStrategy extends AbstractApiKeyRoutingStrategy {
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected ApiKey doRoute(List<ApiKey> validKeys, String vendorModelId) {
        if (validKeys.isEmpty()) {
            return null;
        }
        
        int index = ThreadLocalRandom.current().nextInt(validKeys.size());
        return validKeys.get(index);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getStrategyName() {
        return "random";
    }
}