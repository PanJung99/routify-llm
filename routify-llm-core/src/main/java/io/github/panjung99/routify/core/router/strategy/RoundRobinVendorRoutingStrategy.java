package io.github.panjung99.routify.core.router.strategy;

import io.github.panjung99.routify.core.client.RoutingContext;
import io.github.panjung99.routify.core.model.entity.VendorModel;
import io.github.panjung99.routify.core.router.AbstractVendorRoutingStrategy;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinVendorRoutingStrategy extends AbstractVendorRoutingStrategy {

    private final AtomicInteger currentIndex = new AtomicInteger(0);

    /** {@inheritDoc} */
    @Override
    protected VendorModel doRoute(List<VendorModel> vendorModels, RoutingContext context) {
        if (vendorModels.isEmpty()) {
            return null;
        }
        
        int index = currentIndex.getAndUpdate(i -> (i + 1) % vendorModels.size());
        return vendorModels.get(index);
    }
}