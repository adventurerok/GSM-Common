package com.ithinkrok.msm.common.economy.result;

public class TransferResult {

    public static final TransferResult FAILURE = new TransferResult(TransactionResult.FAILURE, null, null);

    private final TransactionResult transactionResult;
    private final BalanceChange sendingBalanceChange;
    private final BalanceChange receivingBalanceChange;

    public TransferResult(TransactionResult transResult,
                          BalanceChange sendingChange,
                          BalanceChange receivingChange) {

        if (transResult == null) {
            throw new NullPointerException("transResult cannot be null");
        }

        if (transResult != TransactionResult.FAILURE && sendingChange == null) {
            throw new IllegalArgumentException("sendingChange cannot be null unless transResult is FAILURE");
        }

        if (transResult != TransactionResult.FAILURE && receivingChange == null) {
            throw new IllegalArgumentException("receivingChange cannot be null unless transResult is FAILURE");
        }

        this.transactionResult = transResult;
        this.sendingBalanceChange = sendingChange;
        this.receivingBalanceChange = receivingChange;
    }

    public TransactionResult getTransactionResult() {
        return transactionResult;
    }

    public BalanceChange getSendingBalanceChange() {
        return sendingBalanceChange;
    }

    public BalanceChange getReceivingBalanceChange() {
        return receivingBalanceChange;
    }
}
