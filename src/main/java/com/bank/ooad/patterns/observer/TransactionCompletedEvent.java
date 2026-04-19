package com.bank.ooad.patterns.observer;

import com.bank.ooad.models.transactions.Transaction;
import org.springframework.context.ApplicationEvent;

public class TransactionCompletedEvent extends ApplicationEvent {
    private final Transaction transaction;

    public TransactionCompletedEvent(Object source, Transaction transaction) {
        super(source);
        this.transaction = transaction;
    }

    public Transaction getTransaction() {
        return transaction;
    }
}
