package com.ithinkrok.msm.common.economy.batch;

import com.ithinkrok.msm.common.economy.AccountIdentifier;
import com.ithinkrok.msm.common.economy.result.BalanceChange;

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


    /**
     * Creates an Update that would rollback the specified BalanceChange.
     *
     * @return the rollback update, or null if there is no rollback possible for the balance change.
     */
    public static Update rollback(BalanceChange balanceChange) {
        if(balanceChange.getChange().compareTo(BigDecimal.ZERO) > 0) {
            return new Update(balanceChange.getAccount(), UpdateType.WITHDRAW, balanceChange.getChange());

        } else if(balanceChange.getChange().compareTo(BigDecimal.ZERO) < 0) {
            return new Update(balanceChange.getAccount(), UpdateType.DEPOSIT, balanceChange.getChange().negate());

        } else {
            //nothing to do to rollback
            return null;
        }
    }
}
