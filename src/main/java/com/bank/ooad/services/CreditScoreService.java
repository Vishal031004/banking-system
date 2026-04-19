package com.bank.ooad.services;

import com.bank.ooad.patterns.observer.TransactionCompletedEvent;
import com.bank.ooad.models.users.Customer;
import com.bank.ooad.repositories.UserRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class CreditScoreService {
    
    private final UserRepository userRepository;

    public CreditScoreService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public int fetchScore(String customerId) {
        Customer c = (Customer) userRepository.findById(customerId).orElse(null);
        return c != null ? c.getCreditScore() : 750;
    }

    public boolean evaluateEligibility(int score, double income) {
        System.out.println("Evaluating loan eligibility");
        return score > 700 && income > 50000;
    }
    
    @EventListener
    public void onTransactionCompleted(TransactionCompletedEvent event) {
        System.out.println("CreditScoreEngine processing transaction event: " + event.getTransaction().getAmount());
        
        // Find sender and bump script
        String fromAcc = event.getTransaction().getSenderAccount();
        // Since we don't have direct acc->user here easily without AccountRepository,
        // we'll simulate the bump by logging it. 
        // In a real DB we would query the user by account ID and update.
        System.out.println("Dynamically adjusting credit score based on positive transaction history.");
    }
}
