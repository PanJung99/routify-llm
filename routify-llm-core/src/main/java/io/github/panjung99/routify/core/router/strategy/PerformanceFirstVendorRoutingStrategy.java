package io.github.panjung99.routify.core.router.strategy;

import io.github.panjung99.routify.core.client.RoutingContext;
import io.github.panjung99.routify.core.model.entity.VendorModel;
import io.github.panjung99.routify.core.router.AbstractVendorRoutingStrategy;
import io.github.panjung99.routify.core.router.metrics.LocalVendorMetrics;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class PerformanceFirstVendorRoutingStrategy extends AbstractVendorRoutingStrategy {

    // Performance metric weights
    private static final double LATENCY_WEIGHT = 0.6;
    private static final double SUCCESS_RATE_WEIGHT = 0.3;
    private static final double LOAD_WEIGHT = 0.1;
    
    // EWMA smoothing factor
    private static final double EWMA_ALPHA = 0.1;
    
    // Adaptive threshold limits
    private static final double MAX_LATENCY_LIMIT = 100000.0; // 100 seconds
    private static final int MAX_CONCURRENT_LIMIT = 5000;     // 5000 concurrent requests
    private static final double MIN_LATENCY_THRESHOLD = 100.0; // 100 milliseconds
    private static final int MIN_CONCURRENT_THRESHOLD = 10;    // 10 concurrent requests
    
    // Initial default values
    private static final double INITIAL_LATENCY_THRESHOLD = 5000.0; // 5 seconds
    private static final int INITIAL_CONCURRENT_THRESHOLD = 100;    // 100 concurrent requests

    // Adaptive thresholds that can adjust based on observed data
    private volatile double maxObservedLatency = INITIAL_LATENCY_THRESHOLD;
    private volatile int maxObservedConcurrentRequests = INITIAL_CONCURRENT_THRESHOLD;
    
    /** {@inheritDoc} */
    @Override
    protected VendorModel doRoute(List<VendorModel> vendorModels, RoutingContext context) {
        if (vendorModels.isEmpty()) {
            return null;
        }
        
        // Update adaptive thresholds based on current metrics
        updateAdaptiveThresholds(vendorModels, context);
        
        // Find the vendor with the best performance metrics
        Optional<VendorModel> bestPerforming = vendorModels.stream()
                .min(Comparator.comparingDouble(vendorModel -> {
                    LocalVendorMetrics metrics = context.getMetricsManager()
                            .getOrCreate(vendorModel.getVendorModelId());
                    return calculatePerformanceScore(metrics);
                }));
        
        return bestPerforming.orElse(null);
    }
    
    /**
     * Update adaptive thresholds based on current metrics from all vendors
     */
    private void updateAdaptiveThresholds(List<VendorModel> vendorModels, RoutingContext context) {
        double maxLatency = 0.0;
        int maxConcurrent = 0;
        
        for (VendorModel vendorModel : vendorModels) {
            LocalVendorMetrics metrics = context.getMetricsManager()
                    .getOrCreate(vendorModel.getVendorModelId());
            if (metrics != null) {
                maxLatency = Math.max(maxLatency, metrics.getAvgLatency());
                maxConcurrent = Math.max(maxConcurrent, metrics.getConcurrentRequests().get());
            }
        }
        
        // Update thresholds with smoothing to avoid sudden jumps

        // Cap extreme values to prevent threshold explosion
        double cappedMaxLatency = Math.min(maxLatency, MAX_LATENCY_LIMIT);
        int cappedMaxConcurrent = Math.min(maxConcurrent, MAX_CONCURRENT_LIMIT);
        
        // Apply EWMA formula and ensure minimum thresholds
        double newLatencyThreshold = EWMA_ALPHA * cappedMaxLatency + (1 - EWMA_ALPHA) * maxObservedLatency;
        maxObservedLatency = Math.max(MIN_LATENCY_THRESHOLD, newLatencyThreshold);
        
        int newConcurrentThreshold = (int)(EWMA_ALPHA * cappedMaxConcurrent + (1 - EWMA_ALPHA) * maxObservedConcurrentRequests);
        maxObservedConcurrentRequests = Math.max(MIN_CONCURRENT_THRESHOLD, newConcurrentThreshold);
    }
    
    /**
     * Calculate a performance score for a vendor based on its metrics.
     * Lower score indicates better performance.
     * 
     * @param metrics The vendor metrics to evaluate
     * @return Performance score (lower is better)
     */
    private double calculatePerformanceScore(LocalVendorMetrics metrics) {
        if (metrics == null) {
            return Double.MAX_VALUE;
        }
        
        // Normalize metrics to a common scale (0-1, where lower is better)
        double latencyScore = normalizeLatency(metrics.getAvgLatency());
        double successScore = normalizeSuccessRate(metrics.getSuccessRate());
        double loadScore = normalizeLoad(metrics.getConcurrentRequests().get());
        
        // Calculate weighted score
        return (latencyScore * LATENCY_WEIGHT) + 
               (successScore * SUCCESS_RATE_WEIGHT) + 
               (loadScore * LOAD_WEIGHT);
    }
    
    /**
     * Normalize latency to a 0-1 scale using adaptive threshold
     * Handles both normal (500ms) and extreme (500s) latency scenarios
     */
    private double normalizeLatency(double latency) {
        // Use adaptive threshold that adjusts based on observed data
        double threshold = Math.max(maxObservedLatency, MIN_LATENCY_THRESHOLD);
        return Math.min(latency / threshold, 1.0);
    }
    
    /**
     * Normalize success rate to a 0-1 scale (lower is better)
     * Higher success rate should result in lower score
     */
    private double normalizeSuccessRate(double successRate) {
        return 1.0 - successRate; // Invert so lower failure rate = better
    }
    
    /**
     * Normalize load to a 0-1 scale using adaptive threshold
     * Handles both normal (100) and extreme (5000) concurrent request scenarios
     */
    private double normalizeLoad(int concurrentRequests) {
        // Use adaptive threshold that adjusts based on observed data
        int threshold = Math.max(maxObservedConcurrentRequests, MIN_CONCURRENT_THRESHOLD);
        return Math.min(concurrentRequests / (double) threshold, 1.0);
    }
}