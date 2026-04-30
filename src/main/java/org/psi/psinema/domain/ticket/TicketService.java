package org.psi.psinema.domain.ticket;

import lombok.RequiredArgsConstructor;
import org.psi.psinema.exception.ConflictException;
import org.psi.psinema.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implements UC_02 (kontrola vstupeniek pri vstupe).
 * Diagram 21/28: VALID -> USED. Other states are rejected with explicit reason.
 */
@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    public Ticket findById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + id));
    }

    public byte[] getQrImage(Long id) {
        return findById(id).getQrCodeImage();
    }

    public List<Ticket> findMyTickets(Long userId) {
        return ticketRepository.findByUserId(userId);
    }

    /** UC_02 main success scenario: dek�duj QR -> overPlatnost -> oznacAkoPouzit�. */
    @Transactional
    public Ticket validateQr(String qrCodeData) {
        Ticket ticket = ticketRepository.findByQrCodeData(qrCodeData)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found for QR code"));
        return markAsUsed(ticket);
    }

    /** UC_02 extension: manu�lna kontrola vstupeniek (n�dzov� scen�r). */
    @Transactional
    public Ticket manualCheck(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + ticketId));
        return markAsUsed(ticket);
    }

    private Ticket markAsUsed(Ticket ticket) {
        switch (ticket.getStatus()) {
            case USED      -> throw new ConflictException("Ticket already used");
            case CANCELLED -> throw new ConflictException("Ticket is cancelled");
            case EXPIRED   -> throw new ConflictException("Ticket is expired");
            case GENERATED -> throw new ConflictException("Ticket is not yet valid (payment pending)");
            case VALID     -> ticket.setStatus(TicketStatus.USED);
        }
        return ticketRepository.save(ticket);
    }
}
