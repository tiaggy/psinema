package org.psi.psinema.domain.order.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.psi.psinema.domain.ticket.TicketType;

import java.util.List;

@Data
public class OrderRequest {
    @NotNull
    private Long screeningId;
    @NotEmpty
    private List<Long> reservationIds;
    private List<TicketType> ticketTypes; // parallel to reservationIds, defaults to NORMAL
    private String paymentToken;
    /** sposob platby per Diagram 19 (CARD, CASH, ...). Defaults to CARD if null. */
    private String paymentMethod;
}
