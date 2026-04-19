package com.bank.ooad.models.users;

import jakarta.persistence.Entity;

@Entity
public class Customer extends User {
    private String kycStatus;
    private int creditScore;
    private double income;
    private String sessionToken;
    private int failedLoginAttempts;
    private boolean isLocked;

    @jakarta.persistence.OneToMany(mappedBy = "customer", cascade = jakarta.persistence.CascadeType.ALL, fetch = jakarta.persistence.FetchType.EAGER)
    private java.util.List<com.bank.ooad.models.accounts.BankAccount> accounts = new java.util.ArrayList<>();

    public void applyForLoan() {
        System.out.println("Customer applying for loan");
    }

    public void viewAccountDetails() {
        System.out.println("Customer viewing account details");
    }

    public void viewTransactionHistory() {
        System.out.println("Customer viewing transactions");
    }

    public void downloadMonthlyStatement() {
        System.out.println("Downloading monthly statement");
    }

    public void submitKYC() {
        System.out.println("Submitting KYC");
    }

    public void enterOTP() {
        System.out.println("Entering OTP");
    }

    // Getters & Setters
    public String getKycStatus() { return kycStatus; }
    public void setKycStatus(String kycStatus) { this.kycStatus = kycStatus; }
    public int getCreditScore() { return creditScore; }
    public void setCreditScore(int creditScore) { this.creditScore = creditScore; }
    public double getIncome() { return income; }
    public void setIncome(double income) { this.income = income; }
    public String getSessionToken() { return sessionToken; }
    public void setSessionToken(String sessionToken) { this.sessionToken = sessionToken; }
    public int getFailedLoginAttempts() { return failedLoginAttempts; }
    public void setFailedLoginAttempts(int failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }
    public boolean isLocked() { return isLocked; }
    public void setLocked(boolean isLocked) { this.isLocked = isLocked; }

    public java.util.List<com.bank.ooad.models.accounts.BankAccount> getAccounts() { return accounts; }
    public void setAccounts(java.util.List<com.bank.ooad.models.accounts.BankAccount> accounts) { this.accounts = accounts; }
}
