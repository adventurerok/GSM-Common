package com.ithinkrok.msm.common.economy.provider;

import com.ithinkrok.msm.common.economy.Currency;
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
    public boolean hasLocalAccount(UUID uuid, Currency currency) {
        return lookupProviderForCurrency(currency).hasLocalAccount(uuid, currency);
    }

    @Override
    public void hasAccount(UUID uuid, Currency currency, Consumer<Boolean> consumer) {
        lookupProviderForCurrency(currency).hasAccount(uuid, currency, consumer);
    }

    @Override
    public Optional<Boolean> hasAccount(UUID uuid, Currency currency) {
        return lookupProviderForCurrency(currency).hasAccount(uuid, currency);
    }

    @Override
    public void getBalance(UUID uuid, Currency currency, Consumer<Balance> consumer) {
        lookupProviderForCurrency(currency).getBalance(uuid, currency, consumer);
    }

    @Override
    public Optional<Balance> getBalance(UUID uuid, Currency currency) {
        return lookupProviderForCurrency(currency).getBalance(uuid, currency);
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
    public void deposit(UUID uuid, Currency currency, BigDecimal amount, String reason,
                        Consumer<BalanceUpdateResult> consumer) {
        lookupProviderForCurrency(currency).deposit(uuid, currency, amount, reason, consumer);
    }

    @Override
    public void withdraw(UUID uuid, Currency currency, BigDecimal amount, String reason,
                         Consumer<BalanceUpdateResult> consumer) {
        lookupProviderForCurrency(currency).withdraw(uuid, currency, amount, reason, consumer);
    }

    @Override
    public void transfer(UUID from, UUID to, Currency currency, BigDecimal amount,
                         String reason,
                         Consumer<TransferResult> consumer) {
        lookupProviderForCurrency(currency).transfer(from, to, currency, amount, reason, consumer);
    }

    @Override
    public void setBalance(UUID uuid, Currency currency, BigDecimal amount, String reason,
                           Consumer<BalanceUpdateResult> consumer) {
        lookupProviderForCurrency(currency).setBalance(uuid, currency, amount, reason, consumer);
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
}
