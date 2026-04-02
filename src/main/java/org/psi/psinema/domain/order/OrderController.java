package org.psi.psinema.domain.order;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.psi.psinema.domain.order.dto.CancelRequest;
import org.psi.psinema.domain.order.dto.OrderRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Order> create(@Valid @RequestBody OrderRequest request,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.confirmOrder(request, userDetails.getUsername()));
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<Order> myOrders(@AuthenticationPrincipal UserDetails userDetails) {
        return orderService.findMyOrders(userDetails.getUsername());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Order getById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return orderService.findById(id, userDetails.getUsername());
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> cancel(@PathVariable Long id,
                                       @RequestBody(required = false) CancelRequest request,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        orderService.cancelOrder(id, request, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Order> all() {
        return orderService.findAll();
    }
}
