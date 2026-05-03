package org.psi.psinema.domain.reservation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.psi.psinema.domain.reservation.dto.ReservationRequest;
import org.psi.psinema.domain.reservation.dto.ReservationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class SeatReservationController {

    private final SeatReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> reserve(
            @Valid @RequestBody ReservationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.reserve(request, email));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> release(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        reservationService.release(id, email);
        return ResponseEntity.noContent().build();
    }
}
