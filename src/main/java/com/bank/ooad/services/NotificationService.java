package com.bank.ooad.services;

import com.bank.ooad.models.system.NotificationMessage;
import com.bank.ooad.models.enums.NotificationType;
import com.bank.ooad.patterns.observer.TransactionCompletedEvent;
import com.bank.ooad.patterns.strategy.INotificationGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.bank.ooad.patterns.observer.LoanStatusEvent;

@Service
public class NotificationService {
    @Autowired @Qualifier("emailGateway") private INotificationGateway emailGateway;
    @Autowired @Qualifier("smsGateway") private INotificationGateway smsGateway;

    // Observer Pattern target
    @EventListener
    public void handleTransactionCompleted(TransactionCompletedEvent event) {
        System.out.println("NotificationService observed transaction completion: " + event.getTransaction().getTransactionId());
        receiveDispatchRequest("Tx " + event.getTransaction().getTransactionId() + " completed");
    }

    @EventListener
    public void handleLoanStatus(LoanStatusEvent event) {
        System.out.println("NotificationService observed loan change: " + event.getLoanApplication().getApplicationId());
        receiveDispatchRequest(event.getMessage());
    }

    public void receiveDispatchRequest(String eventMsg) {
        System.out.println("Received dispatch request: " + eventMsg);
        NotificationMessage msg = new NotificationMessage();
        msg.generateMessagePayload(eventMsg, NotificationType.EMAIL);
        prepareEmailNotification(msg);
    }

    public void prepareEmailNotification(NotificationMessage msg) {
        // Strategy usage
        boolean success = emailGateway.dispatch(msg);
        logDeliveryOutcome(msg, success);
    }

    public void prepareSMSNotification(NotificationMessage msg) {
        // Strategy usage
        boolean success = smsGateway.dispatch(msg);
        logDeliveryOutcome(msg, success);
    }

    public void evaluateDeliveryStatus() {
        System.out.println("Evaluating delivery status");
    }

    public void queueForRetry(NotificationMessage msg) {
        System.out.println("Queueing for retry");
    }

    public void logDeliveryOutcome(NotificationMessage msg, boolean success) {
        System.out.println("Outcome logged: " + success);
    }
}
