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

    default void deposit(UUID uuid, Currency currency, BigDecimal amount, Consumer<TransactionResult> consumer) {
        consumer.accept(deposit(uuid, currency, amount).get());
    }

    /**
     * Deposits an amount into an account.
     * Will not do anything if blocking would be required.
     */
    Optional<TransactionResult> deposit(UUID uuid, Currency currency, BigDecimal amount);

    default void withdraw(UUID uuid, Currency currency, BigDecimal amount, Consumer<TransactionResult> consumer) {
        consumer.accept(withdraw(uuid, currency, amount).get());
    }

    /**
     * Withdraws an amount from an account.
     * Will not do anything if blocking would be required.
     */
    Optional<TransactionResult> withdraw(UUID uuid, Currency currency, BigDecimal amount);

    default void transfer(UUID from, UUID to, Currency currency, BigDecimal amount,
                          Consumer<TransactionResult> consumer) {
        consumer.accept(transfer(from, to, currency, amount).get());
    }

    /**
     * Transfers some balance between two accounts.
     * Will not do anything if blocking would be required.
     */
    Optional<TransactionResult> transfer(UUID from, UUID to, Currency currency, BigDecimal amount);

    default void setBalance(UUID uuid, Currency currency, BigDecimal amount, Consumer<TransactionResult> consumer) {
        consumer.accept(setBalance(uuid, currency, amount).get());
    }

    /**
     * Sets the balance of an account.
     * Will not do anything if blocking would be required.
     */
    Optional<TransactionResult> setBalance(UUID uuid, Currency currency, BigDecimal amount);


    /**
     * @return A name for this economy provider
     */
    default String getName() {
        return getClass().getName();
    }
}
