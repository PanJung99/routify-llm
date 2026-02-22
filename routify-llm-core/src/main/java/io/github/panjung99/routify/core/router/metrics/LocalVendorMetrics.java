package io.github.panjung99.routify.core.router.metrics;

import io.github.panjung99.routify.core.router.enums.CircuitBreakerState;
import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Local metrics to tracking performance and health status.
 */
@Data
public class LocalVendorMetrics {

    // Internal lock for recorder synchronized update.
    private final transient Object updateLock = new Object();

    private final String vendorId;

    /**
     * Current average latency in milliseconds.
     */
    private volatile double avgLatency = 0.0;

    /**
     * Current success rate. (0.0 to 1.0)
     */
    private volatile double successRate = 1.0;

    /**
     * Total number of requests since service startup.
     */
    private AtomicLong totalRequests = new AtomicLong(0L);

    /**
     * Total number of successful requests since service startup.
     */
    private AtomicLong totalSuccess = new AtomicLong(0L);

    /**
     * Current number of active concurrent requests.
     */
    private AtomicInteger concurrentRequests = new AtomicInteger(0);

    /**
     * Health status flag.
     * {@code true} if the vendor is healthy.
     */
    private volatile boolean healthy = true;

    /**
     * Current state of circuit breaker.
     * e.g., CLOSED, OPEN, HALF_OPEN
     */
    private volatile CircuitBreakerState circuitBreakerState;

    /**
     * Flag to manually disable the vendor.
     * If the vendor is unavailable or should be skipped, set this to {@code true} to disable it.
     */
    private volatile boolean manuallyDisabled = false;

    /**
     * The timestamp when this vendor model was last used.
     */
    private volatile long lastUsedAt;

    public LocalVendorMetrics(String vendorId) {
        this.vendorId = vendorId;
    }
}