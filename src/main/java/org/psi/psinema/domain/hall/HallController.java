package org.psi.psinema.domain.hall;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.psi.psinema.domain.hall.dto.HallRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/halls")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class HallController {

    private final HallService hallService;

    @GetMapping
    public List<Hall> list() {
        return hallService.findAll();
    }

    @GetMapping("/{id}")
    public Hall get(@PathVariable Long id) {
        return hallService.findById(id);
    }

    @GetMapping("/{id}/seats")
    public List<Seat> seats(@PathVariable Long id) {
        return hallService.findSeats(id);
    }

    @PostMapping
    public ResponseEntity<Hall> create(@Valid @RequestBody HallRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hallService.create(request));
    }
}
