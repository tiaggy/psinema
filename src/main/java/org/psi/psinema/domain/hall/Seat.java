package org.psi.psinema.domain.hall;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Hall hall;

    private int rowNumber;
    private int seatNumber;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SeatType type = SeatType.STANDARD;
}
