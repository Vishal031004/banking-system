package com.bank.ooad.models.system;

import com.bank.ooad.models.enums.NotificationStatus;
import com.bank.ooad.models.enums.NotificationType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class NotificationMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String messageId;
    
    private String recipientId;
    
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    
    private String payload;
    
    @Enumerated(EnumType.STRING)
    private NotificationStatus status;
    
    private int retryCount;
    private int maxRetries;
    private LocalDateTime createdAt;

    public void generateMessagePayload(String string, NotificationType type) {
        this.payload = string;
        this.type = type;
        System.out.println("Generating payload: " + string);
    }

    public void pushToQueue() {
        this.status = NotificationStatus.QUEUED;
        System.out.println("Pushing message to queue");
    }

    public void dispatch() {
        this.status = NotificationStatus.DISPATCHING;
        System.out.println("Dispatching message");
    }

    public void retryDispatch() {
        if(retryCount < maxRetries) {
            retryCount++;
            System.out.println("Retrying dispatch : " + retryCount);
        } else {
            abort();
        }
    }

    public void markDelivered() {
        this.status = NotificationStatus.DELIVERED;
        System.out.println("Message delivered");
    }

    public void abort() {
        this.status = NotificationStatus.ABORTED;
        System.out.println("Message delivery aborted");
    }

    // Getters and Setters
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    public String getRecipientId() { return recipientId; }
    public void setRecipientId(String recipientId) { this.recipientId = recipientId; }
    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    public NotificationStatus getStatus() { return status; }
    public void setStatus(NotificationStatus status) { this.status = status; }
    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
    public int getMaxRetries() { return maxRetries; }
    public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
