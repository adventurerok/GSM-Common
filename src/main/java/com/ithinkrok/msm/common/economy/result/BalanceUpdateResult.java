package com.ithinkrok.msm.common.economy.result;

public class BalanceUpdateResult {

    public static final BalanceUpdateResult FAILURE = new BalanceUpdateResult(TransactionResult.FAILURE, null);

    private final TransactionResult transactionResult;

    private final BalanceChange balanceChange;


    public BalanceUpdateResult(TransactionResult transactionResult,
                               BalanceChange balanceChange) {
        if(transactionResult == null) {
            throw new NullPointerException("transactionResult cannot be null");
        }

        if(transactionResult != TransactionResult.FAILURE && balanceChange == null) {
            throw new IllegalArgumentException("balanceChange cannot be null unless transactionResult is FAILURE");
        }

        this.transactionResult = transactionResult;
        this.balanceChange = balanceChange;
    }

    /**
     * @return The result status of the transaction. Cannot be null.
     */
    public TransactionResult getTransactionResult() {
        return transactionResult;
    }

    /**
     * @return The balance change. Can only be null if the TransactionResult is {@link TransactionResult#FAILURE}
     */
    public BalanceChange getBalanceChange() {
        return balanceChange;
    }
}
