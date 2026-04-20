package org.psi.psinema.domain.screening;

import lombok.RequiredArgsConstructor;
import org.psi.psinema.domain.ticket.Ticket;
import org.psi.psinema.domain.ticket.TicketRepository;
import org.psi.psinema.domain.ticket.TicketStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ScreeningLifecycleScheduler {

    private final ScreeningRepository screeningRepository;
    private final TicketRepository ticketRepository;

    @Scheduled(fixedDelay = 60_000L)
    @Transactional
    public void tick() {
        LocalDateTime now = LocalDateTime.now();
        for (Screening s : screeningRepository.findAll()) {
            if (s.isCancelled()) {
                s.setStatus(ScreeningStatus.CANCELED);
                continue;
            }
            ScreeningStatus current = s.getStatus() != null ? s.getStatus() : ScreeningStatus.SCHEDULED;
            ScreeningStatus next = computeNext(s, now);
            if (next != current) {
                s.setStatus(next);
                if (next == ScreeningStatus.FINISHED) {
                    expireTickets(s.getId());
                }
            }
        }
    }

    private ScreeningStatus computeNext(Screening s, LocalDateTime now) {
        if (now.isAfter(s.getEndTime())) return ScreeningStatus.FINISHED;
        if (!now.isBefore(s.getStartTime())) return ScreeningStatus.IN_PROGRESS;
        long hoursToStart = java.time.Duration.between(now, s.getStartTime()).toHours();
        if (hoursToStart <= 24) return ScreeningStatus.OPEN_FOR_SALE;
        return ScreeningStatus.SCHEDULED;
    }

    private void expireTickets(Long screeningId) {
        List<Ticket> active = ticketRepository.findByOrderScreeningIdAndStatusIn(
                screeningId, List.of(TicketStatus.ACTIVE, TicketStatus.VALID, TicketStatus.GENERATED));
        active.forEach(t -> t.setStatus(TicketStatus.EXPIRED));
        ticketRepository.saveAll(active);
    }
}
