package com.ithinkrok.msm.common.economy.batch;

public enum UpdateType {

    /**
     * Deposit an amount into an account
     */
    DEPOSIT,

    /**
     * Withdraw an amount from an account
     */
    WITHDRAW,

    /**
     * Set the balance of an account
     */
    SET,

    /**
     * Check if the balance of the account is above the figure
     */
    CHECK

}
