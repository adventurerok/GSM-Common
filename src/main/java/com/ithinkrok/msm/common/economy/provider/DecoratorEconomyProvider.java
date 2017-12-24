package com.ithinkrok.msm.common.economy.provider;

import com.ithinkrok.msm.common.economy.Currency;
import com.ithinkrok.msm.common.economy.result.Balance;
import com.ithinkrok.msm.common.economy.result.BalanceUpdateResult;
import com.ithinkrok.msm.common.economy.result.MultiBalanceResult;
import com.ithinkrok.msm.common.economy.result.TransferResult;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class DecoratorEconomyProvider implements EconomyProvider {

    private EconomyProvider decorating;

    public DecoratorEconomyProvider(EconomyProvider decorating) {
        this.decorating = decorating;
    }

    protected void setDecorating(EconomyProvider decorating) {
        this.decorating = decorating;
    }

    @Override
    public Set<Currency> getManagedCurrencies() {
        return decorating.getManagedCurrencies();
    }

    @Override
    public boolean hasLocalAccount(UUID uuid, Currency currency) {
        return decorating.hasLocalAccount(uuid, currency);
    }

    @Override
    public void hasAccount(UUID uuid, Currency currency, Consumer<Boolean> consumer) {
        decorating.hasAccount(uuid, currency, consumer);
    }

    @Override
    public Optional<Boolean> hasAccount(UUID uuid, Currency currency) {
        return decorating.hasAccount(uuid, currency);
    }

    @Override
    public void getBalance(UUID uuid, Currency currency, Consumer<Balance> consumer) {
        decorating.getBalance(uuid, currency, consumer);
    }

    @Override
    public Optional<Balance> getBalance(UUID uuid, Currency currency) {
        return decorating.getBalance(uuid, currency);
    }

    @Override
    public void getBalances(Set<UUID> uuids, Set<Currency> currencies, Consumer<MultiBalanceResult> consumer) {
        decorating.getBalances(uuids, currencies, consumer);
    }

    @Override
    public void deposit(UUID uuid, Currency currency, BigDecimal amount, String reason,
                        Consumer<BalanceUpdateResult> consumer) {
        decorating.deposit(uuid, currency, amount, reason, consumer);
    }

    @Override
    public void withdraw(UUID uuid, Currency currency, BigDecimal amount, String reason,
                         Consumer<BalanceUpdateResult> consumer) {
        decorating.withdraw(uuid, currency, amount, reason, consumer);
    }

    @Override
    public void transfer(UUID from, UUID to, Currency currency, BigDecimal amount, String reason,
                         Consumer<TransferResult> consumer) {
        decorating.transfer(from, to, currency, amount, reason, consumer);
    }

    @Override
    public void setBalance(UUID uuid, Currency currency, BigDecimal amount, String reason,
                           Consumer<BalanceUpdateResult> consumer) {
        decorating.setBalance(uuid, currency, amount, reason, consumer);
    }

    @Override
    public String getName() {
        return decorating.getName();
    }
}
