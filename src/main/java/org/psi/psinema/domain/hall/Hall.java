package org.psi.psinema.domain.hall;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Diagram 19: Sála (cislo, sala_id, kapacita, typ).
 * Operation sedadlá() is exposed via HallService#findSeats.
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                  // sala_id

    @Column(nullable = false)
    private String name;              // cislo / display name

    private int totalRows;
    private int seatsPerRow;

    /** typ - hall type (STANDARD, IMAX, VIP, ...) per Diagram 19. */
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private HallType type = HallType.STANDARD;

    /** kapacita - derived from grid; not persisted. */
    @Transient
    public int getCapacity() {
        return totalRows * seatsPerRow;
    }
}
