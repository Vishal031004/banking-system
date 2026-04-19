package com.bank.ooad.models.transactions;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Receipt {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String receiptId;
    
    private String transactionId;
    private LocalDateTime generatedAt;
    private String details;

    public void print() {
        System.out.println("Printing receipt: " + details);
    }

    public void download() {
        System.out.println("Downloading receipt " + receiptId);
    }

    // Getters/Setters
    public String getReceiptId() { return receiptId; }
    public void setReceiptId(String receiptId) { this.receiptId = receiptId; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
