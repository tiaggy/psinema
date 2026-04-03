package org.psi.psinema.domain.snack.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SnackItemRequest {
    @NotBlank
    private String name;
    private String description;
    private BigDecimal price;
    @Min(0)
    private int stockQuantity;
    private String imageUrl;
    private boolean available = true;
}
