package com.ithinkrok.msm.common.economy.batch;

import com.ithinkrok.msm.common.economy.AccountIdentifier;

import java.math.BigDecimal;

public class Update {

    private final AccountIdentifier account;
    private final UpdateType updateType;
    private final BigDecimal amount;


    public Update(AccountIdentifier account, UpdateType updateType, BigDecimal amount) {
        this.account = account;
        this.updateType = updateType;
        this.amount = amount;
    }


    public AccountIdentifier getAccount() {
        return account;
    }


    public UpdateType getUpdateType() {
        return updateType;
    }


    public BigDecimal getAmount() {
        return amount;
    }
}
