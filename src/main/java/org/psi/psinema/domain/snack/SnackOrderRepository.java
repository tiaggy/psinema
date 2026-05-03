package org.psi.psinema.domain.snack;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SnackOrderRepository extends JpaRepository<SnackOrder, Long> {
    List<SnackOrder> findByStatus(SnackOrderStatus status);
    List<SnackOrder> findByOrderId(Long orderId);
}
