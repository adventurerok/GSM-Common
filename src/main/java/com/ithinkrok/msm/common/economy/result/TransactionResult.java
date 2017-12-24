package com.ithinkrok.msm.common.economy.result;

public enum TransactionResult {

    SUCCESS,
    FAILURE,
    NO_FUNDS,
    ACCOUNT_FULL,
    ACCOUNT_LOCKED,

    /**
     * Indicates that the UUID used for the transaction did not have an account and one was not created
     */
    NO_ACCOUNT

}
