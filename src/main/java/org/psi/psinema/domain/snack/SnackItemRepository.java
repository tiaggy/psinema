package org.psi.psinema.domain.snack;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SnackItemRepository extends JpaRepository<SnackItem, Long> {
    List<SnackItem> findByAvailableTrue();
}
