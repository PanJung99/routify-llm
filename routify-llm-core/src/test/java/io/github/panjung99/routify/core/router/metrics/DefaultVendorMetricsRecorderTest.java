package io.github.panjung99.routify.core.router.metrics;

import io.github.panjung99.routify.core.router.enums.CircuitBreakerState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultVendorMetricsRecorderTest {

    private DefaultVendorMetricsRecorder recorder;
    private LocalVendorMetrics metrics;
    
    private static final double ALPHA = 0.1;
    private static final String VENDOR_ID = "test-vendor";
    private static final double EPSILON = 0.0001; // 浮点数比较精度
    
    @BeforeEach
    void setUp() {
        recorder = new DefaultVendorMetricsRecorder(ALPHA);
        metrics = new LocalVendorMetrics(VENDOR_ID);
    }
    
    @Test
    void record_shouldThrowException_whenMetricsIsNull() {
        // Arrange
        long latency = 100L;
        boolean success = true;
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
            () -> recorder.record(null, latency, success));
    }
    
    @Test
    void record_shouldUpdateMetricsWithSuccess_whenLatencyIsPositive() {
        // Arrange
        long latency = 100L;
        boolean success = true;
        double initialAvgLatency = 50.0;
        double initialSuccessRate = 0.9;
        
        // 设置初始值
        metrics.setAvgLatency(initialAvgLatency);
        metrics.setSuccessRate(initialSuccessRate);
        
        // Act
        recorder.record(metrics, latency, success);
        
        // Assert
        double expectedAvgLatency = ALPHA * latency + (1 - ALPHA) * initialAvgLatency;
        double expectedSuccessRate = ALPHA * 1.0 + (1 - ALPHA) * initialSuccessRate;
        
        assertEquals(expectedAvgLatency, metrics.getAvgLatency(), EPSILON);
        assertEquals(expectedSuccessRate, metrics.getSuccessRate(), EPSILON);
        assertEquals(1, metrics.getTotalRequests().get());
        assertEquals(1, metrics.getTotalSuccess().get());
    }
    
    @Test
    void record_shouldUpdateMetricsWithFailure_whenSuccessIsFalse() {
        // Arrange
        long latency = 200L;
        boolean success = false;
        double initialAvgLatency = 100.0;
        double initialSuccessRate = 0.8;
        
        // 设置初始值
        metrics.setAvgLatency(initialAvgLatency);
        metrics.setSuccessRate(initialSuccessRate);
        
        // Act
        recorder.record(metrics, latency, success);
        
        // Assert
        double expectedAvgLatency = ALPHA * latency + (1 - ALPHA) * initialAvgLatency;
        double expectedSuccessRate = ALPHA * 0.0 + (1 - ALPHA) * initialSuccessRate;
        
        assertEquals(expectedAvgLatency, metrics.getAvgLatency(), EPSILON);
        assertEquals(expectedSuccessRate, metrics.getSuccessRate(), EPSILON);
        assertEquals(1, metrics.getTotalRequests().get());
        assertEquals(0, metrics.getTotalSuccess().get());
    }
    
    @Test
    void record_shouldUseMinimumLatency_whenLatencyIsZero() {
        // Arrange
        long latency = 0L;
        boolean success = true;
        double initialAvgLatency = 50.0;
        
        // 设置初始值
        metrics.setAvgLatency(initialAvgLatency);
        
        // Act
        recorder.record(metrics, latency, success);
        
        // Assert
        double expectedAvgLatency = ALPHA * 1L + (1 - ALPHA) * initialAvgLatency;
        assertEquals(expectedAvgLatency, metrics.getAvgLatency(), EPSILON);
    }
    
    @Test
    void record_shouldUseMinimumLatency_whenLatencyIsNegative() {
        // Arrange
        long latency = -50L;
        boolean success = true;
        double initialAvgLatency = 50.0;
        
        // 设置初始值
        metrics.setAvgLatency(initialAvgLatency);
        
        // Act
        recorder.record(metrics, latency, success);
        
        // Assert
        double expectedAvgLatency = ALPHA * 1L + (1 - ALPHA) * initialAvgLatency;
        assertEquals(expectedAvgLatency, metrics.getAvgLatency(), EPSILON);
    }
    
    @Test
    void incrementConcurrency_shouldIncrementConcurrentRequests() {
        // Arrange
        int initialConcurrency = 5;
        metrics.getConcurrentRequests().set(initialConcurrency);
        
        // Act
        recorder.incrementConcurrency(metrics);
        
        // Assert
        assertEquals(initialConcurrency + 1, metrics.getConcurrentRequests().get());
    }
    
    @Test
    void decrementConcurrency_shouldDecrementConcurrentRequests() {
        // Arrange
        int initialConcurrency = 5;
        metrics.getConcurrentRequests().set(initialConcurrency);
        
        // Act
        recorder.decrementConcurrency(metrics);
        
        // Assert
        assertEquals(initialConcurrency - 1, metrics.getConcurrentRequests().get());
    }
    
    @Test
    void updateHealth_shouldSetHealthStatus() {
        // Arrange
        boolean healthy = false;
        
        // Act
        recorder.updateHealth(metrics, healthy);
        
        // Assert
        assertEquals(healthy, metrics.isHealthy());
    }
    
    @Test
    void updateCircuitBreakerState_shouldSetCircuitBreakerState() {
        // Arrange
        CircuitBreakerState state = CircuitBreakerState.OPEN;
        
        // Act
        recorder.updateCircuitBreakerState(metrics, state);
        
        // Assert
        assertEquals(state, metrics.getCircuitBreakerState());
    }
    
    @Test
    void setManuallyDisabled_shouldSetManualDisabledFlag() {
        // Arrange
        boolean disabled = true;
        
        // Act
        recorder.setManuallyDisabled(metrics, disabled);
        
        // Assert
        assertEquals(disabled, metrics.isManuallyDisabled());
    }
    
    @Test
    void record_shouldHandleMultipleConcurrentUpdates() throws InterruptedException {
        // Arrange
        int numThreads = 10;
        int iterations = 100;
        
        // Act
        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < iterations; j++) {
                    recorder.record(metrics, 100L, true);
                }
            });
            threads[i].start();
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        // Assert
        long expectedTotalRequests = numThreads * iterations;
        long expectedTotalSuccess = numThreads * iterations;
        
        assertEquals(expectedTotalRequests, metrics.getTotalRequests().get());
        assertEquals(expectedTotalSuccess, metrics.getTotalSuccess().get());
    }
    
    @Test
    void record_shouldCalculateEWMACorrectly() {
        // Arrange
        long latency1 = 100L;
        long latency2 = 200L;
        boolean success1 = true;
        boolean success2 = false;
        
        // Act - First record
        recorder.record(metrics, latency1, success1);
        
        // Assert - First record
        double expectedAvgLatency1 = ALPHA * latency1 + (1 - ALPHA) * 0.0;
        double expectedSuccessRate1 = ALPHA * 1.0 + (1 - ALPHA) * 1.0;
        
        assertEquals(expectedAvgLatency1, metrics.getAvgLatency(), EPSILON);
        assertEquals(expectedSuccessRate1, metrics.getSuccessRate(), EPSILON);
        assertEquals(1, metrics.getTotalRequests().get());
        assertEquals(1, metrics.getTotalSuccess().get());
        
        // Act - Second record
        recorder.record(metrics, latency2, success2);
        
        // Assert - Second record
        double expectedAvgLatency2 = ALPHA * latency2 + (1 - ALPHA) * expectedAvgLatency1;
        double expectedSuccessRate2 = ALPHA * 0.0 + (1 - ALPHA) * expectedSuccessRate1;
        
        assertEquals(expectedAvgLatency2, metrics.getAvgLatency(), EPSILON);
        assertEquals(expectedSuccessRate2, metrics.getSuccessRate(), EPSILON);
        assertEquals(2, metrics.getTotalRequests().get());
        assertEquals(1, metrics.getTotalSuccess().get());
    }
    
    @Test
    void record_shouldInitializeAtomicFieldsCorrectly() {
        // Arrange
        long latency = 150L;
        boolean success = true;
        
        // Act
        recorder.record(metrics, latency, success);
        
        // Assert - 验证Atomic字段被正确初始化
        assertNotNull(metrics.getTotalRequests());
        assertNotNull(metrics.getTotalSuccess());
        assertNotNull(metrics.getConcurrentRequests());
        
        assertEquals(1, metrics.getTotalRequests().get());
        assertEquals(1, metrics.getTotalSuccess().get());
        assertEquals(0, metrics.getConcurrentRequests().get()); // 初始并发数应为0
    }
    
    @Test
    void constructor_shouldAcceptValidAlphaValues() {
        // Arrange & Act & Assert
        assertDoesNotThrow(() -> new DefaultVendorMetricsRecorder(0.0));
        assertDoesNotThrow(() -> new DefaultVendorMetricsRecorder(0.5));
        assertDoesNotThrow(() -> new DefaultVendorMetricsRecorder(1.0));
    }
}