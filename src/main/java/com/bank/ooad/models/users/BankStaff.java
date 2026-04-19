package com.bank.ooad.models.users;

import com.bank.ooad.models.enums.StaffRole;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
public class BankStaff extends User {
    private String staffId;

    @Enumerated(EnumType.STRING)
    private StaffRole staffRole;

    public void reviewLoanApplication() {
        System.out.println("Staff reviewing loan application");
    }

    public void endorseLoan() {
        System.out.println("Staff endorsing loan");
    }

    public void approveLoan() {
        System.out.println("Staff approving loan");
    }

    public void rejectLoan() {
        System.out.println("Staff rejecting loan");
    }

    // Getters & Setters
    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }
    public StaffRole getStaffRole() { return staffRole; }
    public void setStaffRole(StaffRole staffRole) { this.staffRole = staffRole; }
}
