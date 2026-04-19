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

    public RealBankController(CustomerService customerService, AccountService accountService, 
                              TransactionRepository transactionRepository, ApplicationEventPublisher publisher) {
        this.customerService = customerService;
        this.accountService = accountService;
        this.transactionRepository = transactionRepository;
        this.publisher = publisher;
    }

    @PostMapping("/users/register")
    public Customer register(@RequestParam String name, @RequestParam String email, @RequestParam double income) {
        return customerService.registerCustomer(name, email, income);
    }

    @GetMapping("/users/{id}/dashboard")
    public Map<String, Object> getDashboard(@PathVariable String id) {
        Customer c = customerService.getCustomer(id);
        if (c == null) throw new RuntimeException("Customer not found");

        Map<String, Object> data = new HashMap<>();
        data.put("user", c);
        data.put("accounts", c.getAccounts());
        data.put("totalBalance", c.getAccounts().stream().mapToDouble(BankAccount::getBalance).sum());
        return data;
    }

    @PostMapping("/accounts/create")
    public BankAccount createAccount(@RequestParam String customerId, @RequestParam String type, @RequestParam double initialDeposit) {
        return accountService.createAccountForCustomer(customerId, AccountType.valueOf(type.toUpperCase()), initialDeposit);
    }

    @GetMapping("/accounts/all")
    public List<BankAccount> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @PostMapping("/transactions/transfer")
    public Map<String, Object> transfer(@RequestParam String fromAccount, @RequestParam String toAccount, @RequestParam double amount) {
        BankAccount source = accountService.getAccount(fromAccount);
        BankAccount target = accountService.getAccount(toAccount);

        if (source.getBalance() < amount) {
            throw new RuntimeException("Insufficient Funds");
        }

        // Domain Logic
        source.withdraw(amount);
        target.deposit(amount);
        accountService.saveAccount(source);
        accountService.saveAccount(target);

        // Record Transaction
        Transaction tx = new Transaction();
        tx.setAmount(amount);
        tx.setSenderAccount(fromAccount);
        tx.setReceiverAccount(toAccount);
        tx.setTimestamp(LocalDateTime.now());
        tx.setStatus(TransactionStatus.COMPLETED);
        tx.setType("TRANSFER");
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
