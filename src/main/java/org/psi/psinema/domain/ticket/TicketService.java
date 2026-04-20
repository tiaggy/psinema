package org.psi.psinema.domain.ticket;

import lombok.RequiredArgsConstructor;
import org.psi.psinema.exception.ConflictException;
import org.psi.psinema.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Transactional
    public Ticket validateQr(String qrCodeData) {
        Ticket ticket = ticketRepository.findByQrCodeData(qrCodeData)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found for QR code"));
        return markAsUsed(ticket);
    }

    @Transactional
    public Ticket manualCheck(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + ticketId));
        return markAsUsed(ticket);
    }

    private Ticket markAsUsed(Ticket ticket) {
        if (ticket.getStatus() == TicketStatus.USED) {
            throw new ConflictException("Ticket already used");
        }
        if (ticket.getStatus() == TicketStatus.CANCELLED) {
            throw new ConflictException("Ticket is cancelled");
        }
        if (ticket.getStatus() == TicketStatus.EXPIRED) {
            throw new ConflictException("Ticket is expired");
        }
        ticket.setStatus(TicketStatus.USED);
        return ticketRepository.save(ticket);
    }
}
