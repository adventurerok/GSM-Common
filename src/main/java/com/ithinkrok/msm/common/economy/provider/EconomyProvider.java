package com.ithinkrok.msm.common.economy.provider;

import com.ithinkrok.msm.common.economy.Currency;
import com.ithinkrok.msm.common.economy.result.*;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public interface EconomyProvider {


    /**
     * Gets a collection of Currencies that have their accounts managed by this provider.
     * While all Currencies returned must be managed by this provider,
     * there may be currencies not in this Collection that may work with this provider.
     *
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

    default void getBalance(UUID uuid, Currency currency, Consumer<Balance> consumer) {
        consumer.accept(getBalance(uuid, currency).get());
    }


    /**
     * Attempts to get the balances of the specified UUIDs for the specified currencies.
     *
     * Will call the Consumer once all or some of the balances have been found, and may keep calling it.
     * The MultiBalanceResult will indicate whether there are more events coming or not.
     */
    void getBalances(Set<UUID> uuids, Set<Currency> currencies, Consumer<MultiBalanceResult> consumer);

    /**
     * @return The balance of the specified UUID in the currency, 0 if they have none, or
     * {@link Optional#EMPTY} if this check would require blocking.
     */
    Optional<Balance> getBalance(UUID uuid, Currency currency);

    /**
     * Deposits the amount into the account of the given UUID and currency.
     */
    void deposit(UUID uuid, Currency currency, BigDecimal amount, String reason,
                 Consumer<BalanceUpdateResult> consumer);

    /**
     * Withdraws amount from the account of the given UUID and currency.
     *
     * If insufficient funds are available in the account,
     * the consumer will be called with {@link TransactionResult#NO_FUNDS}.
     * There is no guarantee the account will have the required funds even after a call to getBalance.
     */
    void withdraw(UUID uuid, Currency currency, BigDecimal amount, String reason,
                  Consumer<BalanceUpdateResult> consumer);

    /**
     * Transfers money between from and to. This may run the consumer code on any thread that it wants.
     *  @param from UUID to transfer money from
     * @param to UUID to transfer money to
     * @param currency Currency to transfer
     * @param amount Amount to transfer
     * @param reason
     * @param consumer Consumer to send the result to
     */
    default void transfer(UUID from, UUID to, Currency currency, BigDecimal amount, String reason,
                  Consumer<TransferResult> consumer) {
        withdraw(from, currency, amount, reason, withRes -> {
            if (withRes.getTransactionResult() == TransactionResult.SUCCESS) {
                deposit(to, currency, amount, reason, depRes -> {

                    TransferResult result = new TransferResult(depRes.getTransactionResult(),
                                                               withRes.getBalanceChange(),
                                                               depRes.getBalanceChange());
                    consumer.accept(result);
                });
            } else {
                getBalance(to, currency, balance -> {
                    BalanceChange bc = BalanceChange.noBalanceChange(to, currency, balance.getAmount(), reason);
                    TransferResult result = new TransferResult(withRes.getTransactionResult(),
                                                               withRes.getBalanceChange(),
                                                               bc);
                    consumer.accept(result);
                });
            }
        });
    }

    /**
     * Sets the balance of the account for the given UUID and currency. Consumer code may be run on any thread.
     */
    void setBalance(UUID uuid, Currency currency, BigDecimal amount, String reason,
                    Consumer<BalanceUpdateResult> consumer);



    /**
     * @return A name for this economy provider
     */
    default String getName() {
        return getClass().getName();
    }
}
