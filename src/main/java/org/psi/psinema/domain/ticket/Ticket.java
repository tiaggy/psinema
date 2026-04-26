package org.psi.psinema.domain.ticket;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
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

    @JdbcTypeCode(SqlTypes.VARBINARY)
    private byte[] qrCodeImage;

    private BigDecimal price;
}
