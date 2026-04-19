package com.bank.ooad.models.transactions;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bank_statement")
public class Statement {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String statementId;
    
    private String accountId;
    private int month;
    private int year;
    
    // In UML it references List<Transaction>
    @OneToMany(mappedBy = "statement")
    private List<Transaction> transactions;
    
    private LocalDateTime generatedAt;

    public java.io.File generatePDF() {
        System.out.println("Generating PDF for statement");
        return null;
    }

    public void download() {
        System.out.println("Downloading statement");
    }

    public void print() {
        System.out.println("Printing statement");
    }

    // Getters / Setters
    public String getStatementId() { return statementId; }
    public void setStatementId(String statementId) { this.statementId = statementId; }
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    public List<Transaction> getTransactions() { return transactions; }
    public void setTransactions(List<Transaction> transactions) { this.transactions = transactions; }
}
