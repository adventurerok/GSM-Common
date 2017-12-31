package com.ithinkrok.msm.common.economy;

import com.ithinkrok.msm.common.economy.provider.EconomyProvider;
import com.ithinkrok.msm.common.economy.result.Balance;
import com.ithinkrok.msm.common.economy.result.BalanceUpdateResult;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public interface Account {

    /**
     * Lookup a currency in this account's context
     *
     * @param name The name of the currency
     * @return The currency, or null if one isn't found
     */
    default Currency lookupCurrency(String name) {
        return getContext().lookupCurrency(name);
    }

    EconomyContext getContext();

    default void hasBalance(Currency currency, Consumer<Boolean> consumer) {
        getProvider().hasAccount(getUUID(), currency, consumer);
    }

    /**
     * @return The {@link EconomyProvider} used by this account
     */
    EconomyProvider getProvider();

    /**
     * @return The UUID of this account
     */
    UUID getUUID();

    default Optional<Boolean> hasBalance(Currency currency) {
        return getProvider().hasAccount(getUUID(), currency);
    }

    default void getBalance(Currency currency, Consumer<Balance> consumer) {
        getProvider().getBalance(getUUID(), currency, consumer);
    }

    default Optional<Balance> getBalance(Currency currency) {
        return getProvider().getBalance(getUUID(), currency);
    }


    default void deposit(Currency currency, BigDecimal amount, String reason, Consumer<BalanceUpdateResult> consumer) {
        getProvider().deposit(getUUID(), currency, amount, reason, consumer);
    }

    default void withdraw(Currency currency, BigDecimal amount, String reason, Consumer<BalanceUpdateResult> consumer) {
        getProvider().withdraw(getUUID(), currency, amount, reason, consumer);
    }

    default void setBalance(Currency currency, BigDecimal amount, String reason,
                            Consumer<BalanceUpdateResult> consumer) {
        getProvider().setBalance(getUUID(), currency, amount, reason, consumer);
    }

}
