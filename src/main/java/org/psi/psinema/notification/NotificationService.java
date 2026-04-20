package org.psi.psinema.notification;

import lombok.extern.slf4j.Slf4j;
import org.psi.psinema.domain.screening.Screening;
import org.psi.psinema.domain.user.User;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    public void notifyScreeningCancelled(User user, Screening screening) {
        log.info("[NOTIFY] {} -> screening '{}' at {} was cancelled",
                user.getEmail(),
                screening.getMovie() != null ? screening.getMovie().getTitle() : "?",
                screening.getStartTime());
    }

    public void notifyRefundProcessed(User user, String transactionId, String amount) {
        log.info("[NOTIFY] {} -> refund {} for tx {}", user.getEmail(), amount, transactionId);
    }

    public void notifyBuffetStaff(Long snackOrderId) {
        log.info("[NOTIFY] buffet staff -> snack order {} ready to prepare", snackOrderId);
    }
}
