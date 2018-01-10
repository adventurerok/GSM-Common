package com.ithinkrok.msm.common.economy.provider;

import com.ithinkrok.msm.common.economy.AccountIdentifier;
import com.ithinkrok.msm.common.economy.Currency;
import com.ithinkrok.msm.common.economy.batch.Batch;
import com.ithinkrok.msm.common.economy.batch.BatchResult;
import com.ithinkrok.msm.common.economy.batch.Update;
import com.ithinkrok.msm.common.economy.batch.UpdateResult;
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
    public boolean hasLocalAccount(AccountIdentifier account) {
        return decorating.hasLocalAccount(account);
    }

    @Override
    public void hasAccount(AccountIdentifier account, Consumer<Boolean> consumer) {
        decorating.hasAccount(account, consumer);
    }

    @Override
    public Optional<Boolean> hasAccount(AccountIdentifier account) {
        return decorating.hasAccount(account);
    }

    @Override
    public void getBalance(AccountIdentifier account, Consumer<Balance> consumer) {
        decorating.getBalance(account, consumer);
    }

    @Override
    public Optional<Balance> getBalance(AccountIdentifier account) {
        return decorating.getBalance(account);
    }

    @Override
    public void getBalances(Set<UUID> uuids, Set<Currency> currencies, Consumer<MultiBalanceResult> consumer) {
        decorating.getBalances(uuids, currencies, consumer);
    }


    @Override
    public void executeUpdate(Update update, String reason, Consumer<UpdateResult> consumer) {
        decorating.executeUpdate(update, reason, consumer);
    }


    @Override
    public void executeBatch(Batch batch, String reason, Consumer<BatchResult> consumer) {
        decorating.executeBatch(batch, reason, consumer);
    }


    @Override
    public String getName() {
        return decorating.getName();
    }
}
