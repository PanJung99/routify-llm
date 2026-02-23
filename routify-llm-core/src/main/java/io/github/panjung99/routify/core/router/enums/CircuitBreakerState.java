package io.github.panjung99.routify.core.router.enums;

/**
 * Represents the current state of a circuit breaker.
 */
public enum CircuitBreakerState {
    CLOSED,     // Closed(Normal)
    OPEN,       // Open(Breaking)
    HALF_OPEN   // Half open(Try to recover)
}