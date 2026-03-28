package io.github.panjung99.routify.core.router.metrics;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class MetricsStorageManager {

    private final ConcurrentMap<String, LocalVendorMetrics> metricsMap = new ConcurrentHashMap<>();

    private final ScheduledExecutorService scheduler;

    private ScheduledFuture<?> cleanupFuture;

    private static final ScheduledExecutorService DEFAULT_SCHEDULER;

    static {
        DEFAULT_SCHEDULER = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "routify-global-metrics-cleanup");
            t.setDaemon(true);
            return t;
        });
    }

    /**
     * Uses the global scheduler.
     */
    public MetricsStorageManager() {
        this.scheduler = DEFAULT_SCHEDULER;
    }

    /**
     * Uses a custom scheduler provided by the user.
     * Note: The caller is responsible for shutting down this scheduler.
     */
    public MetricsStorageManager(ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * Retrieves or creates a metrics instance for the specified vendor model ID.
     * <p>
     * If an instance already exists in the storage, it will be returned.
     * Otherwise, a new {@link LocalVendorMetrics} instance will be created,
     * stored, and then returned.
     * </p>
     *
     * @param vendorModelId The vendor model ID (must not be null)
     * @return The existing or newly created metrics instance (never null)
     */
    public LocalVendorMetrics getOrCreate(String vendorModelId) {
        return metricsMap.computeIfAbsent(vendorModelId, LocalVendorMetrics::new);
    }

    /**
     * Retrieves a metrics instance for the specified vendor model ID.
     * <p>
     * If the instance is not exists in the storage, it will return null.
     * </p>
     *
     * @param vendorModelId The vendor model ID (must not be null)
     * @return The metrics instance or null (if not exists)
     */
    public LocalVendorMetrics get(String vendorModelId) {
        return metricsMap.get(vendorModelId);
    }

    /**
     * Remove the metrics instance for the specified vendor model ID.
     * @param vendorModelId The vendor model ID (must not be null)
     */
    public void remove(String vendorModelId) {
        metricsMap.remove(vendorModelId);
    }

    /**
     * Removes all metrics instances that exceed the idle threshold.
     *
     * @param maxIdleMillis The maximum allowed idle time in milliseconds.
     * @return The number of removed metrics instances.
     */
    public int removeExpired(long maxIdleMillis) {
        long now = System.currentTimeMillis();
        AtomicInteger count = new AtomicInteger(0);
        metricsMap.entrySet().removeIf(entry -> {
            LocalVendorMetrics metrics = entry.getValue();
            if (now - metrics.getLastUsedAt() > maxIdleMillis) {
                count.incrementAndGet();
                return true;
            }
            return false;
        });
        return count.get();
    }

    /**
     * Starts the auto cleanup task to remove metrics instances that expired.
     *
     * @param initialDelay The time to delay first execution
     * @param period The period between successive executions.
     * @param unit The time unit of the initialDelay and period parameters.
     * @param maxIdleMillis The maximum allowed idle time in milliseconds.
     */
    public synchronized void startAutoCleanup(long initialDelay, long period, TimeUnit unit, long maxIdleMillis) {
        stopAutoCleanup();
        this.cleanupFuture = scheduler.scheduleAtFixedRate(() -> {
            try {
                int count = removeExpired(maxIdleMillis);
                log.debug("Auto-cleanup task removed {} expired metrics instances.", count);
            } catch (Exception e) {
                log.error("Failed to execute metrics auto-cleanup task", e);
            }
        }, initialDelay, period, unit);
    }

    public synchronized void stopAutoCleanup() {
        if (cleanupFuture != null && !cleanupFuture.isDone()) {
            cleanupFuture.cancel(false); // 不中断正在执行的任务
        }
    }
}
