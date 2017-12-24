package com.ithinkrok.msm.common.economy.result;

public class TransferResult {

    private final TransactionResult transactionResult;
    private final BalanceChange sendingBalanceChange;
    private final BalanceChange receivingBalanceChange;

    public TransferResult(TransactionResult transactionResult,
                          BalanceChange sendingBalanceChange,
                          BalanceChange receivingBalanceChange) {

        if(transactionResult == null) {
            throw new NullPointerException("transactionResult cannot be null");
        }

        if(sendingBalanceChange == null) {
            throw new NullPointerException("sendingBalanceChange cannot be null");
        }

        if(receivingBalanceChange == null) {
            throw new NullPointerException("receivingBalanceChange cannot be null");
        }

        this.transactionResult = transactionResult;
        this.sendingBalanceChange = sendingBalanceChange;
        this.receivingBalanceChange = receivingBalanceChange;
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
