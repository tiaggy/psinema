package org.psi.psinema.domain.screening.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ScreeningRequest {
    @NotNull
    private Long movieId;
    @NotNull
    private Long hallId;
    @NotNull
    private LocalDateTime startTime;
    private BigDecimal basePrice;
}
