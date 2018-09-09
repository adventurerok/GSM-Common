package com.ithinkrok.msm.common.economy.provider;

import com.ithinkrok.msm.common.economy.AccountIdentifier;
import com.ithinkrok.msm.common.economy.Currency;
import com.ithinkrok.msm.common.economy.batch.Batch;
import com.ithinkrok.msm.common.economy.batch.BatchResult;
import com.ithinkrok.msm.common.economy.batch.Update;
import com.ithinkrok.msm.common.economy.batch.UpdateResult;
import com.ithinkrok.msm.common.economy.result.Balance;
import com.ithinkrok.msm.common.economy.result.BalanceChange;
import com.ithinkrok.msm.common.economy.result.MultiBalanceResult;
import com.ithinkrok.msm.common.economy.result.TransactionResult;
import com.ithinkrok.util.NullReplacements;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class LookupEconomyProvider implements EconomyProvider {


    @Override
    public boolean hasLocalAccount(AccountIdentifier account) {
        return lookupProviderForAccount(account).hasLocalAccount(account);
    }


    private EconomyProvider lookupProviderForAccount(AccountIdentifier account) {
        return lookupProviderForCurrency(account.getCurrency());
    }


    /**
     * Finds the EconomyProvider for the specified currency.
     *
     * @throws com.ithinkrok.msm.common.economy.CurrencyNotProvidedException if the currency cannot be found
     */
    protected abstract EconomyProvider lookupProviderForCurrency(Currency currency);


    @Override
    public void hasAccount(AccountIdentifier account, Consumer<Boolean> consumer) {
        lookupProviderForAccount(account).hasAccount(account, consumer);
    }


    @Override
    public Optional<Boolean> hasAccount(AccountIdentifier account) {
        return lookupProviderForAccount(account).hasAccount(account);
    }


    @Override
    public void getBalance(AccountIdentifier account, Consumer<Balance> consumer) {
        lookupProviderForAccount(account).getBalance(account, consumer);
    }


    @Override
    public Optional<Balance> getBalance(AccountIdentifier account) {
        return lookupProviderForAccount(account).getBalance(account);
    }


    @Override
    public void getBalances(Set<UUID> uuids, Set<Currency> allCurrencies, Consumer<MultiBalanceResult> consumer) {
        Map<EconomyProvider, Set<Currency>> targets = new HashMap<>();

        for (Currency currency : allCurrencies) {
            EconomyProvider provider = lookupProviderForCurrency(currency);

            Set<Currency> forProvider = targets.computeIfAbsent(provider, economyProvider -> new HashSet<>());
            forProvider.add(currency);
        }

        Consumer<MultiBalanceResult> aggregateConsumer = new AggregateMultiBalanceConsumer(consumer, targets.size());

        for (Map.Entry<EconomyProvider, Set<Currency>> entry : targets.entrySet()) {
            entry.getKey().getBalances(uuids, entry.getValue(), aggregateConsumer);
        }

    }


    @Override
    public void executeUpdate(Update update, String reason, Consumer<UpdateResult> consumer) {
        lookupProviderForAccount(update.getAccount()).executeUpdate(update, reason, consumer);
    }


    @Override
    public void executeBatch(Batch batch, String reason, Consumer<BatchResult> consumer) {
        if (batch.getUpdates().isEmpty()) {
            consumer.accept(new BatchResult(Collections.emptyList(), true));
        }

        if (executeBatchIfSameProvider(batch, reason, consumer)) {
            return;
        }

        executeBatchMultipleProviders(batch, reason, consumer);
    }


    void executeBatchMultipleProviders(Batch batch, String reason, Consumer<BatchResult> consumer) {
        Map<EconomyProvider, Batch> targets = new HashMap<>();

        for (Update update : batch.getUpdates()) {
            EconomyProvider provider = lookupProviderForAccount(update.getAccount());

            Batch forProvider = targets.computeIfAbsent(provider, economyProvider -> new Batch());
            forProvider.addUpdate(update);
        }

        AggregateBatchConsumer aggregateConsumer = new AggregateBatchConsumer(consumer, targets.size());

        //TODO it may be safer to execute local batches first, and only do global ones if local ones are successful

        for (Map.Entry<EconomyProvider, Batch> entry : targets.entrySet()) {
            entry.getKey().executeBatch(batch, reason, batchResult -> {
                aggregateConsumer.accept(entry.getKey(), batchResult);
            });
        }
    }


    boolean executeBatchIfSameProvider(Batch batch, String reason, Consumer<BatchResult> consumer) {
        EconomyProvider provider = lookupProviderForAccount(batch.getUpdates().get(0).getAccount());

        for (Update update : batch.getUpdates()) {
            if (lookupProviderForAccount(update.getAccount()) != provider) {
                return false;
            }
        }

        provider.executeBatch(batch, reason, consumer);
        return true;
    }


    private static final class AggregateMultiBalanceConsumer implements Consumer<MultiBalanceResult> {

        final Consumer<MultiBalanceResult> target;
        final int lastResultsExpected;

        int lastResultsReceived;


        private AggregateMultiBalanceConsumer(Consumer<MultiBalanceResult> target, int lastResultsExpected) {
            this.target = target;
            this.lastResultsExpected = lastResultsExpected;
        }


        @Override
        public void accept(MultiBalanceResult multiBalanceResult) {
            if (!multiBalanceResult.isLastResult()) {
                target.accept(multiBalanceResult);
                return;
            }

            if (multiBalanceResult.isLastResult()) {
                ++lastResultsReceived;
            }

            if (lastResultsReceived == lastResultsExpected) {
                target.accept(multiBalanceResult);
            } else {
                target.accept(new MultiBalanceResult(multiBalanceResult.getBalances(), false));
            }
        }
    }

    // TODO implement support for cross provider Batches
    private class AggregateBatchConsumer {

        final Consumer<BatchResult> target;
        final int resultsExpected;

        Map<EconomyProvider, BatchResult> successfulResults = new ConcurrentHashMap<>();
        Map<EconomyProvider, BatchResult> failedResults = new ConcurrentHashMap<>();

        AtomicBoolean sentResult = new AtomicBoolean(false);


        public AggregateBatchConsumer(Consumer<BatchResult> target, int resultsExpected) {
            this.target = target;
            this.resultsExpected = resultsExpected;
        }


        public void accept(EconomyProvider provider, BatchResult batchResult) {
            if (batchResult.wasSuccessful()) {
                successfulResults.put(provider, batchResult);
                if (!failedResults.isEmpty()) {
                    rollback();
                }
            } else {
                if (failedResults.isEmpty()) {
                    rollback();
                } else {
                    failedResults.put(provider, batchResult);
                }
            }

            if((successfulResults.size() == resultsExpected) && sentResult.compareAndSet(false, true)) {
                List<UpdateResult> results = successfulResults.values().stream()
                        .map(BatchResult::getResults)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());

                target.accept(new BatchResult(results, true));
            }

            if((failedResults.size() == resultsExpected) && sentResult.compareAndSet(false, true)) {
                List<UpdateResult> results = failedResults.values().stream()
                        .map(BatchResult::getResults)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());

                target.accept(new BatchResult(results, false));
            }
        }


        private void rollback() {
            for (EconomyProvider provider : successfulResults.keySet()) {
                successfulResults.computeIfPresent(provider, (economyProvider, batchResult) -> {
                    Batch rollback = Batch.rollback(batchResult);

                    //this could fail, but then we are truly fucked
                    provider.executeBatch(rollback, "rollback LookupEconomyProvider",
                                          NullReplacements.nullConsumer());


                    List<UpdateResult> rollbackChanges = batchResult.getResults().stream()
                            .map(update ->
                                 {
                                     BalanceChange noChange = BalanceChange.noBalanceChange(
                                             update.getChange().getAccount(),
                                             update.getChange().getOldBalance(),
                                             "rolled back");

                                     return new UpdateResult(update.getUpdate(),
                                                             TransactionResult.ROLLED_BACK,
                                                             noChange);
                                 })
                            .collect(Collectors.toList());

                    failedResults.put(provider, new BatchResult(rollbackChanges, false));

                    return null;
                });
            }

        }
    }
}
