package com.bank.ooad.models.accounts;

import com.bank.ooad.models.transactions.Statement;
import jakarta.persistence.Entity;

@Entity
public class SavingsAccount extends BankAccount {
    private double interestRate;
    private double minimumBalance;

    public void calculateInterest() {
        double interest = getBalance() * interestRate;
        System.out.println("Interest calculated: " + interest);
    }

    @Override
    public Statement generateMonthlyStatement() {
        System.out.println("Generating Savings Statement");
        return new Statement();
    }

    // Getters / Setters
    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }
    public double getMinimumBalance() { return minimumBalance; }
    public void setMinimumBalance(double minimumBalance) { this.minimumBalance = minimumBalance; }
}
