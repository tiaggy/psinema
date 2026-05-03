package org.psi.psinema.domain.ticket;

/**
 * Ticket lifecycle states (Diagram 28 - Vstupenka_state_diagram).
 * GENERATED -> VALID -> USED
 *                   \-> CANCELLED
 *                   \-> EXPIRED
 */
public enum TicketStatus {
    GENERATED,  // vygenerovaná
    VALID,      // platná
    USED,       // použitá
    CANCELLED,  // stornovaná
    EXPIRED     // expirovaná
}
