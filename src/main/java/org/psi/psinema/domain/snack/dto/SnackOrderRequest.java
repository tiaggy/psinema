package org.psi.psinema.domain.snack.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SnackOrderRequest {
    @NotNull
    private Long orderId;
    @NotEmpty
    private List<ItemLine> items;

    @Data
    public static class ItemLine {
        @NotNull
        private Long snackItemId;
        @Min(1)
        private int quantity;
    }
}
