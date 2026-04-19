package com.bank.ooad.models.accounts;

import com.bank.ooad.models.transactions.Statement;
import jakarta.persistence.Entity;

@Entity
public class CurrentAccount extends BankAccount {
    private double overdraftLimit;

    public double getOverdraftAvailable() {
        return overdraftLimit + getBalance(); // Simplified calculation
    }

    @Override
    public Statement generateMonthlyStatement() {
        System.out.println("Generating Current Account Statement");
        return new Statement();
    }

    // Getters / Setters
    public double getOverdraftLimit() { return overdraftLimit; }
    public void setOverdraftLimit(double overdraftLimit) { this.overdraftLimit = overdraftLimit; }
}
