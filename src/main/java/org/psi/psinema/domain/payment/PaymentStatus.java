package org.psi.psinema.domain.payment;

/**
 * Payment lifecycle states (Diagram 27 - Platba_state_diagram).
 * PENDING -> PROCESSING -> SUCCESS  -> COMPLETED
 *                                  \-> REFUNDED
 *                       \-> FAILED  -> PENDING (retry)
 *                                  \-> CANCELLED
 */
public enum PaymentStatus {
    PENDING,     // platba čaká
    PROCESSING,  // platba je spracovaná
    SUCCESS,     // úspešná
    FAILED,      // neúspešná
    COMPLETED,   // dokončená platba
    CANCELLED,   // zrušená platba
    REFUNDED     // platba je refundovaná
}
