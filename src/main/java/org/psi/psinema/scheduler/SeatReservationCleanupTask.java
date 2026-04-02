package org.psi.psinema.scheduler;

import lombok.RequiredArgsConstructor;
import org.psi.psinema.domain.reservation.SeatReservationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeatReservationCleanupTask {

    private final SeatReservationService reservationService;

    @Scheduled(fixedRate = 60_000)
    public void releaseExpired() {
        reservationService.releaseExpired();
    }
}
