package com.bank.ooad.models.system;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class AuditTrail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String auditId;
    
    private String userId;
    private String action;
    private LocalDateTime timestamp;
    private String ipAddress;

    public void logEvent(String userId, String action) {
        System.out.println("Logging event: " + action + " for user: " + userId);
    }

    public void updateMasterAuditTrail() {
        System.out.println("Updating master audit trail");
    }

    // Getters/Setters
    public String getAuditId() { return auditId; }
    public void setAuditId(String auditId) { this.auditId = auditId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
}
