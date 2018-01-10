package com.ithinkrok.msm.common.economy.provider;

import com.ithinkrok.msm.common.economy.AccountIdentifier;
import com.ithinkrok.msm.common.economy.Currency;
import com.ithinkrok.msm.common.economy.CurrencyNotProvidedException;
import com.ithinkrok.msm.common.economy.EconomyContext;
import com.ithinkrok.msm.common.economy.result.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class MemoryEconomyProvider implements EconomyProvider {


    private final EconomyContext context;

    private final Map<AccountIdentifier, BigDecimal> store = new ConcurrentHashMap<>();

    public MemoryEconomyProvider(EconomyContext context) {
        this.context = context;
    }

    @Override
    public Set<Currency> getManagedCurrencies() {
        return Collections.emptySet();
    }

    private void checkCurrency(Currency currency) {
        if(currency.getContext() != context) {
            throw new CurrencyNotProvidedException(this, currency);
        }
    }

    @Override
    public boolean hasLocalAccount(AccountIdentifier account) {
        checkCurrency(account.getCurrency());

        return store.containsKey(account);
    }

    @Override
    public Optional<Boolean> hasAccount(AccountIdentifier account) {
        return Optional.of(hasLocalAccount(account));
    }

    @Override
    public void getBalances(Set<UUID> uuids, Set<Currency> currencies, Consumer<MultiBalanceResult> consumer) {
        currencies.forEach(this::checkCurrency);

        List<Balance> balances = new ArrayList<>(uuids.size() * currencies.size());

        for (Currency currency : currencies) {
            for (UUID uuid : uuids) {
                BigDecimal amount = store.getOrDefault(new AccountIdentifier(uuid, currency), BigDecimal.ZERO);

                balances.add(new Balance(new AccountIdentifier(uuid, currency), amount));
            }

        }

        consumer.accept(new MultiBalanceResult(balances, true));
    }

    @Override
    public Optional<Balance> getBalance(AccountIdentifier account) {
        checkCurrency(account.getCurrency());

        BigDecimal amount = store.getOrDefault(account, BigDecimal.ZERO);

        return Optional.of(new Balance(account, amount));
    }


    public void clearBalances(Currency currency) {
        checkCurrency(currency);

        store.keySet().removeIf(uuidAndCurrency -> {
           return uuidAndCurrency.getCurrency().getName().equals(currency.getName());
        });
    }

    public void clearAllBalances() {
        store.clear();
    }

}
