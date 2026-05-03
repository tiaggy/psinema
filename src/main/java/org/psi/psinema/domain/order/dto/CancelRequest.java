package org.psi.psinema.domain.order.dto;

import lombok.Data;

import java.util.List;

@Data
public class CancelRequest {
    private List<Long> ticketIds; // null = cancel all
}
