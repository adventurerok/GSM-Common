package com.ithinkrok.msm.common.economy.result;

public class BalanceUpdateResult {

    private final TransactionResult transactionResult;

    private final BalanceChange balanceChange;


    public BalanceUpdateResult(TransactionResult transactionResult,
                               BalanceChange balanceChange) {
        if(transactionResult == null) {
            throw new NullPointerException("transactionResult cannot be null");
        }

        if(balanceChange == null) {
            throw new NullPointerException("balanceChange cannot be null");
        }

        this.transactionResult = transactionResult;
        this.balanceChange = balanceChange;
    }

    public TransactionResult getTransactionResult() {
        return transactionResult;
    }

    public BalanceChange getBalanceChange() {
        return balanceChange;
    }
}
