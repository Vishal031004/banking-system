package com.bank.ooad.models.transactions;

import com.bank.ooad.models.enums.TransactionStatus;
import com.bank.ooad.models.enums.RiskLevel;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String transactionId;
    
    private double amount;
    private String type;
    
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    
    private LocalDateTime timestamp;
    private String senderAccount;
    private String receiverAccount;
    private double riskScore;
    private String failureReason;

    @ManyToOne
    @JoinColumn(name="statement_id")
    private Statement statement;

    public boolean validateInput() {
        System.out.println("Validating transaction input");
        return true;
    }

    public boolean checkDailyLimit() {
        System.out.println("Checking daily limits");
        return true;
    }

    public boolean verifyBalance() {
        System.out.println("Verifying balance");
        return true;
    }

    public RiskLevel analyzeRisk() {
        System.out.println("Analyzing risk");
        return RiskLevel.LOW;
    }

    public void initiateDBTransaction() {
        System.out.println("Initiating DB transaction");
    }

    public void debitSender() {
        System.out.println("Debiting sender");
    }

    public void creditReceiver() {
        System.out.println("Crediting receiver");
    }

    public void commitTransaction() {
        System.out.println("Committing transaction");
        this.status = TransactionStatus.COMPLETED;
    }

    public void rollbackTransaction() {
        System.out.println("Rolling back transaction");
        this.status = TransactionStatus.ROLLBACK;
    }

    public Receipt generateReceipt() {
        System.out.println("Generating receipt");
        return new Receipt();
    }

    public void logException() {
        System.out.println("Logging exception");
    }

    // Getters and Setter
    public String getTransactionId() { return transactionId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getSenderAccount() { return senderAccount; }
    public void setSenderAccount(String senderAccount) { this.senderAccount = senderAccount; }
    public String getReceiverAccount() { return receiverAccount; }
    public void setReceiverAccount(String receiverAccount) { this.receiverAccount = receiverAccount; }
    public double getRiskScore() { return riskScore; }
    public void setRiskScore(double riskScore) { this.riskScore = riskScore; }
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
}
