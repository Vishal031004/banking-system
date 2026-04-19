package com.bank.ooad.services;

import com.bank.ooad.models.accounts.BankAccount;
import com.bank.ooad.models.enums.AccountType;
import com.bank.ooad.models.enums.AccountStatus;
import com.bank.ooad.models.users.Customer;
import com.bank.ooad.patterns.factory.BankAccountFactory;
import com.bank.ooad.repositories.BankAccountRepository;
import com.bank.ooad.repositories.UserRepository;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class AccountService {
    private final BankAccountFactory factory;
    private final BankAccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(BankAccountFactory factory, BankAccountRepository accountRepository, UserRepository userRepository) {
        this.factory = factory;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public BankAccount createAccountForCustomer(String customerId, AccountType type, double initialDeposit) {
        Customer customer = (Customer) userRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Customer not found"));
        
        BankAccount account = factory.createAccount(type);
        account.setAccountNumber("ACC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        account.setCustomer(customer);
        account.deposit(initialDeposit); // Utilizes information expert
        account.setStatus(AccountStatus.ACTIVE);
        
        return accountRepository.save(account);
    }

    public BankAccount getAccount(String accountId) {
        return accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Account not found"));
    }
    
    public java.util.List<BankAccount> getAllAccounts() {
        return accountRepository.findAll();
    }
    
    public BankAccount saveAccount(BankAccount account) {
        return accountRepository.save(account);
    }
}
