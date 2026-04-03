package org.psi.psinema.domain.payment;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@Primary
public class MockPaymentGateway implements PaymentGateway {

    @Override
    public PaymentResult charge(BigDecimal amount, String paymentToken) {
        return new PaymentResult(true, UUID.randomUUID().toString(), "Payment successful (mock)");
    }

    @Override
    public PaymentResult refund(String transactionId, BigDecimal amount) {
        return new PaymentResult(true, UUID.randomUUID().toString(), "Refund successful (mock)");
    }
}
