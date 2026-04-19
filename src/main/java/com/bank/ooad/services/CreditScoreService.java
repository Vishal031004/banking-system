package com.bank.ooad.services;

import org.springframework.stereotype.Service;

@Service
public class CreditScoreService {

    public int fetchScore(String customerId) {
        System.out.println("Fetching credit score for user: " + customerId);
        return 750;
    }

    public boolean evaluateEligibility(int score, double income) {
        System.out.println("Evaluating loan eligibility");
        return score > 700 && income > 50000;
    }
}
