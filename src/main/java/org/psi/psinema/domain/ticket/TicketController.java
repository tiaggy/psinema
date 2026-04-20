package org.psi.psinema.domain.ticket;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.psi.psinema.domain.ticket.dto.ManualCheckRequest;
import org.psi.psinema.domain.ticket.dto.ValidateRequest;
import org.psi.psinema.domain.user.UserRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<Ticket> myTickets(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userRepository.findByEmail(userDetails.getUsername()).orElseThrow().getId();
        return ticketService.findMyTickets(userId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Ticket getById(@PathVariable Long id) {
        return ticketService.findById(id);
    }

    @GetMapping(value = "/{id}/qr", produces = MediaType.IMAGE_PNG_VALUE)
    @PreAuthorize("isAuthenticated()")
    public byte[] getQr(@PathVariable Long id) {
        return ticketService.getQrImage(id);
    }

    @PostMapping("/validate")
    @PreAuthorize("hasRole('ENTRY_CONTROLLER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Ticket> validate(@Valid @RequestBody ValidateRequest request) {
        return ResponseEntity.ok(ticketService.validateQr(request.getQrCodeData()));
    }

    @PostMapping("/manual-check")
    @PreAuthorize("hasRole('ENTRY_CONTROLLER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Ticket> manualCheck(@Valid @RequestBody ManualCheckRequest request) {
        return ResponseEntity.ok(ticketService.manualCheck(request.getTicketId()));
    }
}
