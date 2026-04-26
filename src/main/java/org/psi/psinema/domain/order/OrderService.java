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

        // Create order
        Order order = Order.builder()
                .user(user)
                .screening(screening)
                .status(OrderStatus.CONFIRMED)
                .totalAmount(total)
                .build();
        order = orderRepository.save(order);

        // Process payment (skipped when app.dont-pay=true)
        if (!dontPay) {
            PaymentGateway.PaymentResult result = paymentGateway.charge(total, request.getPaymentToken());
            if (!result.success()) {
                throw new ConflictException("Payment failed: " + result.message());
            }
            Payment payment = Payment.builder()
                    .order(order)
                    .status(PaymentStatus.COMPLETED)
                    .externalTransactionId(result.transactionId())
                    .amount(total)
                    .processedAt(LocalDateTime.now())
                    .build();
            paymentRepository.save(payment);
        }

        // Create tickets + release reservations
        List<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < reservations.size(); i++) {
            SeatReservation r = reservations.get(i);
            TicketType type = (request.getTicketTypes() != null && i < request.getTicketTypes().size())
                    ? request.getTicketTypes().get(i) : TicketType.NORMAL;
            String qrData = UUID.randomUUID().toString();
            tickets.add(Ticket.builder()
                    .order(order)
                    .seat(r.getSeat())
                    .type(type)
                    .status(TicketStatus.ACTIVE)
                    .qrCodeData(qrData)
                    .qrCodeImage(QrCodeGenerator.generate(qrData))
                    .price(basePrice)
                    .build());
            r.setReleased(true);
        }
        ticketRepository.saveAll(tickets);
        reservationRepository.saveAll(reservations);

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

    @Transactional
    public void cancelOrder(Long orderId, CancelRequest request, String userEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus() == OrderStatus.CANCELLED) {
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

        ticketsToCancel.forEach(t -> t.setStatus(TicketStatus.CANCELLED));
        ticketRepository.saveAll(ticketsToCancel);

        // Check if entire order is now cancelled
        List<Ticket> allTickets = ticketRepository.findByOrderId(orderId);
        boolean allCancelled = allTickets.stream().allMatch(t -> t.getStatus() == TicketStatus.CANCELLED);
        if (allCancelled) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        }

        // Refund if >60 min before start
        if (minutesToStart >= 60) {
            processRefund(order, ticketsToCancel);
        }
    }

    @Transactional
    public void cancelScreening(Long screeningId) {
        Screening screening = screeningRepository.findById(screeningId)
                .orElseThrow(() -> new ResourceNotFoundException("Screening not found"));
        screening.setCancelled(true);
        screeningRepository.save(screening);

        // Cancel all active tickets + refund
        List<Order> orders = orderRepository.findByScreeningIdAndStatus(screeningId, OrderStatus.CONFIRMED);
        for (Order order : orders) {
            List<Ticket> tickets = ticketRepository.findByOrderId(order.getId());
            tickets.forEach(t -> t.setStatus(TicketStatus.CANCELLED));
            ticketRepository.saveAll(tickets);
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            processRefund(order, tickets);
            notificationService.notifyScreeningCancelled(order.getUser(), screening);
        }
    }

    private void processRefund(Order order, List<Ticket> tickets) {
        paymentRepository.findByOrderId(order.getId()).ifPresent(payment -> {
            BigDecimal refundAmount = tickets.stream()
                    .map(Ticket::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            PaymentGateway.PaymentResult result = paymentGateway.refund(
                    payment.getExternalTransactionId(), refundAmount);
            payment.setStatus(result.success() ? PaymentStatus.REFUNDED : PaymentStatus.MANUAL_REFUND);
            paymentRepository.save(payment);
            if (result.success()) {
                order.setStatus(OrderStatus.REFUNDED);
                orderRepository.save(order);
                notificationService.notifyRefundProcessed(
                        order.getUser(), payment.getExternalTransactionId(), refundAmount.toPlainString());
            }
        });
    }
}
