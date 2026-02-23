package io.github.panjung99.routify.core.router;

import io.github.panjung99.routify.core.client.RoutingContext;
import io.github.panjung99.routify.core.model.entity.VendorModel;
import io.github.panjung99.routify.core.router.enums.CircuitBreakerState;
import io.github.panjung99.routify.core.router.metrics.LocalVendorMetrics;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractVendorRoutingStrategy implements VendorRoutingStrategy {

    /** {@inheritDoc} */
    @Override
    public final VendorModel route(RoutingContext context) {

        List<VendorModel> available = filterFatal(context.getVendorModels(), context);
        if (available.isEmpty()) {
            return null; // Let the caller handle exception or fallback
        }
        VendorModel vendorModel = doRoute(available, context);
        if (vendorModel == null) {
            return null;
        }
        return vendorModel;
    }

    /**
     * Subclasses should implement the routing logic according to their specific strategy.
     * Unusable vendors have been filtered before {@code doRoute} is invoked.
     * @param vendorModels All available VendorModels for the LogicModel.
     * @param context The current routing context.
     * @return The most suitable {@code VendorModel} base on the strategy.
     */
    protected abstract VendorModel doRoute(List<VendorModel> vendorModels, RoutingContext context);

    /**
     * Filters out unusable vendors by checking fatal factors in their metrics.
     * @param vendorModels All VendorModels for the LogicModel.
     * @return The vendor model list that has filtered out unusable vendors.
     */
    private List<VendorModel> filterFatal(List<VendorModel> vendorModels, RoutingContext context) {
        return vendorModels.stream()
                .filter(vendorModel -> {
                    LocalVendorMetrics metrics = context.getMetricsManager().getOrCreate(vendorModel.getVendorModelId());
                    if (metrics == null) {
                        return false;
                    }
                    return !metrics.isManuallyDisabled()
                            && metrics.isHealthy()
                            && metrics.getCircuitBreakerState() != CircuitBreakerState.OPEN;
                })
                .collect(Collectors.toList());
    }
}
