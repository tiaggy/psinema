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

        if (ticket.getStatus() == TicketStatus.USED) {
            throw new ConflictException("Ticket already used");
        }
        if (ticket.getStatus() == TicketStatus.CANCELLED) {
            throw new ConflictException("Ticket is cancelled");
        }

        ticket.setStatus(TicketStatus.USED);
        return ticketRepository.save(ticket);
    }
}
