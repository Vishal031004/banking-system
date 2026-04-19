package com.bank.ooad.services;

import org.springframework.stereotype.Service;
import com.bank.ooad.models.system.AuditTrail;
import java.time.LocalDateTime;

@Service
public class AuthService {
    private int maxFailedAttempts = 3;

    // Facade for auth
    public boolean validateCredentials(String userId, String pwd) {
        System.out.println("Validating credentials");
        logToAudit(userId, "LOGIN_ATTEMPT");
        return true;
    }

    public String generateOTP() {
        return "123456";
    }

    public boolean verifyOTP(String otp) {
        return "123456".equals(otp);
    }

    public String generateSessionToken() {
        return "SESSION_ABC_123";
    }

    public void lockAccount(String userId) {
        System.out.println("Locking account: " + userId);
    }

    public void logLoginAttempt(String userId, boolean success) {
        System.out.println("Logging login attempt: " + success);
    }

    public void triggerSecurityAlert(String userId) {
        System.out.println("Security alert triggered for: " + userId);
    }

    private void logToAudit(String userId, String action) {
        AuditTrail audit = new AuditTrail();
        audit.setUserId(userId);
        audit.setAction(action);
        audit.setTimestamp(LocalDateTime.now());
        audit.logEvent(userId, action);
    }
}
