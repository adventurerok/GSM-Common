package com.ithinkrok.msm.common.economy.batch;

import java.math.BigDecimal;

public class Update {

    private final Account account;
    private final UpdateType updateType;
    private final BigDecimal amount;


    public Update(Account account, UpdateType updateType, BigDecimal amount) {
        this.account = account;
        this.updateType = updateType;
        this.amount = amount;
    }


    public Account getAccount() {
        return account;
    }


    public UpdateType getUpdateType() {
        return updateType;
    }


    public BigDecimal getAmount() {
        return amount;
    }
}
