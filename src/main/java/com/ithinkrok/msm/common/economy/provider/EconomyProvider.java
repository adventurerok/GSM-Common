package com.ithinkrok.msm.common.economy.provider;

import com.ithinkrok.msm.common.economy.AccountIdentifier;
import com.ithinkrok.msm.common.economy.Currency;
import com.ithinkrok.msm.common.economy.batch.Batch;
import com.ithinkrok.msm.common.economy.batch.BatchResult;
import com.ithinkrok.msm.common.economy.batch.Update;
import com.ithinkrok.msm.common.economy.batch.UpdateResult;
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
    boolean hasLocalAccount(AccountIdentifier account);

    default void hasAccount(AccountIdentifier account, Consumer<Boolean> consumer) {
        consumer.accept(hasAccount(account).get());
    }

    /**
     * Will be empty if doing this check would require blocking.
     * However, this should not require blocking if the user specified is currently connected to this server.
     *
     * @return An Optional depicting whether the UUID specified has an account
     */
    Optional<Boolean> hasAccount(AccountIdentifier account);

    default void getBalance(AccountIdentifier account, Consumer<Balance> consumer) {
        consumer.accept(getBalance(account).get());
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
    Optional<Balance> getBalance(AccountIdentifier account);

    /**
     * Perform the specified update for the given reason, giving the result to the consumer
     */
    default void executeUpdate(Update update, String reason, Consumer<UpdateResult> consumer) {
        executeBatch(new Batch(update), reason, batchResult -> {
            consumer.accept(batchResult.getResults().get(0));
        });
    }

    /**
     * Do all of the specified updates in the batch, giving the result to the consumer.
     *
     * Either all of the updates will succeed, or none of them will.
     * (If one fails, we should rollback the others).
     */
    void executeBatch(Batch batch, String reason, Consumer<BatchResult> consumer);

    /**
     * @return A name for this economy provider
     */
    default String getName() {
        return getClass().getName();
    }
}
