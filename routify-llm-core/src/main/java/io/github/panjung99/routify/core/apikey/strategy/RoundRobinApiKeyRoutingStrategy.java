package io.github.panjung99.routify.core.apikey.strategy;

import io.github.panjung99.routify.core.apikey.AbstractApiKeyRoutingStrategy;
import io.github.panjung99.routify.core.model.entity.ApiKey;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Round-robin API Key routing strategy.
 * Cycles through available API keys in sequence for load balancing.
 */
public class RoundRobinApiKeyRoutingStrategy extends AbstractApiKeyRoutingStrategy {
    
    private final AtomicInteger currentIndex = new AtomicInteger(0);
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected ApiKey doRoute(List<ApiKey> validKeys, String vendorModelId) {
        if (validKeys.isEmpty()) {
            return null;
        }
        
        int index = currentIndex.getAndUpdate(i -> (i + 1) % validKeys.size());
        return validKeys.get(index);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getStrategyName() {
        return "round-robin";
    }
}