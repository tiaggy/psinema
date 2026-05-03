package org.psi.psinema.domain.order;

import lombok.RequiredArgsConstructor;
import org.psi.psinema.domain.order.dto.CancelRequest;
import org.psi.psinema.domain.order.dto.OrderRequest;
import org.psi.psinema.domain.payment.Payment;
import org.psi.psinema.domain.payment.PaymentGateway;
import org.psi.psinema.domain.payment.PaymentRepository;
import org.psi.psinema.domain.payment.PaymentStatus;
import org.psi.psinema.domain.reservation.SeatReservation;
import org.psi.psinema.domain.reservation.SeatReservationRepository;
import org.psi.psinema.domain.screening.Screening;
import org.psi.psinema.domain.screening.ScreeningRepository;
import org.psi.psinema.domain.ticket.Ticket;
import org.psi.psinema.domain.ticket.TicketRepository;
import org.psi.psinema.domain.ticket.TicketStatus;
import org.psi.psinema.domain.ticket.TicketType;
import org.psi.psinema.domain.user.User;
import org.psi.psinema.domain.user.UserRepository;
import org.psi.psinema.exception.CancellationNotAllowedException;
import org.psi.psinema.exception.ConflictException;
import org.psi.psinema.exception.ResourceNotFoundException;
import org.psi.psinema.notification.NotificationService;
import org.psi.psinema.util.QrCodeGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implements UC_01 (n�kup vstupeniek) and UC_04 (storno + refund�cia).
 * Order lifecycle (Diagram 23): CREATED -> AWAITING_PAYMENT -> PAID -> ACTIVE
 *                                                          \-> CANCELLED
 *                               ACTIVE -> STORNOVANA -> REFUNDED   (UC_04)
 *                               any    -> CANCELLED                (UC_07 cascade)
 * Payment lifecycle (Diagram 27): PENDING -> PROCESSING -> SUCCESS -> COMPLETED | REFUNDED
 *                                                       \-> FAILED  -> CANCELLED
 * Ticket lifecycle (Diagram 28): GENERATED -> VALID -> USED | CANCELLED | EXPIRED
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final TicketRepository ticketRepository;
    private final SeatReservationRepository reservationRepository;
    private final ScreeningRepository screeningRepository;
    private final UserRepository userRepository;
    private final PaymentGateway paymentGateway;
    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;

    @Value("${app.dont-pay:false}")
    private boolean dontPay;

    @Transactional
    public Order confirmOrder(OrderRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Screening screening = screeningRepository.findById(request.getScreeningId())
                .orElseThrow(() -> new ResourceNotFoundException("Screening not found"));

        if (screening.isCancelled()) {
            throw new ConflictException("Screening is cancelled");
        }

        // Fetch and validate reservations
        List<SeatReservation> reservations = request.getReservationIds().stream()
                .map(id -> reservationRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Reservation not found: " + id)))
                .toList();

        LocalDateTime now = LocalDateTime.now();
        for (SeatReservation r : reservations) {
            if (r.isReleased() || r.getExpiresAt().isBefore(now)) {
                throw new ConflictException("Reservation has expired");
            }
        }

        BigDecimal basePrice = screening.getBasePrice() != null ? screening.getBasePrice() : BigDecimal.ZERO;
        BigDecimal total = basePrice.multiply(BigDecimal.valueOf(reservations.size()));

        // (1) Vytvoren� - persist order in CREATED state
        Order order = Order.builder()
                .user(user)
                .screening(screening)
                .status(OrderStatus.CREATED)
                .totalAmount(total)
                .build();
        order = orderRepository.save(order);

        // (2) cakaj�ca na platbu - move to AWAITING_PAYMENT before charging the gateway
        order.setStatus(OrderStatus.AWAITING_PAYMENT);
        order = orderRepository.save(order);

        // (3) Process payment via the external gateway (PaymentGateway = Diagram 18 boundary)
        Payment payment = Payment.builder()
                .order(order)
                .status(PaymentStatus.PROCESSING)
                .method(request.getPaymentMethod() != null ? request.getPaymentMethod() : "CARD")
                .amount(total)
                .processedAt(LocalDateTime.now())
                .build();

        if (!dontPay) {
            PaymentGateway.PaymentResult result = paymentGateway.charge(total, request.getPaymentToken());
            if (!result.success()) {
                // platba zlyhala -> objedn�vka prech�dza do zrušenej, sedadl� uvolnen�
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
                order.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);
                reservations.forEach(r -> r.setReleased(true));
                reservationRepository.saveAll(reservations);
                throw new ConflictException("Payment failed: " + result.message());
            }
            payment.setExternalTransactionId(result.transactionId());
            payment.setStatus(PaymentStatus.SUCCESS);
        } else {
            // Test/dev path - simulate immediate success.
            payment.setExternalTransactionId(UUID.randomUUID().toString());
            payment.setStatus(PaymentStatus.SUCCESS);
        }
        paymentRepository.save(payment);

        // (4) zaplaten� - mark order as PAID
        order.setStatus(OrderStatus.PAID);
        order = orderRepository.save(order);

        // (5) Generate tickets and free the temporary reservations.
        List<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < reservations.size(); i++) {
            SeatReservation r = reservations.get(i);
            TicketType type = (request.getTicketTypes() != null && i < request.getTicketTypes().size())
                    ? request.getTicketTypes().get(i) : TicketType.NORMAL;
            String qrData = UUID.randomUUID().toString();
            // Diagram 28: GENERATED -> VALID after successful payment.
            tickets.add(Ticket.builder()
                    .order(order)
                    .seat(r.getSeat())
                    .type(type)
                    .status(TicketStatus.VALID)
                    .qrCodeData(qrData)
                    .qrCodeImage(QrCodeGenerator.generate(qrData))
                    .price(basePrice)
                    .build());
            r.setReleased(true);
        }
        ticketRepository.saveAll(tickets);
        reservationRepository.saveAll(reservations);

        // (6) Diagram 23: vstupenky vygenerovan� -> aktívna
        order.setStatus(OrderStatus.ACTIVE);
        order = orderRepository.save(order);

        // (7) Payment moves to its terminal COMPLETED state once the order is fulfilled.
        payment.setStatus(PaymentStatus.COMPLETED);
        paymentRepository.save(payment);

        return order;
    }

    public List<Order> findMyOrders(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    public Order findById(Long id, String userEmail) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return order;
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    /**
     * UC_04: Customer storno + refund (Diagram 11/20).
     * - Storno window: at least 5 minutes before screening start.
     * - Refund window: at least 60 minutes before start (per UC text).
     */
    @Transactional
    public void cancelOrder(Long orderId, CancelRequest request, String userEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus() == OrderStatus.CANCELLED
                || order.getStatus() == OrderStatus.STORNOVANA
                || order.getStatus() == OrderStatus.REFUNDED) {
            throw new CancellationNotAllowedException("Order already cancelled");
        }

        Screening screening = order.getScreening();
        LocalDateTime now = LocalDateTime.now();
        long minutesToStart = ChronoUnit.MINUTES.between(now, screening.getStartTime());

        if (minutesToStart < 5) {
            throw new CancellationNotAllowedException("Cannot cancel within 5 minutes of screening start");
        }

        List<Ticket> ticketsToCancel;
        if (request == null || request.getTicketIds() == null) {
            ticketsToCancel = ticketRepository.findByOrderId(orderId);
        } else {
            ticketsToCancel = request.getTicketIds().stream()
                    .map(id -> ticketRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + id)))
                    .toList();
        }

        // Pouzit� vstupenky nie je mozn� stornova (Diagram 20 / UC_04 exception).
        for (Ticket t : ticketsToCancel) {
            if (t.getStatus() == TicketStatus.USED) {
                throw new CancellationNotAllowedException("Cannot cancel a ticket that has already been used");
            }
        }

        ticketsToCancel.forEach(t -> t.setStatus(TicketStatus.CANCELLED));
        ticketRepository.saveAll(ticketsToCancel);

        // Diagram 23: ak s� vsetky vstupenky stornovan� -> objedn�vka prech�dza do stornovan�.
        List<Ticket> allTickets = ticketRepository.findByOrderId(orderId);
        boolean allCancelled = allTickets.stream().allMatch(t -> t.getStatus() == TicketStatus.CANCELLED);
        if (allCancelled) {
            order.setStatus(OrderStatus.STORNOVANA);
            orderRepository.save(order);
        }

        // Refund only if the customer is still within the refund window.
        if (minutesToStart >= 60) {
            processRefund(order, ticketsToCancel, /*automatic=*/ false);
        }
    }

    /**
     * UC_07: Manager-initiated screening cancellation (Diagram 17/31).
     * Cascades order CANCELLED status, ticket CANCELLED, automatic refund + notifications.
     */
    @Transactional
    public void cancelScreening(Long screeningId) {
        Screening screening = screeningRepository.findById(screeningId)
                .orElseThrow(() -> new ResourceNotFoundException("Screening not found"));
        screening.setCancelled(true);
        screeningRepository.save(screening);

        // Cancel all live orders (PAID, ACTIVE, CREATED, AWAITING_PAYMENT) for this screening.
        List<Order> orders = new ArrayList<>();
        for (OrderStatus s : List.of(OrderStatus.ACTIVE, OrderStatus.PAID,
                                     OrderStatus.AWAITING_PAYMENT, OrderStatus.CREATED)) {
            orders.addAll(orderRepository.findByScreeningIdAndStatus(screeningId, s));
        }
        for (Order order : orders) {
            List<Ticket> tickets = ticketRepository.findByOrderId(order.getId());
            tickets.forEach(t -> t.setStatus(TicketStatus.CANCELLED));
            ticketRepository.saveAll(tickets);
            // Diagram 23: predstavenie zrušen� -> zrušen�.
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            processRefund(order, tickets, /*automatic=*/ true);
            notificationService.notifyScreeningCancelled(order.getUser(), screening);
        }
    }

    private void processRefund(Order order, List<Ticket> tickets, boolean automatic) {
        paymentRepository.findByOrderId(order.getId()).ifPresent(payment -> {
            BigDecimal refundAmount = tickets.stream()
                    .map(Ticket::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            PaymentGateway.PaymentResult result = paymentGateway.refund(
                    payment.getExternalTransactionId(), refundAmount);
            // Diagram 27: �spešn� -> platba je refundovan�; failure stays SUCCESS for manual.
            payment.setStatus(result.success() ? PaymentStatus.REFUNDED : PaymentStatus.SUCCESS);
            paymentRepository.save(payment);
            if (result.success()) {
                // Diagram 23: stornovan� -> refundovan� (zrušen� stays in CANCELLED).
                if (order.getStatus() == OrderStatus.STORNOVANA) {
                    order.setStatus(OrderStatus.REFUNDED);
                    orderRepository.save(order);
                }
                notificationService.notifyRefundProcessed(
                        order.getUser(), payment.getExternalTransactionId(), refundAmount.toPlainString());
            }
        });
    }
}
