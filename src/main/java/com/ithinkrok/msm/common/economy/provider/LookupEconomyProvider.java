package com.ithinkrok.msm.common.economy.provider;

import com.ithinkrok.msm.common.economy.Currency;
import com.ithinkrok.msm.common.economy.result.BalanceUpdateResult;
import com.ithinkrok.msm.common.economy.result.TransferResult;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
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
    public void getBalance(UUID uuid, Currency currency,
                           Consumer<BigDecimal> consumer) {
        lookupProviderForCurrency(currency).getBalance(uuid, currency, consumer);
    }

    @Override
    public Optional<BigDecimal> getBalance(UUID uuid,
                                           Currency currency) {
        return lookupProviderForCurrency(currency).getBalance(uuid, currency);
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
}
