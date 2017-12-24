package com.ithinkrok.msm.common.economy;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public interface EconomyProvider {


    /**
     * @return A collection of Currencies that this EconomyProvider manages.
     */
    Set<Currency> getManagedCurrencies();

    /**
     * Returns true the user has an account and access can be made to it without blocking.
     *
     * @return If the user has a local account for this currency
     */
    boolean hasLocalAccount(UUID uuid, Currency currency);

    default void hasAccount(UUID uuid, Currency currency, Consumer<Boolean> consumer) {
        consumer.accept(hasAccount(uuid, currency).get());
    }

    /**
     * Will be empty if doing this check would require blocking.
     * However, this should not require blocking if the user specified is currently connected to this server.
     *
     * @return An Optional depicting whether the UUID specified has an account
     */
    Optional<Boolean> hasAccount(UUID uuid, Currency currency);

    default void getBalance(UUID uuid, Currency currency, Consumer<BigDecimal> consumer) {
        consumer.accept(getBalance(uuid, currency).get());
    }

    /**
     * @return The balance of the specified UUID in the currency, 0 if they have none, or
     * {@link Optional#EMPTY} if this check would require blocking.
     */
    Optional<BigDecimal> getBalance(UUID uuid, Currency currency);

    /**
     * Deposits the amount into the account of the given UUID and currency.
     */
    void deposit(UUID uuid, Currency currency, BigDecimal amount, String reason,
                 Consumer<TransactionResult> consumer);

    /**
     * Withdraws amount from the account of the given UUID and currency.
     *
     * If insufficient funds are available in the account,
     * the consumer will be called with {@link TransactionResult#NO_FUNDS}.
     * There is no guarantee the account will have the required funds even after a call to getBalance.
     */
    void withdraw(UUID uuid, Currency currency, BigDecimal amount, String reason,
                  Consumer<TransactionResult> consumer);

    /**
     * Transfers money between from and to. This may run the consumer code on any thread that it wants.
     *  @param from UUID to transfer money from
     * @param to UUID to transfer money to
     * @param currency Currency to transfer
     * @param amount Amount to transfer
     * @param reason
     * @param consumer Consumer to send the result to
     */
    void transfer(UUID from, UUID to, Currency currency, BigDecimal amount, String reason,
                  Consumer<TransactionResult> consumer);

    /**
     * Sets the balance of the account for the given UUID and currency. Consumer code may be run on any thread.
     */
    void setBalance(UUID uuid, Currency currency, BigDecimal amount, String reason,
                    Consumer<TransactionResult> consumer);



    /**
     * @return A name for this economy provider
     */
    default String getName() {
        return getClass().getName();
    }
}
