package com.bank.ooad.controllers;

import com.bank.ooad.models.enums.AccountType;
import com.bank.ooad.patterns.factory.BankAccountFactory;
import com.bank.ooad.models.accounts.BankAccount;
import com.bank.ooad.models.transactions.Transaction;
import com.bank.ooad.models.enums.TransactionStatus;
import com.bank.ooad.patterns.observer.TransactionCompletedEvent;
import com.bank.ooad.models.loans.LoanApplication;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/demo")
public class DemoController {

    private final BankAccountFactory factory;
    private final ApplicationEventPublisher publisher;

    public DemoController(BankAccountFactory factory, ApplicationEventPublisher publisher) {
        this.factory = factory;
        this.publisher = publisher;
    }

    @PostMapping("/factory")
    public Map<String, String> testFactory(@RequestParam String type) {
        BankAccount account = factory.createAccount(AccountType.valueOf(type.toUpperCase()));
        Map<String, String> res = new HashMap<>();
        res.put("message", "Factory instantiated class: " + account.getClass().getSimpleName());
        return res;
    }

    @PostMapping("/observer")
    public Map<String, String> testObserver() {
        Transaction tx = new Transaction();
        tx.setAmount(12000.50);
        tx.setStatus(TransactionStatus.COMPLETED);
        
        // This fires the Observer pattern logic
        publisher.publishEvent(new TransactionCompletedEvent(this, tx));
        
        Map<String, String> res = new HashMap<>();
        res.put("message", "Published TransactionCompletedEvent! Observers (Fraud & Notification) successfully notified.");
        return res;
    }

    @PostMapping("/state")
    public Map<String, String> testState() {
        LoanApplication loan = new LoanApplication();
        loan.submitApplication();
        String st1 = loan.getStatus().name();
        
        loan.officerEndorse();
        String st2 = loan.getStatus().name();
        
        loan.managerApprove();
        String st3 = loan.getStatus().name();
        
        Map<String, String> res = new HashMap<>();
        res.put("message", "Loan Application safely navigated State Pattern: " + st1 + " ➔ " + st2 + " ➔ " + st3);
        return res;
    }
}
