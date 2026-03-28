package io.github.panjung99.routify.core.router.strategy;

import io.github.panjung99.routify.core.client.RoutingContext;
import io.github.panjung99.routify.core.model.entity.VendorModel;
import io.github.panjung99.routify.core.router.AbstractVendorRoutingStrategy;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomVendorRoutingStrategy extends AbstractVendorRoutingStrategy {

    /** {@inheritDoc} */
    @Override
    protected VendorModel doRoute(List<VendorModel> vendorModels, RoutingContext context) {
        if (vendorModels.isEmpty()) {
            return null;
        }
        
        int randomIndex = ThreadLocalRandom.current().nextInt(vendorModels.size());
        return vendorModels.get(randomIndex);
    }
}