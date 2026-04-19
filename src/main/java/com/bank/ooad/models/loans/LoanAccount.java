package com.bank.ooad.models.loans;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class LoanAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String loanAccountId;

    private double principalAmount;
    private double interestRate;
    private int tenure;
    private double emiAmount;
    private LocalDateTime disbursedAt;
    private double outstandingBalance;

    public double calculateEMI() {
        System.out.println("Calculating EMI inside Loan Account");
        return emiAmount;
    }

    public void recordRepayment(double amount) {
        if(outstandingBalance >= amount) {
            outstandingBalance -= amount;
        }
        System.out.println("Recording repayment of: " + amount);
    }

    public double getOutstanding() {
        return outstandingBalance;
    }

    // Getters and Setters
    public String getLoanAccountId() { return loanAccountId; }
    public void setLoanAccountId(String loanAccountId) { this.loanAccountId = loanAccountId; }
    public double getPrincipalAmount() { return principalAmount; }
    public void setPrincipalAmount(double principalAmount) { this.principalAmount = principalAmount; }
    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }
    public int getTenure() { return tenure; }
    public void setTenure(int tenure) { this.tenure = tenure; }
    public double getEmiAmount() { return emiAmount; }
    public void setEmiAmount(double emiAmount) { this.emiAmount = emiAmount; }
    public LocalDateTime getDisbursedAt() { return disbursedAt; }
    public void setDisbursedAt(LocalDateTime disbursedAt) { this.disbursedAt = disbursedAt; }
    public double getOutstandingBalance() { return outstandingBalance; }
    public void setOutstandingBalance(double outstandingBalance) { this.outstandingBalance = outstandingBalance; }
}
