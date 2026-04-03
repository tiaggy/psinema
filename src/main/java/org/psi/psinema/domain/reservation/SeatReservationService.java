package org.psi.psinema.domain.reservation;

import lombok.RequiredArgsConstructor;
import org.psi.psinema.domain.hall.Seat;
import org.psi.psinema.domain.hall.SeatRepository;
import org.psi.psinema.domain.reservation.dto.ReservationRequest;
import org.psi.psinema.domain.reservation.dto.ReservationResponse;
import org.psi.psinema.domain.screening.Screening;
import org.psi.psinema.domain.screening.ScreeningRepository;
import org.psi.psinema.domain.user.User;
import org.psi.psinema.domain.user.UserRepository;
import org.psi.psinema.exception.ConflictException;
import org.psi.psinema.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatReservationService {

    private final SeatReservationRepository reservationRepository;
    private final ScreeningRepository screeningRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;

    @Value("${app.seat-reservation.timeout-minutes}")
    private int timeoutMinutes;

    @Transactional
    public ReservationResponse reserve(ReservationRequest request, String userEmail) {
        Screening screening = screeningRepository.findById(request.getScreeningId())
                .orElseThrow(() -> new ResourceNotFoundException("Screening not found"));

        // Check seats are not already taken or temp-reserved by someone else
        List<SeatReservation> existing = reservationRepository.findActiveForSeats(
                request.getScreeningId(), request.getSeatIds(), LocalDateTime.now());
        if (!existing.isEmpty()) {
            throw new ConflictException("One or more seats are already reserved");
        }

        User user = userEmail != null ? userRepository.findByEmail(userEmail).orElse(null) : null;
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(timeoutMinutes);

        List<SeatReservation> reservations = new ArrayList<>();
        for (Long seatId : request.getSeatIds()) {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new ResourceNotFoundException("Seat not found: " + seatId));
            reservations.add(SeatReservation.builder()
                    .screening(screening)
                    .seat(seat)
                    .user(user)
                    .sessionToken(request.getSessionToken())
                    .expiresAt(expiresAt)
                    .build());
        }

        List<Long> ids = reservationRepository.saveAll(reservations).stream().map(SeatReservation::getId).toList();
        return new ReservationResponse(ids, expiresAt);
    }

    @Transactional
    public void release(Long reservationId, String userEmail) {
        SeatReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
        reservation.setReleased(true);
        reservationRepository.save(reservation);
    }

    @Transactional
    public void releaseExpired() {
        List<SeatReservation> expired = reservationRepository.findExpired(LocalDateTime.now());
        expired.forEach(r -> r.setReleased(true));
        reservationRepository.saveAll(expired);
    }
}
