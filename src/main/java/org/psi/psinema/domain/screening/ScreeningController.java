package org.psi.psinema.domain.screening;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.psi.psinema.domain.order.OrderService;
import org.psi.psinema.domain.screening.dto.ScreeningRequest;
import org.psi.psinema.domain.screening.dto.SeatAvailabilityDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/screenings")
@RequiredArgsConstructor
public class ScreeningController {

    private final ScreeningService screeningService;
    private final OrderService orderService;

    @GetMapping
    public List<Screening> list(@RequestParam(required = false) Long movieId,
                                @RequestParam(required = false)
                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        return screeningService.findAll(movieId, date);
    }

    @GetMapping("/{id}")
    public Screening get(@PathVariable Long id) {
        return screeningService.findById(id);
    }

    @GetMapping("/{id}/seats")
    public List<SeatAvailabilityDto> seats(@PathVariable Long id) {
        return screeningService.getSeatAvailability(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Screening> create(@Valid @RequestBody ScreeningRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(screeningService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Screening update(@PathVariable Long id, @Valid @RequestBody ScreeningRequest request) {
        return screeningService.update(id, request);
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        orderService.cancelScreening(id);
        return ResponseEntity.noContent().build();
    }
}
