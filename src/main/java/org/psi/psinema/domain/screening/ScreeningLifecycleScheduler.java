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

/**
 * Drives Screening state transitions per Diagram 33:
 *   Scheduled       -> Open for Sale (system-defined sales open time, here T-24h)
 *   Open for Sale   <-> Sold Out (capacity reached / freed)
 *   Open for Sale   -> In Progress (start time reached)
 *   In Progress     -> Finished (duration elapsed)
 *   any active      -> Canceled (manager-initiated, see OrderService#cancelScreening)
 */
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
                if (s.getStatus() != ScreeningStatus.CANCELED) {
                    s.setStatus(ScreeningStatus.CANCELED);
                    screeningRepository.save(s);
                }
                continue;
            }
            ScreeningStatus current = s.getStatus() != null ? s.getStatus() : ScreeningStatus.SCHEDULED;
            ScreeningStatus next = computeNext(s, now, current);
            if (next != current) {
                s.setStatus(next);
                screeningRepository.save(s);
                if (next == ScreeningStatus.FINISHED) {
                    expireTickets(s.getId());
                }
            }
        }
    }

    private ScreeningStatus computeNext(Screening s, LocalDateTime now, ScreeningStatus current) {
        // Terminal states: Finished and Canceled never transition.
        if (current == ScreeningStatus.FINISHED || current == ScreeningStatus.CANCELED) return current;

        if (now.isAfter(s.getEndTime())) return ScreeningStatus.FINISHED;
        if (!now.isBefore(s.getStartTime())) return ScreeningStatus.IN_PROGRESS;

        long hoursToStart = java.time.Duration.between(now, s.getStartTime()).toHours();
        boolean salesOpen = hoursToStart <= 24;

        if (!salesOpen) return ScreeningStatus.SCHEDULED;

        // Open for Sale <-> Sold Out is decided by capacity vs sold tickets.
        int capacity = s.getHall().getCapacity();
        long sold = countSoldTickets(s.getId());
        return sold >= capacity ? ScreeningStatus.SOLD_OUT : ScreeningStatus.OPEN_FOR_SALE;
    }

    private long countSoldTickets(Long screeningId) {
        return ticketRepository.findByOrderScreeningIdAndStatusIn(
                screeningId, List.of(TicketStatus.VALID, TicketStatus.USED)).size();
    }

    private void expireTickets(Long screeningId) {
        List<Ticket> active = ticketRepository.findByOrderScreeningIdAndStatusIn(
                screeningId, List.of(TicketStatus.VALID, TicketStatus.GENERATED));
        active.forEach(t -> t.setStatus(TicketStatus.EXPIRED));
        ticketRepository.saveAll(active);
    }
}
