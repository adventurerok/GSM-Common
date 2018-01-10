package com.ithinkrok.msm.common.economy.batch;

import com.ithinkrok.msm.common.economy.result.BalanceChange;
import com.ithinkrok.msm.common.economy.result.TransactionResult;

public class UpdateResult {

    private final Update update;
    private final TransactionResult result;
    private final BalanceChange change;


    public UpdateResult(Update update, TransactionResult result, BalanceChange change) {
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
