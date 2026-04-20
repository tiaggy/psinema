package org.psi.psinema.domain.snack;

import lombok.RequiredArgsConstructor;
import org.psi.psinema.domain.order.Order;
import org.psi.psinema.domain.order.OrderRepository;
import org.psi.psinema.domain.snack.dto.SnackItemRequest;
import org.psi.psinema.domain.snack.dto.SnackOrderRequest;
import org.psi.psinema.exception.ConflictException;
import org.psi.psinema.exception.ResourceNotFoundException;
import org.psi.psinema.notification.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SnackService {

    private final SnackItemRepository snackItemRepository;
    private final SnackOrderRepository snackOrderRepository;
    private final OrderRepository orderRepository;
    private final NotificationService notificationService;

    public List<SnackItem> findAvailableItems() {
        return snackItemRepository.findByAvailableTrue();
    }

    public SnackItem createItem(SnackItemRequest request) {
        SnackItem item = SnackItem.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .imageUrl(request.getImageUrl())
                .available(request.isAvailable())
                .build();
        return snackItemRepository.save(item);
    }

    public SnackItem updateItem(Long id, SnackItemRequest request) {
        SnackItem item = snackItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Snack item not found: " + id));
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setStockQuantity(request.getStockQuantity());
        item.setImageUrl(request.getImageUrl());
        item.setAvailable(request.isAvailable());
        return snackItemRepository.save(item);
    }

    public void deactivateItem(Long id) {
        SnackItem item = snackItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Snack item not found: " + id));
        item.setAvailable(false);
        snackItemRepository.save(item);
    }

    @Transactional
    public SnackOrder placeOrder(SnackOrderRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // Block if movie already started
        if (order.getScreening().getStartTime().isBefore(LocalDateTime.now())) {
            throw new ConflictException("Cannot order snacks after the movie has started");
        }

        SnackOrder snackOrder = SnackOrder.builder().order(order).build();

        List<SnackOrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (SnackOrderRequest.ItemLine line : request.getItems()) {
            SnackItem snackItem = snackItemRepository.findById(line.getSnackItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Snack item not found: " + line.getSnackItemId()));
            if (!snackItem.isAvailable() || snackItem.getStockQuantity() < line.getQuantity()) {
                throw new ConflictException("Snack item unavailable or insufficient stock: " + snackItem.getName());
            }
            snackItem.setStockQuantity(snackItem.getStockQuantity() - line.getQuantity());
            snackItemRepository.save(snackItem);

            BigDecimal lineTotal = snackItem.getPrice().multiply(BigDecimal.valueOf(line.getQuantity()));
            total = total.add(lineTotal);

            items.add(SnackOrderItem.builder()
                    .snackOrder(snackOrder)
                    .snackItem(snackItem)
                    .quantity(line.getQuantity())
                    .unitPrice(snackItem.getPrice())
                    .build());
        }

        snackOrder.setTotalAmount(total);
        snackOrder.setItems(items);
        SnackOrder saved = snackOrderRepository.save(snackOrder);
        notificationService.notifyBuffetStaff(saved.getId());
        return saved;
    }

    public SnackOrder findById(Long id) {
        return snackOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Snack order not found: " + id));
    }

    public List<SnackOrder> findPending() {
        return snackOrderRepository.findByStatus(SnackOrderStatus.PENDING);
    }

    public SnackOrder updateStatus(Long id, SnackOrderStatus status) {
        SnackOrder order = findById(id);
        order.setStatus(status);
        return snackOrderRepository.save(order);
    }
}
