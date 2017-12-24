package com.ithinkrok.msm.common.economy.provider;

import com.ithinkrok.msm.common.economy.Currency;
import com.ithinkrok.msm.common.economy.CurrencyNotProvidedException;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AggregateEconomyProvider extends LookupEconomyProvider {

    private final String name;

    private final Map<com.ithinkrok.msm.common.economy.Currency, EconomyProvider> subProviders = new ConcurrentHashMap<>();

    public AggregateEconomyProvider(String name) {
        this.name = name;
    }

    /**
     * Adds the given EconomyProvider as a provider
     * for all the currencies specified by {@link EconomyProvider#getManagedCurrencies()}
     */
    public void addProvider(EconomyProvider provider) {
        for (com.ithinkrok.msm.common.economy.Currency currency : provider.getManagedCurrencies()) {
            subProviders.put(currency, provider);
        }
    }


    @Override
    public Set<com.ithinkrok.msm.common.economy.Currency> getManagedCurrencies() {
        return subProviders.keySet();
    }


    @Override
    protected EconomyProvider lookupProviderForCurrency(Currency currency) {
        if(!subProviders.containsKey(currency)) {
            throw new CurrencyNotProvidedException(this, currency);
        }

        return subProviders.get(currency);
    }

    @Override
    public String getName() {
        return "AggregateEconomyProvider: " + name;
    }
}
