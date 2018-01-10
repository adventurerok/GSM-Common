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
    NO_ACCOUNT,

    /**
     * This update itself would have succeeded, but a later update in the batch failed.
     */
    ROLLED_BACK,

    /**
     * This update was not tried because an earlier update failed
     */
    BATCH_FAILED,

    /**
     * This type of update is unsupported, or if this is in a batch,
     * something about the batch (such as using multiple currencies across different providers) is unsupported.
     */
    UNSUPPORTED

}
