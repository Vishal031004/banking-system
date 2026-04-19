package com.bank.ooad.patterns.observer;

import com.bank.ooad.models.loans.LoanApplication;
import org.springframework.context.ApplicationEvent;

public class LoanStatusEvent extends ApplicationEvent {
    private final LoanApplication loanApplication;
    private final String message;

    public LoanStatusEvent(Object source, LoanApplication loanApplication, String message) {
        super(source);
        this.loanApplication = loanApplication;
        this.message = message;
    }

    public LoanApplication getLoanApplication() {
        return loanApplication;
    }

    public String getMessage() {
        return message;
    }
}
