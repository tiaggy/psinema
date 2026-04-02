package org.psi.psinema.domain.reservation.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ReservationRequest {
    @NotNull
    private Long screeningId;
    @NotEmpty
    private List<Long> seatIds;
    private String sessionToken;
}
