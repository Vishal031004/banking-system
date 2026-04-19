package com.bank.ooad.models.accounts;

import com.bank.ooad.models.transactions.Statement;
import jakarta.persistence.Entity;

@Entity
public class CorporateAccount extends BankAccount {
    private String companyName;
    private String gstNumber;
    private String authorizedSignatory;

    @Override
    public Statement generateMonthlyStatement() {
        System.out.println("Generating Corporate Statement");
        return new Statement();
    }

    // Getters / Setters
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getGstNumber() { return gstNumber; }
    public void setGstNumber(String gstNumber) { this.gstNumber = gstNumber; }
    public String getAuthorizedSignatory() { return authorizedSignatory; }
    public void setAuthorizedSignatory(String authorizedSignatory) { this.authorizedSignatory = authorizedSignatory; }
}
