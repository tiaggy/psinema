package org.psi.psinema.domain.order;

/**
 * Order lifecycle states (Diagram 23 - Objednávka_state_diagram).
 * CREATED -> AWAITING_PAYMENT -> PAID -> ACTIVE -> FINISHED
 *                            \-> CANCELLED              \-> STORNOVANA -> REFUNDED
 */
public enum OrderStatus {
    CREATED,            // Vytvorená
    AWAITING_PAYMENT,   // čakajúca na platbu
    PAID,               // zaplatená
    ACTIVE,             // aktívna
    FINISHED,           // dokončená
    CANCELLED,          // zrušená
    STORNOVANA,         // stornovaná
    REFUNDED            // refundovaná
}
