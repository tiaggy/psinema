package org.psi.psinema.domain.snack;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.psi.psinema.domain.snack.dto.SnackItemRequest;
import org.psi.psinema.domain.snack.dto.SnackOrderRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SnackController {

    private final SnackService snackService;

    // Snack Items
    @GetMapping("/api/snacks")
    public List<SnackItem> listItems() {
        return snackService.findAvailableItems();
    }

    @PostMapping("/api/snacks")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SnackItem> createItem(@Valid @RequestBody SnackItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(snackService.createItem(request));
    }

    @PutMapping("/api/snacks/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public SnackItem updateItem(@PathVariable Long id, @Valid @RequestBody SnackItemRequest request) {
        return snackService.updateItem(id, request);
    }

    @DeleteMapping("/api/snacks/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateItem(@PathVariable Long id) {
        snackService.deactivateItem(id);
        return ResponseEntity.noContent().build();
    }

    // Snack Orders
    @PostMapping("/api/snack-orders")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<SnackOrder> placeOrder(@Valid @RequestBody SnackOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(snackService.placeOrder(request));
    }

    @GetMapping("/api/snack-orders/{id}")
    @PreAuthorize("isAuthenticated()")
    public SnackOrder getOrder(@PathVariable Long id) {
        return snackService.findById(id);
    }

    @GetMapping("/api/snack-orders/pending")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public List<SnackOrder> pending() {
        return snackService.findPending();
    }

    @PutMapping("/api/snack-orders/{id}/status")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public SnackOrder updateStatus(@PathVariable Long id, @RequestParam SnackOrderStatus status) {
        return snackService.updateStatus(id, status);
    }
}
