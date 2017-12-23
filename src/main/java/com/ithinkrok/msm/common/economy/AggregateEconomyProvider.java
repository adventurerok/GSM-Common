package com.ithinkrok.msm.common.economy;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class AggregateEconomyProvider implements EconomyProvider {

    private final String name;

    private final Map<Currency, EconomyProvider> subProviders = new ConcurrentHashMap<>();

    public AggregateEconomyProvider(String name) {
        this.name = name;
    }

    /**
     * Adds the given EconomyProvider as a provider
     * for all the currencies specified by {@link EconomyProvider#getManagedCurrencies()}
     */
    public void addProvider(EconomyProvider provider) {
        for (Currency currency : provider.getManagedCurrencies()) {
            subProviders.put(currency, provider);
        }
    }


    @Override
    public Set<Currency> getManagedCurrencies() {
        return subProviders.keySet();
    }

    @Override
    public boolean hasLocalAccount(UUID uuid, Currency currency) {
        currencyCheck(currency);

        return subProviders.get(currency).hasLocalAccount(uuid, currency);
    }

    @Override
    public Optional<Boolean> hasAccount(UUID uuid, Currency currency) {
        currencyCheck(currency);

        return subProviders.get(currency).hasAccount(uuid, currency);
    }

    @Override
    public void hasAccount(UUID uuid, Currency currency, Consumer<Boolean> consumer) {
        currencyCheck(currency);

        subProviders.get(currency).hasAccount(uuid, currency, consumer);
    }

    @Override
    public Optional<BigDecimal> getBalance(UUID uuid, Currency currency) {
        currencyCheck(currency);

        return subProviders.get(currency).getBalance(uuid, currency);
    }

    @Override
    public void getBalance(UUID uuid, Currency currency, Consumer<BigDecimal> consumer) {
        currencyCheck(currency);

        subProviders.get(currency).getBalance(uuid, currency, consumer);
    }


    @Override
    public void deposit(UUID uuid, Currency currency, BigDecimal amount, Consumer<TransactionResult> consumer) {
        currencyCheck(currency);

        subProviders.get(currency).deposit(uuid, currency, amount, consumer);
    }

    @Override
    public void withdraw(UUID uuid, Currency currency, BigDecimal amount, Consumer<TransactionResult> consumer) {
        currencyCheck(currency);

        subProviders.get(currency).withdraw(uuid, currency, amount, consumer);
    }

    @Override
    public void transfer(UUID from, UUID to, Currency currency, BigDecimal amount,
                         Consumer<TransactionResult> consumer) {

        currencyCheck(currency);

        subProviders.get(currency).transfer(from, to, currency, amount, consumer);
    }

    @Override
    public void setBalance(UUID uuid, Currency currency, BigDecimal amount, Consumer<TransactionResult> consumer) {
        currencyCheck(currency);

        subProviders.get(currency).setBalance(uuid, currency, amount, consumer);
    }

    private void currencyCheck(Currency currency) {
        if(!subProviders.containsKey(currency)) {
            throw new CurrencyNotProvidedException(this, currency);
        }
    }

    @Override
    public String getName() {
        return "AggregateEconomyProvider: " + name;
    }
}
