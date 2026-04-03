package org.psi.psinema.domain.ticket;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.psi.psinema.domain.hall.Seat;
import org.psi.psinema.domain.order.Order;

import java.math.BigDecimal;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Seat seat;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TicketType type = TicketType.NORMAL;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TicketStatus status = TicketStatus.ACTIVE;

    @Column(unique = true, nullable = false)
    private String qrCodeData;

    @Lob
    @Column(columnDefinition = "bytea")
    private byte[] qrCodeImage;

    private BigDecimal price;
}
