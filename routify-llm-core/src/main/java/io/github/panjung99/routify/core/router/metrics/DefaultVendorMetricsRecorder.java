package io.github.panjung99.routify.core.router.metrics;

import io.github.panjung99.routify.core.router.enums.CircuitBreakerState;

public class DefaultVendorMetricsRecorder implements VendorMetricsRecorder {

    private final double alpha; // EWMA Smoothing Factor

    public DefaultVendorMetricsRecorder(double alpha) {
        this.alpha = alpha;
    }

    /**
     * {@inheritDoc}
     * Recording metrics via EWMA algorithm,
     */
    @Override
    public void record(LocalVendorMetrics metrics, long latency, boolean success) {
        if (metrics == null) {
            throw new IllegalArgumentException("Metrics object cannot be null");
        }
        // Use a floor of 1ms to prevent average latency from fluctuating too much.
        long finalLatency = Math.max(latency, 1L);

        // Ensure average latency and success rate are updated atomically
        synchronized (metrics.getUpdateLock()) {
            // Update avgLatency (EWMA)
            double newAvg = alpha * finalLatency + (1 - alpha) * metrics.getAvgLatency();
            metrics.setAvgLatency(newAvg);

            // update successRate (EWMA)
            double newSuccess = alpha * (success ? 1.0 : 0.0) + (1 - alpha) * metrics.getSuccessRate();
            metrics.setSuccessRate(newSuccess);
        }

        // Update total requests number.
        metrics.getTotalRequests().incrementAndGet();
        if (success) {
            metrics.getTotalSuccess().incrementAndGet();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void incrementConcurrency(LocalVendorMetrics metrics) {
        metrics.getConcurrentRequests().incrementAndGet();
    }

    /** {@inheritDoc} */
    @Override
    public void decrementConcurrency(LocalVendorMetrics metrics) {
        metrics.getConcurrentRequests().decrementAndGet();
    }

    /** {@inheritDoc} */
    @Override
    public void updateHealth(LocalVendorMetrics metrics, boolean healthy) {
        metrics.setHealthy(healthy);
    }

    /** {@inheritDoc} */
    @Override
    public void updateCircuitBreakerState(LocalVendorMetrics metrics, CircuitBreakerState state) {
        metrics.setCircuitBreakerState(state);
    }

    /** {@inheritDoc} */
    @Override
    public void setManuallyDisabled(LocalVendorMetrics metrics, boolean disabled) {
        metrics.setManuallyDisabled(disabled);
    }
}
