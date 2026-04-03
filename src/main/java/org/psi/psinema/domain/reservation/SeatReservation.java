package org.psi.psinema.domain.reservation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.psi.psinema.domain.hall.Seat;
import org.psi.psinema.domain.screening.Screening;
import org.psi.psinema.domain.user.User;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Screening screening;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Seat seat;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String sessionToken;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Builder.Default
    private boolean released = false;
}
