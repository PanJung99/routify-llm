package io.github.panjung99.routify.core.router.metrics;

import io.github.panjung99.routify.core.router.enums.CircuitBreakerState;

public interface VendorMetricsRecorder {

    /**
     * Record a request result and update metrics.
     * @param metrics the target vendor metrics
     * @param latency request latency in milliseconds
     * @param success whether the request was successful
     */
    void record(LocalVendorMetrics metrics, long latency, boolean success);

    /**
     * Increment current concurrent request count.
     */
    void incrementConcurrency(LocalVendorMetrics metrics);

    /**
     * Decrement current concurrent request count.
     */
    void decrementConcurrency(LocalVendorMetrics metrics);

    /**
     * Update health status.
     * @param metrics the target vendor metrics
     * @param healthy {@code true} if the vendor is healthy.
     */
    void updateHealth(LocalVendorMetrics metrics, boolean healthy);

    /**
     * Update circuit breaker state.
     * @param metrics the target vendor metrics
     * @param state current state of circuit breaker.
     */
    void updateCircuitBreakerState(LocalVendorMetrics metrics,
                                   CircuitBreakerState state);

    /**
     * Manually disable or enable vendor.
     * @param metrics the target vendor metrics
     * @param disabled {@code true} to disable it.
     */
    void setManuallyDisabled(LocalVendorMetrics metrics, boolean disabled);
}
