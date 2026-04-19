package com.bank.ooad.patterns.factory;

import com.bank.ooad.models.accounts.BankAccount;
import com.bank.ooad.models.accounts.CorporateAccount;
import com.bank.ooad.models.accounts.CurrentAccount;
import com.bank.ooad.models.accounts.SavingsAccount;
import com.bank.ooad.models.enums.AccountType;
import org.springframework.stereotype.Component;

@Component
public class BankAccountFactory {
    
    public BankAccount createAccount(AccountType type) {
        System.out.println("Factory creating account of type: " + type);
        switch (type) {
            case SAVINGS: return new SavingsAccount();
            case CURRENT: return new CurrentAccount();
            case CORPORATE: return new CorporateAccount();
            default: throw new IllegalArgumentException("Unknown account type");
        }
    }
}
