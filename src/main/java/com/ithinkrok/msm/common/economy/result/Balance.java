package com.ithinkrok.msm.common.economy.result;

import com.ithinkrok.msm.common.economy.Currency;

import java.math.BigDecimal;
import java.util.UUID;

public class Balance {

    private final UUID account;
    private final Currency currency;
    private final BigDecimal amount;

    public Balance(UUID account, Currency currency, BigDecimal amount) {
        this.account = account;
        this.currency = currency;
        this.amount = amount;
    }

    public UUID getAccount() {
        return account;
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
