package com.ithinkrok.msm.common.economy.batch;

import com.ithinkrok.msm.common.economy.result.BalanceChange;
import com.ithinkrok.msm.common.economy.result.TransactionResult;

import java.util.Objects;

public class UpdateResult {

    private final Update update;
    private final TransactionResult result;
    private final BalanceChange change;


    public UpdateResult(Update update, TransactionResult result, BalanceChange change) {
        Objects.requireNonNull(update, "update cannot be null");
        Objects.requireNonNull(result, "result cannot be null");
        //change can be null if we failed to lookup balance

        this.update = update;
        this.result = result;
        this.change = change;
    }


    public Update getUpdate() {
        return update;
    }


    public TransactionResult getResult() {
        return result;
    }


    public BalanceChange getChange() {
        return change;
    }
}
