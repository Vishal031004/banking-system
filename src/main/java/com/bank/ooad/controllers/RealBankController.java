package com.bank.ooad.controllers;

import com.bank.ooad.models.accounts.BankAccount;
import com.bank.ooad.models.enums.AccountType;
import com.bank.ooad.models.enums.TransactionStatus;
import com.bank.ooad.models.transactions.Transaction;
import com.bank.ooad.models.users.Customer;
import com.bank.ooad.patterns.observer.TransactionCompletedEvent;
import com.bank.ooad.repositories.TransactionRepository;
import com.bank.ooad.services.AccountService;
import com.bank.ooad.services.CustomerService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/app")
public class RealBankController {

    private final CustomerService customerService;
    private final AccountService accountService;
    private final TransactionRepository transactionRepository;
    private final ApplicationEventPublisher publisher;
    private final com.bank.ooad.services.FraudDetectionService fraudService;

    public RealBankController(CustomerService customerService, AccountService accountService, 
                              TransactionRepository transactionRepository, ApplicationEventPublisher publisher,
                              com.bank.ooad.services.FraudDetectionService fraudService) {
        this.customerService = customerService;
        this.accountService = accountService;
        this.transactionRepository = transactionRepository;
        this.publisher = publisher;
        this.fraudService = fraudService;
    }

    @PostMapping("/users/register")
    public Customer register(@RequestParam String name, @RequestParam String email, @RequestParam double income, @RequestParam String password) {
        if (password == null || password.trim().length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters.");
        }
        return customerService.registerCustomer(name, email, income, password);
    }

    @PostMapping("/users/login")
    public com.bank.ooad.models.users.User login(@RequestParam String email, @RequestParam String password) {
        if ("admin@finance.com".equalsIgnoreCase(email)) {
            // Hardcoded admin backdoor for demo — verify against fixed password
            if (!"admin123".equals(password)) {
                throw new RuntimeException("Invalid admin credentials.");
            }
            com.bank.ooad.models.users.BankStaff admin = new com.bank.ooad.models.users.BankStaff();
            admin.setEmail("admin@finance.com");
            admin.setName("System Administrator");
            admin.setRole("ADMIN");
            return admin;
        }

        com.bank.ooad.models.users.User user = customerService.getAllCustomers().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No account found with that email."));

        if (!customerService.verifyPassword(password, user.getPasswordHash())) {
            throw new RuntimeException("Incorrect password.");
        }

        return user;
    }

    @GetMapping("/users/{id}/dashboard")
    public Map<String, Object> getDashboard(@PathVariable String id) {
        Customer c = customerService.getCustomer(id);
        if (c == null) throw new RuntimeException("Customer not found");

        Map<String, Object> data = new HashMap<>();
        data.put("user", c);
        // Only return ACTIVE accounts to the dashboard
        List<BankAccount> activeAccounts = c.getAccounts().stream()
            .filter(a -> a.getStatus() == com.bank.ooad.models.enums.AccountStatus.ACTIVE)
            .toList();
        data.put("accounts", activeAccounts);
        data.put("totalBalance", activeAccounts.stream().mapToDouble(BankAccount::getBalance).sum());
        return data;
    }

    @PostMapping("/accounts/create")
    public BankAccount createAccount(@RequestParam String customerId, @RequestParam String type, @RequestParam double initialDeposit, @RequestParam(required = false, defaultValue = "dummy_passport.pdf") String kycDoc) {
        return accountService.createAccountForCustomer(customerId, AccountType.valueOf(type.toUpperCase()), initialDeposit, kycDoc);
    }

    @GetMapping("/accounts/all")
    public List<BankAccount> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @PostMapping("/admin/accounts/{accountId}/verify")
    public BankAccount verifyAccount(@PathVariable String accountId) {
        BankAccount acc = accountService.getAccount(accountId);
        acc.setStatus(com.bank.ooad.models.enums.AccountStatus.ACTIVE);
        return accountService.saveAccount(acc);
    }

    @PostMapping("/transactions/transfer")
    public Map<String, Object> transfer(@RequestParam String fromAccount, @RequestParam String toAccount, @RequestParam double amount) {
        BankAccount source = accountService.getAccount(fromAccount);
        BankAccount target = accountService.getAccount(toAccount);

        if (source.getBalance() < amount) {
            throw new RuntimeException("Insufficient Funds");
        }

        // Initial record setup for risk check
        Transaction tx = new Transaction();
        tx.setAmount(amount);
        tx.setSenderAccount(fromAccount);
        tx.setReceiverAccount(toAccount);
        tx.setTimestamp(LocalDateTime.now());
        tx.setType("TRANSFER");

        // Synchronous Risk Engine invocation
        fraudService.analyzeTransaction(tx); // Throws exceptions on high risk

        // Domain Logic
        source.withdraw(amount);
        target.deposit(amount);
        accountService.saveAccount(source);
        accountService.saveAccount(target);

        // Commit Transaction
        tx.setStatus(TransactionStatus.COMPLETED);
        tx = transactionRepository.save(tx);

        // Fire Observer
        publisher.publishEvent(new TransactionCompletedEvent(this, tx));

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("transaction", tx);
        res.put("newBalance", source.getBalance());
        return res;
    }
}
