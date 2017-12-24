package com.ithinkrok.msm.common.economy;

import com.ithinkrok.msm.common.economy.provider.EconomyProvider;

public class CurrencyNotProvidedException extends EconomyException {

    private final EconomyProvider provider;
    private final Currency currency;

    public CurrencyNotProvidedException(EconomyProvider provider, Currency currency) {
        super(currency.getName() + " is not provided by economy provider: " + provider.getName());
        this.provider = provider;
        this.currency = currency;
    }

    public EconomyProvider getProvider() {
        return provider;
    }

    public Currency getCurrency() {
        return currency;
    }
}
