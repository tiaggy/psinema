package org.psi.psinema.domain.ticket.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ValidateRequest {
    @NotBlank
    private String qrCodeData;
}
