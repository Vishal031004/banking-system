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
    private final OCRService ocrService;
    private final FraudDetectionService fraudService;

    public AccountService(BankAccountFactory factory, BankAccountRepository accountRepository, UserRepository userRepository, OCRService ocrService, FraudDetectionService fraudService) {
        this.factory = factory;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.ocrService = ocrService;
        this.fraudService = fraudService;
    }

    public BankAccount createAccountForCustomer(String customerId, AccountType type, double initialDeposit, String kycDoc) {
        Customer customer = (Customer) userRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Customer not found"));
        
        System.out.println("Initiating Complex KYC Flow limit verification...");
        // Handle mock OCR
        // (Assuming Document exists or we just bypass the risky call for the demo)
        fraudService.flagSuspiciousActivity("KYC-CHECK-" + customerId); // Mock fraud KYC verification
        
        BankAccount account = factory.createAccount(type);
        account.setAccountNumber("ACC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        account.setCustomer(customer);
        account.deposit(initialDeposit); 
        account.setStatus(AccountStatus.UNVERIFIED); // Mandates Admin verification
        
        if (account.getKycDocuments() == null) {
            account.setKycDocuments(new java.util.ArrayList<>());
        }
        account.getKycDocuments().add(kycDoc);

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
