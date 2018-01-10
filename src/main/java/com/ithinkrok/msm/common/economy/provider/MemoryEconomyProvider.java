package com.ithinkrok.msm.common.economy.provider;

import com.ithinkrok.msm.common.economy.AccountIdentifier;
import com.ithinkrok.msm.common.economy.Currency;
import com.ithinkrok.msm.common.economy.CurrencyNotProvidedException;
import com.ithinkrok.msm.common.economy.EconomyContext;
import com.ithinkrok.msm.common.economy.batch.*;
import com.ithinkrok.msm.common.economy.result.Balance;
import com.ithinkrok.msm.common.economy.result.BalanceChange;
import com.ithinkrok.msm.common.economy.result.MultiBalanceResult;
import com.ithinkrok.msm.common.economy.result.TransactionResult;

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


    public void clearBalances(Currency currency) {
        checkCurrency(currency);

        store.keySet().removeIf(uuidAndCurrency -> {
            return uuidAndCurrency.getCurrency().getName().equals(currency.getName());
        });
    }


    private void checkCurrency(Currency currency) {
        if (currency.getContext() != context) {
            throw new CurrencyNotProvidedException(this, currency);
        }
    }


    public void clearAllBalances() {
        store.clear();
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
    public void executeBatch(Batch batch, String reason, Consumer<BatchResult> consumer) {
        //Index where we failed
        int failIndex = getFailIndex(batch);


        if (failIndex >= 0) {
            resultsForFailedBatch(batch, reason, consumer, failIndex);
        } else {
            executeSuccessfulBatch(batch, reason, consumer);
        }
    }


    private void executeSuccessfulBatch(Batch batch, String reason, Consumer<BatchResult> consumer) {
        List<UpdateResult> results = new ArrayList<>();

        for (Update update : batch.getUpdates()) {
            AccountIdentifier account = update.getAccount();
            BigDecimal oldBalance = store.getOrDefault(account, BigDecimal.ZERO);

            BalanceChange change;

            switch (update.getUpdateType()) {
                case CHECK:
                    change = BalanceChange.noBalanceChange(account, oldBalance, reason);
                    break;
                case SET:
                    store.put(account, update.getAmount());
                    change = BalanceChange.fromOldAndNew(account, oldBalance, update.getAmount(), reason);
                    break;
                case DEPOSIT:
                    store.put(account, oldBalance.add(update.getAmount()));
                    change = BalanceChange.fromOldAndChange(account, oldBalance, update.getAmount(), reason);
                    break;
                case WITHDRAW:
                    store.put(account, oldBalance.subtract(update.getAmount()));
                    change = BalanceChange.fromOldAndChange(account, oldBalance, update.getAmount().negate(), reason);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported UpdateType. Thus we fail");
            }

            results.add(new UpdateResult(update, TransactionResult.SUCCESS, change));
        }

        consumer.accept(new BatchResult(results, true));
    }


    private void resultsForFailedBatch(Batch batch, String reason, Consumer<BatchResult> consumer, int failIndex) {
        List<UpdateResult> results = new ArrayList<>();

        int updateIndex = 0;
        for (Update update : batch.getUpdates()) {
            BigDecimal balance = store.getOrDefault(update.getAccount(), BigDecimal.ZERO);

            BalanceChange change = BalanceChange.noBalanceChange(update.getAccount(), balance, reason);

            TransactionResult transResult =
                    updateIndex < failIndex ? TransactionResult.ROLLED_BACK :
                            updateIndex > failIndex ? TransactionResult.BATCH_FAILED :
                                    isSupportedUpdate(update.getUpdateType()) ? TransactionResult.NO_FUNDS :
                                            TransactionResult.UNSUPPORTED;


            results.add(new UpdateResult(update, transResult, change));

            ++updateIndex;
        }

        consumer.accept(new BatchResult(results, false));
    }


    private boolean isSupportedUpdate(UpdateType updateType) {
        switch(updateType) {
            case CHECK:
            case SET:
            case DEPOSIT:
            case WITHDRAW:
                return true;
            default:
                return false;
        }
    }


    private int getFailIndex(Batch batch) {
        int updateIndex = 0;
        for (Update update : batch.getUpdates()) {
            switch (update.getUpdateType()) {
                case SET:
                case DEPOSIT:
                    //We can't fail on these types
                    continue;
                case CHECK:
                case WITHDRAW:
                    //Check if we have the required balance
                    BigDecimal ourAmount = store.getOrDefault(update.getAccount(), BigDecimal.ZERO);
                    if (ourAmount.compareTo(update.getAmount()) < 0) {
                        return updateIndex;
                    }

                    break;
                default:
                    //We are unsupported
                    return updateIndex;
            }

            ++updateIndex;
        }
        return -1;
    }


    @Override
    public Optional<Balance> getBalance(AccountIdentifier account) {
        checkCurrency(account.getCurrency());

        BigDecimal amount = store.getOrDefault(account, BigDecimal.ZERO);

        return Optional.of(new Balance(account, amount));
    }


}
