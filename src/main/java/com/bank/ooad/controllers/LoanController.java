package com.bank.ooad.controllers;

import com.bank.ooad.models.loans.LoanApplication;
import com.bank.ooad.patterns.observer.LoanStatusEvent;
import com.bank.ooad.repositories.LoanApplicationRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/app/loans")
public class LoanController {

    private final LoanApplicationRepository repository;
    private final ApplicationEventPublisher publisher;
    private final com.bank.ooad.services.CreditScoreService creditService;

    public LoanController(LoanApplicationRepository repository, ApplicationEventPublisher publisher, 
                          com.bank.ooad.services.CreditScoreService creditService) {
        this.repository = repository;
        this.publisher = publisher;
        this.creditService = creditService;
    }

    @GetMapping("/offers/{customerId}")
    public Map<String, Object> getPreapprovedOffers(@PathVariable String customerId) {
        int score = creditService.fetchScore(customerId);
        Map<String, Object> res = new HashMap<>();
        res.put("creditScore", score);
        List<Map<String, Object>> offers = new java.util.ArrayList<>();
        
        if (score >= 750) {
            offers.add(Map.of("type", "HOME", "maxAmount", 500000, "rate", "4.5%"));
            offers.add(Map.of("type", "AUTO", "maxAmount", 60000, "rate", "5.2%"));
        } else if (score >= 650) {
            offers.add(Map.of("type", "AUTO", "maxAmount", 30000, "rate", "6.5%"));
            offers.add(Map.of("type", "PERSONAL", "maxAmount", 10000, "rate", "8.0%"));
        } else {
            offers.add(Map.of("type", "PERSONAL", "maxAmount", 2000, "rate", "12.0%"));
        }
        res.put("offers", offers);
        return res;
    }

    @PostMapping("/apply")
    public Map<String, Object> applyForLoan(@RequestParam String customerId, 
                                            @RequestParam double amount, 
                                            @RequestParam int tenure, 
                                            @RequestParam String purpose) {
        
        // Single active application rule
        List<LoanApplication> existing = repository.findByCustomerId(customerId);
        boolean hasPending = existing.stream().anyMatch(l -> 
            l.getStatus().toString().equals("SUBMITTED") || l.getStatus().toString().equals("PENDING_MANAGER"));
        
        if (hasPending) {
            throw new RuntimeException("Wait for your previous loan application to be processed before submitting another one.");
        }

        LoanApplication loan = new LoanApplication();
        loan.setCustomerId(customerId);
        loan.setLoanAmount(amount);
        loan.setTenure(tenure);
        loan.setPurpose(purpose);
        loan.setSubmittedAt(LocalDateTime.now());
        
        loan.submitApplication(); // Status = SUBMITTED
        loan.setEmi(loan.calculateEmi());

        LoanApplication saved = repository.save(loan);
        
        publisher.publishEvent(new LoanStatusEvent(this, saved, "New loan application submitted for $" + amount));

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("loan", saved);
        return res;
    }

    @GetMapping("/user/{customerId}")
    public List<LoanApplication> getUserLoans(@PathVariable String customerId) {
        return repository.findByCustomerId(customerId);
    }
    
    @GetMapping("/all")
    public List<LoanApplication> getAllLoans() {
        return repository.findAll();
    }

    @PostMapping("/manager-review/{applicationId}")
    public Map<String, Object> managerReview(@PathVariable String applicationId, @RequestParam boolean approve) {
        LoanApplication loan = repository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        if (approve) {
            loan.managerApprove();
            publisher.publishEvent(new LoanStatusEvent(this, loan, "Loan " + loan.getApplicationId() + " has been Approved!"));
        } else {
            loan.managerReject();
            publisher.publishEvent(new LoanStatusEvent(this, loan, "Loan " + loan.getApplicationId() + " has been Rejected."));
        }
        
        LoanApplication saved = repository.save(loan);

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("loan", saved);
        return res;
    }
}
