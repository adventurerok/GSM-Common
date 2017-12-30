package com.ithinkrok.msm.common.economy.provider;

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

    private final Map<UUIDAndCurrency, BigDecimal> store = new ConcurrentHashMap<>();

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
    public boolean hasLocalAccount(UUID uuid, Currency currency) {
        checkCurrency(currency);

        return store.containsKey(key(uuid, currency));
    }

    private UUIDAndCurrency key(UUID uuid, Currency currency) {
        return new UUIDAndCurrency(uuid, currency.getName());
    }

    @Override
    public Optional<Boolean> hasAccount(UUID uuid, Currency currency) {
        return Optional.of(hasLocalAccount(uuid, currency));
    }

    @Override
    public void getBalances(Set<UUID> uuids, Set<Currency> currencies, Consumer<MultiBalanceResult> consumer) {
        currencies.forEach(this::checkCurrency);

        List<Balance> balances = new ArrayList<>(uuids.size() * currencies.size());

        for (Currency currency : currencies) {
            for (UUID uuid : uuids) {
                BigDecimal amount = store.getOrDefault(key(uuid, currency), BigDecimal.ZERO);

                balances.add(new Balance(uuid, currency, amount));
            }

        }

        consumer.accept(new MultiBalanceResult(balances, true));
    }

    @Override
    public Optional<Balance> getBalance(UUID uuid, Currency currency) {
        checkCurrency(currency);

        BigDecimal amount = store.getOrDefault(key(uuid, currency), BigDecimal.ZERO);

        return Optional.of(new Balance(uuid, currency, amount));
    }

    @Override
    public synchronized void deposit(UUID uuid, Currency currency, BigDecimal amount, String reason,
                        Consumer<BalanceUpdateResult> consumer) {
        checkCurrency(currency);

        BigDecimal oldBalance = store.getOrDefault(key(uuid, currency), BigDecimal.ZERO);
        BigDecimal newBalance = oldBalance.add(amount);

        store.put(key(uuid, currency), newBalance);

        BalanceChange change = BalanceChange.fromOldAndNew(uuid, currency, oldBalance, newBalance, reason);

        consumer.accept(new BalanceUpdateResult(TransactionResult.SUCCESS, change));
    }

    @Override
    public synchronized void withdraw(UUID uuid, Currency currency, BigDecimal amount, String reason,
                         Consumer<BalanceUpdateResult> consumer) {
        checkCurrency(currency);

        BigDecimal oldBalance = store.getOrDefault(key(uuid, currency), BigDecimal.ZERO);

        BigDecimal newBalance = oldBalance.subtract(amount);

        if(newBalance.compareTo(BigDecimal.ZERO) < 0) {
            BalanceChange change = BalanceChange.noBalanceChange(uuid, currency, oldBalance, reason);

            consumer.accept(new BalanceUpdateResult(TransactionResult.NO_FUNDS, change));
        } else {
            store.put(key(uuid, currency), newBalance);

            BalanceChange change = BalanceChange.fromOldAndNew(uuid, currency, oldBalance, newBalance, reason);

            consumer.accept(new BalanceUpdateResult(TransactionResult.SUCCESS, change));
        }
    }

    @Override
    public synchronized void transfer(UUID from, UUID to, Currency currency, BigDecimal amount, String reason,
                         Consumer<TransferResult> consumer) {
        EconomyProvider.super.transfer(from, to, currency, amount, reason, consumer);
    }

    @Override
    public synchronized void setBalance(UUID uuid, Currency currency, BigDecimal amount, String reason,
                           Consumer<BalanceUpdateResult> consumer) {
        checkCurrency(currency);

        BigDecimal oldBalance = store.getOrDefault(key(uuid, currency), BigDecimal.ZERO);

        store.put(key(uuid, currency), amount);

        BalanceChange change = BalanceChange.fromOldAndNew(uuid, currency, oldBalance, amount, reason);

        consumer.accept(new BalanceUpdateResult(TransactionResult.SUCCESS, change));

    }

    public void clearBalances(Currency currency) {
        checkCurrency(currency);

        store.keySet().removeIf(uuidAndCurrency -> {
           return uuidAndCurrency.currency.equals(currency.getName());
        });
    }

    public void clearAllBalances() {
        store.clear();
    }

    private final class UUIDAndCurrency {

        UUID uuid;
        String currency;

        public UUIDAndCurrency(UUID uuid, String currency) {
            this.uuid = uuid;
            this.currency = currency;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UUIDAndCurrency that = (UUIDAndCurrency) o;
            return Objects.equals(uuid, that.uuid) &&
                   Objects.equals(currency, that.currency);
        }

        @Override
        public int hashCode() {

            return Objects.hash(uuid, currency);
        }
    }
}
