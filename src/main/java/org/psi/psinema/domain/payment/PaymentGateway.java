package org.psi.psinema.domain.payment;

import java.math.BigDecimal;

public interface PaymentGateway {
    PaymentResult charge(BigDecimal amount, String paymentToken);
    PaymentResult refund(String transactionId, BigDecimal amount);

    record PaymentResult(boolean success, String transactionId, String message) {}
}
