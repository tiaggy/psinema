package org.psi.psinema.domain.screening.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.psi.psinema.domain.hall.SeatType;

@Data
@AllArgsConstructor
public class SeatAvailabilityDto {
    private Long seatId;
    private int rowNumber;
    private int seatNumber;
    private SeatType type;
    private String status; // AVAILABLE, TEMPORARILY_RESERVED, TAKEN
}
