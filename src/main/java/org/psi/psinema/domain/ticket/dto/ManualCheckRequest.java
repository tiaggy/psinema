package org.psi.psinema.domain.ticket.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ManualCheckRequest {
    @NotNull
    private Long ticketId;
    private String reason;
}
