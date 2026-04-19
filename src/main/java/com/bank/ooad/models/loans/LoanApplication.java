package com.bank.ooad.models.loans;

import com.bank.ooad.models.enums.LoanStatus;
import com.bank.ooad.models.enums.RiskLevel;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class LoanApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String applicationId;
    
    private String customerId;
    private double loanAmount;
    private int tenure;
    private String purpose;
    
    @Enumerated(EnumType.STRING)
    private LoanStatus status;
    
    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;
    
    private int creditScore;
    private double emi;
    private LocalDateTime submittedAt;

    public void submitApplication() {
        System.out.println("Submitting loan application");
        this.status = LoanStatus.SUBMITTED;
    }

    public void startOCR() {
        System.out.println("Starting OCR process");
    }

    public void extractFinancialData() {
        System.out.println("Extracting financial data");
    }

    public double calculateEmi() {
        System.out.println("Calculating EMI");
        return 1000.0;
    }

    public RiskLevel performRiskAssessment() {
        System.out.println("Performing risk assessment");
        return RiskLevel.MEDIUM;
    }

    public void autoReject() {
        this.status = LoanStatus.REJECTED;
        System.out.println("Auto rejecting loan");
    }

    public void officerEndorse() {
        this.status = LoanStatus.PENDING_MANAGER;
        System.out.println("Officer endorsing loan");
    }

    public void officerReject() {
        this.status = LoanStatus.REJECTED;
        System.out.println("Officer rejecting loan");
    }

    public void managerApprove() {
        this.status = LoanStatus.APPROVED;
        System.out.println("Manager approving loan");
    }

    public void managerReject() {
        this.status = LoanStatus.REJECTED;
        System.out.println("Manager rejecting loan");
    }

    public void disburseFunds() {
        this.status = LoanStatus.DISBURSED;
        System.out.println("Disbursing funds to customer");
    }

    // Getters / Setters
    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public double getLoanAmount() { return loanAmount; }
    public void setLoanAmount(double loanAmount) { this.loanAmount = loanAmount; }
    public int getTenure() { return tenure; }
    public void setTenure(int tenure) { this.tenure = tenure; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public LoanStatus getStatus() { return status; }
    public void setStatus(LoanStatus status) { this.status = status; }
    public RiskLevel getRiskLevel() { return riskLevel; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }
    public int getCreditScore() { return creditScore; }
    public void setCreditScore(int creditScore) { this.creditScore = creditScore; }
    public double getEmi() { return emi; }
    public void setEmi(double emi) { this.emi = emi; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
}
