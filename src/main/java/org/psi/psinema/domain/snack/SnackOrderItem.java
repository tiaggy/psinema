package org.psi.psinema.domain.snack;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnackOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private SnackOrder snackOrder;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private SnackItem snackItem;

    private int quantity;
    private BigDecimal unitPrice;
}
