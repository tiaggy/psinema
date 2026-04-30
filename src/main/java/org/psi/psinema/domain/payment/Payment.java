package org.psi.psinema.domain.payment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.psi.psinema.domain.order.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Diagram 19: Platba (datumPlatby, platba_id, sposob, stav, suma)
 * Operations: spracovat() -> see PaymentGateway.charge, refundovat() -> PaymentGateway.refund
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                          // platba_id

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;   // stav

    /** sposob - payment method (CARD, CASH, ...) */
    private String method;

    private String externalTransactionId;
    private BigDecimal amount;                // suma
    private LocalDateTime processedAt;        // datumPlatby

    @Column(columnDefinition = "TEXT")
    private String gatewayResponse;
}
