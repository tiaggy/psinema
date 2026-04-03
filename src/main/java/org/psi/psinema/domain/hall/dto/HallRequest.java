package org.psi.psinema.domain.hall.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HallRequest {
    @NotBlank
    private String name;
    @Min(1)
    private int totalRows;
    @Min(1)
    private int seatsPerRow;
}
