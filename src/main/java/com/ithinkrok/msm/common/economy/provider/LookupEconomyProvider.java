package com.ithinkrok.msm.common.economy.provider;

import com.ithinkrok.msm.common.economy.AccountIdentifier;
import com.ithinkrok.msm.common.economy.Currency;
import com.ithinkrok.msm.common.economy.Economy;
import com.ithinkrok.msm.common.economy.batch.Batch;
import com.ithinkrok.msm.common.economy.batch.BatchResult;
import com.ithinkrok.msm.common.economy.batch.Update;
import com.ithinkrok.msm.common.economy.batch.UpdateResult;
import com.ithinkrok.msm.common.economy.result.Balance;
import com.ithinkrok.msm.common.economy.result.BalanceUpdateResult;
import com.ithinkrok.msm.common.economy.result.MultiBalanceResult;
import com.ithinkrok.msm.common.economy.result.TransferResult;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;

public abstract class LookupEconomyProvider implements EconomyProvider {


    /**
     * Finds the EconomyProvider for the specified currency.
     *
     * @throws com.ithinkrok.msm.common.economy.CurrencyNotProvidedException if the currency cannot be found
     */
    protected abstract EconomyProvider lookupProviderForCurrency(Currency currency);


    @Override
    public boolean hasLocalAccount(AccountIdentifier account) {
        return lookupProviderForAccount(account).hasLocalAccount(account);
    }

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
        if(batch.getUpdates().isEmpty()) {
            consumer.accept(new BatchResult(Collections.emptyList(), true));
        }

        EconomyProvider provider = lookupProviderForAccount(batch.getUpdates().get(0).getAccount());

        for (Update update : batch.getUpdates()) {
            if(lookupProviderForAccount(update.getAccount()) != provider) {
                throw new UnsupportedOperationException("We don't support batch updates across multiple providers yet");
            }
        }

        provider.executeBatch(batch, reason, consumer);

        //TODO support for cross provider batches
//        Map<EconomyProvider, Batch> targets = new HashMap<>();
//
//        for (Update update : batch.getUpdates()) {
//            EconomyProvider provider = lookupProviderForAccount(update.getAccount());
//
//            Batch forProvider = targets.computeIfAbsent(provider, economyProvider -> new Batch());
//            forProvider.addUpdate(update);
//        }
//
//        Consumer<BatchResult> aggregateConsumer = new AggregateBatchConsumer(consumer, targets.size());
//
//        for (Map.Entry<EconomyProvider, Batch> entry : targets.entrySet()) {
//            entry.getKey().executeBatch(batch, reason, aggregateConsumer);
//        }
    }


    private EconomyProvider lookupProviderForAccount(AccountIdentifier account) {
        return lookupProviderForCurrency(account.getCurrency());
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
            if(!multiBalanceResult.isLastResult()) {
                target.accept(multiBalanceResult);
                return;
            }

            if(multiBalanceResult.isLastResult()) {
                ++lastResultsReceived;
            }

            if(lastResultsReceived == lastResultsExpected) {
                target.accept(multiBalanceResult);
            } else {
                target.accept(new MultiBalanceResult(multiBalanceResult.getBalances(), false));
            }
        }
    }

    // TODO implement support for cross provider Batches
//    private class AggregateBatchConsumer implements Consumer<BatchResult> {
//
//        final Consumer<BatchResult> target;
//        final int resultsExpected;
//
//        Collection<BatchResult> successfulResults = new ArrayList<>();
//
//        boolean failure;
//        int resultsRecieved;
//
//        public AggregateBatchConsumer(Consumer<BatchResult> target, int resultsExpected) {
//            this.target = target;
//            this.resultsExpected = resultsExpected;
//        }
//
//
//        @Override
//        public void accept(BatchResult batchResult) {
//
//        }
//    }
}
