package io.github.panjung99.routify.core.router.strategy;

import io.github.panjung99.routify.core.client.RoutingContext;
import io.github.panjung99.routify.core.model.entity.VendorModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RoundRobinVendorRoutingStrategy following AAA (Arrange-Act-Assert) principle.
 */
@ExtendWith(MockitoExtension.class)
class RoundRobinVendorRoutingStrategyTest {

    private RoundRobinVendorRoutingStrategy routingStrategy;

    @Mock
    private RoutingContext routingContext;

    private VendorModel vendorModel1;
    private VendorModel vendorModel2;
    private VendorModel vendorModel3;

    @BeforeEach
    void setUp() {
        routingStrategy = new RoundRobinVendorRoutingStrategy();
        
        // Arrange: Create test vendor models
        vendorModel1 = new VendorModel();
        vendorModel1.setVendorModelId("vendor-1");
        vendorModel1.setName("Model 1");
        vendorModel1.setVendorId("vendor-a");
        vendorModel1.setWeight(1);

        vendorModel2 = new VendorModel();
        vendorModel2.setVendorModelId("vendor-2");
        vendorModel2.setName("Model 2");
        vendorModel2.setVendorId("vendor-b");
        vendorModel2.setWeight(2);

        vendorModel3 = new VendorModel();
        vendorModel3.setVendorModelId("vendor-3");
        vendorModel3.setName("Model 3");
        vendorModel3.setVendorId("vendor-c");
        vendorModel3.setWeight(3);

    }

    @Test
    void doRoute_shouldReturnNull_whenVendorModelsListIsEmpty() {
        // Arrange
        List<VendorModel> emptyVendorModels = Collections.emptyList();
        
        // Act
        VendorModel result = routingStrategy.doRoute(emptyVendorModels, routingContext);
        
        // Assert
        assertNull(result, "Should return null when vendor models list is empty");
    }

    @Test
    void doRoute_shouldReturnFirstVendorModel_whenCalledFirstTime() {
        // Arrange
        List<VendorModel> singleVendorModelList = Collections.singletonList(vendorModel1);
        
        // Act
        VendorModel result = routingStrategy.doRoute(singleVendorModelList, routingContext);
        
        // Assert
        assertNotNull(result, "Should not return null when vendor models list is not empty");
        assertEquals(vendorModel1, result, "Should return the first vendor model on first call");
        assertEquals("vendor-1", result.getVendorModelId(), "Vendor model ID should match");
    }

    @Test
    void doRoute_shouldReturnVendorModelsInRoundRobinOrder_whenMultipleVendorModels() {
        // Arrange
        List<VendorModel> vendorModels = Arrays.asList(vendorModel1, vendorModel2, vendorModel3);
        
        // Act & Assert: First call
        VendorModel result1 = routingStrategy.doRoute(vendorModels, routingContext);
        assertEquals(vendorModel1, result1, "First call should return first vendor model");
        
        // Act & Assert: Second call
        VendorModel result2 = routingStrategy.doRoute(vendorModels, routingContext);
        assertEquals(vendorModel2, result2, "Second call should return second vendor model");
        
        // Act & Assert: Third call
        VendorModel result3 = routingStrategy.doRoute(vendorModels, routingContext);
        assertEquals(vendorModel3, result3, "Third call should return third vendor model");
        
        // Act & Assert: Fourth call (should wrap around to first)
        VendorModel result4 = routingStrategy.doRoute(vendorModels, routingContext);
        assertEquals(vendorModel1, result4, "Fourth call should wrap around to first vendor model");
        
        // Act & Assert: Fifth call
        VendorModel result5 = routingStrategy.doRoute(vendorModels, routingContext);
        assertEquals(vendorModel2, result5, "Fifth call should return second vendor model");
    }

    @Test
    void doRoute_shouldHandleSingleVendorModelCorrectly() {
        // Arrange
        List<VendorModel> singleVendorModelList = Collections.singletonList(vendorModel1);
        
        // Act & Assert: Multiple calls should always return the same vendor model
        VendorModel result1 = routingStrategy.doRoute(singleVendorModelList, routingContext);
        assertEquals(vendorModel1, result1, "First call should return the single vendor model");
        
        VendorModel result2 = routingStrategy.doRoute(singleVendorModelList, routingContext);
        assertEquals(vendorModel1, result2, "Second call should return the same vendor model");
        
        VendorModel result3 = routingStrategy.doRoute(singleVendorModelList, routingContext);
        assertEquals(vendorModel1, result3, "Third call should return the same vendor model");
    }

    @Test
    void doRoute_shouldBeThreadSafe_whenCalledConcurrently() throws InterruptedException {
        // Arrange
        List<VendorModel> vendorModels = Arrays.asList(vendorModel1, vendorModel2, vendorModel3);
        int numberOfThreads = 10;
        int callsPerThread = 100;
        
        // Act: Create multiple threads that call doRoute concurrently
        Thread[] threads = new Thread[numberOfThreads];
        VendorModel[][] results = new VendorModel[numberOfThreads][callsPerThread];
        
        for (int i = 0; i < numberOfThreads; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < callsPerThread; j++) {
                    results[threadIndex][j] = routingStrategy.doRoute(vendorModels, routingContext);
                }
            });
        }
        
        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }
        
        // Assert: Verify that all calls returned valid vendor models
        for (int i = 0; i < numberOfThreads; i++) {
            for (int j = 0; j < callsPerThread; j++) {
                assertNotNull(results[i][j], "All concurrent calls should return non-null vendor models");
                assertTrue(vendorModels.contains(results[i][j]), 
                    "All concurrent calls should return valid vendor models from the list");
            }
        }
    }

    @Test
    void doRoute_shouldNotAffectOtherInstances_whenMultipleStrategyInstancesExist() {
        // Arrange
        List<VendorModel> vendorModels = Arrays.asList(vendorModel1, vendorModel2);
        RoundRobinVendorRoutingStrategy strategy1 = new RoundRobinVendorRoutingStrategy();
        RoundRobinVendorRoutingStrategy strategy2 = new RoundRobinVendorRoutingStrategy();
        
        // Act: Call doRoute on first strategy instance
        VendorModel result1_1 = strategy1.doRoute(vendorModels, routingContext);
        VendorModel result1_2 = strategy1.doRoute(vendorModels, routingContext);
        
        // Act: Call doRoute on second strategy instance
        VendorModel result2_1 = strategy2.doRoute(vendorModels, routingContext);
        VendorModel result2_2 = strategy2.doRoute(vendorModels, routingContext);
        
        // Assert: Each strategy instance should maintain its own state
        assertEquals(vendorModel1, result1_1, "First call on first strategy should return first vendor model");
        assertEquals(vendorModel2, result1_2, "Second call on first strategy should return second vendor model");
        
        assertEquals(vendorModel1, result2_1, "First call on second strategy should return first vendor model");
        assertEquals(vendorModel2, result2_2, "Second call on second strategy should return second vendor model");
    }


    @Test
    void doRoute_shouldMaintainConsistentRoundRobinOrder_acrossMultipleCalls() {
        // Arrange
        List<VendorModel> vendorModels = Arrays.asList(vendorModel1, vendorModel2, vendorModel3);
        
        // Act: Make multiple calls and track the order
        VendorModel[] results = new VendorModel[9]; // 3 full cycles
        for (int i = 0; i < 9; i++) {
            results[i] = routingStrategy.doRoute(vendorModels, routingContext);
        }
        
        // Assert: Verify the round-robin pattern
        for (int i = 0; i < 9; i++) {
            VendorModel expected = vendorModels.get(i % vendorModels.size());
            assertEquals(expected, results[i], 
                String.format("Call %d should return vendor model at index %d", i + 1, i % vendorModels.size()));
        }
    }

    @Test
    void doRoute_shouldHandleVendorModelsListChangesCorrectly_whenListSizeDecreases() {
        // Arrange
        List<VendorModel> initialVendorModels = Arrays.asList(vendorModel1, vendorModel2);
        List<VendorModel> smallerVendorModels = Arrays.asList(vendorModel3);
        
        // Act: First call with initial list (returns vendorModel1)
        VendorModel result1 = routingStrategy.doRoute(initialVendorModels, routingContext);
        
        // Act: Second call with smaller list - should handle index wrap-around correctly
        VendorModel result2 = routingStrategy.doRoute(smallerVendorModels, routingContext);
        
        // Assert
        assertEquals(vendorModel1, result1, "First call should return first vendor model from initial list");
        assertEquals(vendorModel3, result2, "Second call should return vendor model from smaller list (index wrapped around)");
        
        // Act: Third call with smaller list (should still return the same vendor model since list has only one element)
        VendorModel result3 = routingStrategy.doRoute(smallerVendorModels, routingContext);
        assertEquals(vendorModel3, result3, "Third call should return the same vendor model from smaller list");
    }
    
    @Test
    void doRoute_shouldHandleVendorModelsListChangesCorrectly_whenListSizeIncreases() {
        // Arrange
        List<VendorModel> initialVendorModels = Arrays.asList(vendorModel1);
        List<VendorModel> largerVendorModels = Arrays.asList(vendorModel1, vendorModel2, vendorModel3);
        
        // Act: First call with initial list (returns vendorModel1)
        VendorModel result1 = routingStrategy.doRoute(initialVendorModels, routingContext);
        
        // Act: Second call with larger list - should handle index correctly
        VendorModel result2 = routingStrategy.doRoute(largerVendorModels, routingContext);
        
        // Assert
        assertEquals(vendorModel1, result1, "First call should return vendor model from initial list");
        assertEquals(vendorModel2, result2, "Second call should return second vendor model from larger list");
        
        // Act: Third call with larger list
        VendorModel result3 = routingStrategy.doRoute(largerVendorModels, routingContext);
        assertEquals(vendorModel3, result3, "Third call should return third vendor model from larger list");
    }
}