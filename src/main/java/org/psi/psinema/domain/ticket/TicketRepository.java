package org.psi.psinema.domain.ticket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByOrderId(Long orderId);

    @Query("SELECT t FROM Ticket t WHERE t.order.screening.id = :screeningId AND t.status IN :statuses")
    List<Ticket> findByOrderScreeningIdAndStatusIn(@Param("screeningId") Long screeningId,
                                                    @Param("statuses") List<TicketStatus> statuses);

    Optional<Ticket> findByQrCodeData(String qrCodeData);

    @Query("SELECT t FROM Ticket t WHERE t.order.user.id = :userId")
    List<Ticket> findByUserId(@Param("userId") Long userId);
}
