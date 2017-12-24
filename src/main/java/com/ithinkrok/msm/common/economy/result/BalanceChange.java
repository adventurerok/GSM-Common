package com.ithinkrok.msm.common.economy.result;

import com.ithinkrok.msm.common.economy.Currency;

import java.math.BigDecimal;
import java.util.UUID;

public class BalanceChange {

    private final UUID account;
    private final Currency currency;
    private final BigDecimal oldBalance;
    private final BigDecimal newBalance;
    private final BigDecimal change;
    private final String reason;

    public BalanceChange(UUID account, Currency currency, BigDecimal oldBalance, BigDecimal newBalance,
                         BigDecimal change, String reason) {
        this.account = account;
        this.currency = currency;
        this.oldBalance = oldBalance;
        this.newBalance = newBalance;
        this.change = change;
        this.reason = reason;
    }

    public static BalanceChange fromOldAndNew(UUID account, Currency currency,
                                              BigDecimal oldBalance, BigDecimal newBalance,
                                              String reason) {
        return new BalanceChange(account, currency, oldBalance, newBalance, newBalance.subtract(oldBalance), reason);
    }

    public static BalanceChange fromNewAndChange(UUID account, Currency currency,
                                              BigDecimal newBalance, BigDecimal change,
                                              String reason) {
        return new BalanceChange(account, currency, newBalance.subtract(change), newBalance, change, reason);
    }

    public static BalanceChange fromOldAndChange(UUID account, Currency currency,
                                                 BigDecimal oldBalance, BigDecimal change,
                                                 String reason) {
        return new BalanceChange(account, currency, oldBalance, oldBalance.add(change), change, reason);
    }

    public UUID getAccount() {
        return account;
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getOldBalance() {
        return oldBalance;
    }

    public BigDecimal getNewBalance() {
        return newBalance;
    }

    public BigDecimal getChange() {
        return change;
    }

    public String getReason() {
        return reason;
    }
}
