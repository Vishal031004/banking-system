package com.bank.ooad.models.accounts;

import com.bank.ooad.models.enums.AccountStatus;
import com.bank.ooad.models.enums.AccountType;
import com.bank.ooad.models.transactions.Statement;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class BankAccount {
    @Id
    protected String accountNumber;
    
    @ManyToOne
    @JoinColumn(name = "customer_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    protected com.bank.ooad.models.users.Customer customer;
    
    protected double balance;
    
    @Enumerated(EnumType.STRING)
    protected AccountType accountType;
    
    @Enumerated(EnumType.STRING)
    protected AccountStatus status;
    
    protected LocalDateTime createdAt;
    
    // In UML it is List<String>
    @ElementCollection
    protected java.util.List<String> kycDocuments;

    public void deposit(double amount) {
        this.balance += amount;
        System.out.println("Deposited " + amount);
    }

    public void withdraw(double amount) {
        if(balance >= amount) {
            this.balance -= amount;
        } else {
            System.out.println("Insufficient funds");
        }
    }

    public void transfer(BankAccount target, double amount) {
        if(balance >= amount) {
            this.withdraw(amount);
            target.deposit(amount);
            System.out.println("Transferred " + amount);
        }
    }

    public double getBalance() {
        return this.balance;
    }

    public void freeze() {
        this.status = AccountStatus.SUSPENDED;
    }

    public void closeAccount() {
        this.status = AccountStatus.CLOSED;
    }

    public void verifyKYC() {
        System.out.println("Verifying KYC");
    }

    public abstract Statement generateMonthlyStatement();

    // Getters and Setters
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public com.bank.ooad.models.users.Customer getCustomer() { return customer; }
    public void setCustomer(com.bank.ooad.models.users.Customer customer) { this.customer = customer; }
    public AccountType getAccountType() { return accountType; }
    public void setAccountType(AccountType accountType) { this.accountType = accountType; }
    public AccountStatus getStatus() { return status; }
    public void setStatus(AccountStatus status) { this.status = status; }
    public java.util.List<String> getKycDocuments() { return kycDocuments; }
    public void setKycDocuments(java.util.List<String> kycDocuments) { this.kycDocuments = kycDocuments; }
}
