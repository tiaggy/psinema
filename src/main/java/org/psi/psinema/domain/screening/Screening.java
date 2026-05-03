package org.psi.psinema.domain.screening;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.psi.psinema.domain.hall.Hall;
import org.psi.psinema.domain.movie.Movie;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Screening {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Hall hall;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    private BigDecimal basePrice;

    @Builder.Default
    private boolean cancelled = false;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ScreeningStatus status = ScreeningStatus.SCHEDULED;
}
