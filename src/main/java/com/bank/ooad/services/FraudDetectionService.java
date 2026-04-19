package com.bank.ooad.services;

import com.bank.ooad.models.enums.RiskLevel;
import com.bank.ooad.models.transactions.Transaction;
import com.bank.ooad.patterns.observer.TransactionCompletedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class FraudDetectionService {
    private double riskThreshold = 50.0;

    // Observer Pattern target
    @EventListener
    public void analyzeTransactionFromEvent(TransactionCompletedEvent event) {
        System.out.println("FraudDetectionService observed transaction completion");
        double score = generateRiskScore(event.getTransaction());
        if(score > riskThreshold) {
            flagSuspiciousActivity(event.getTransaction().getSenderAccount());
        }
    }

    public RiskLevel analyzeTransaction(Transaction transaction) {
        System.out.println("Analyzing transaction details explicitly");
        return RiskLevel.LOW;
    }

    public void flagSuspiciousActivity(String accountId) {
        System.out.println("Flagging suspicious activity on account: " + accountId);
    }

    public double generateRiskScore(Transaction transaction) {
        return Math.random() * 100;
    }
}
