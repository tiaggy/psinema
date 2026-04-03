package org.psi.psinema.domain.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SeatReservationRepository extends JpaRepository<SeatReservation, Long> {

    @Query("SELECT r FROM SeatReservation r WHERE r.screening.id = :screeningId " +
           "AND r.released = false AND r.expiresAt > :now")
    List<SeatReservation> findActiveReservations(@Param("screeningId") Long screeningId,
                                                  @Param("now") LocalDateTime now);

    @Query("SELECT r FROM SeatReservation r WHERE r.released = false AND r.expiresAt <= :now")
    List<SeatReservation> findExpired(@Param("now") LocalDateTime now);

    @Query("SELECT r FROM SeatReservation r WHERE r.screening.id = :screeningId " +
           "AND r.seat.id IN :seatIds " +
           "AND r.released = false AND r.expiresAt > :now")
    List<SeatReservation> findActiveForSeats(@Param("screeningId") Long screeningId,
                                              @Param("seatIds") List<Long> seatIds,
                                              @Param("now") LocalDateTime now);

    List<SeatReservation> findByUserIdAndReleasedFalse(Long userId);
}
