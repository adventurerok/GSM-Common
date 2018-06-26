package com.ithinkrok.msm.client.economy;

import com.ithinkrok.msm.common.economy.AccountIdentifier;
import com.ithinkrok.msm.common.economy.Currency;
import com.ithinkrok.msm.common.economy.batch.Batch;
import com.ithinkrok.msm.common.economy.batch.BatchResult;
import com.ithinkrok.msm.common.economy.batch.UpdateResult;
import com.ithinkrok.msm.common.economy.provider.EconomyProvider;
import com.ithinkrok.msm.common.economy.result.Balance;
import com.ithinkrok.msm.common.economy.result.BalanceChange;
import com.ithinkrok.msm.common.economy.result.BalanceUpdateResult;
import com.ithinkrok.msm.common.economy.result.MultiBalanceResult;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class ClientEconomyProvider implements EconomyProvider {

    private final Map<Long, ConsumerHolder<?>> consumers = new ConcurrentHashMap<>();


    private final Map<AccountIdentifier, BigDecimal> cache = new ConcurrentHashMap<>();
    private final Set<UUID> accountsInCache = new HashSet<>();

    private final ClientEconomyProtocol protocol;
    private final GlobalContext context;

    private final AtomicLong nextRef = new AtomicLong(new Random().nextLong());


    public ClientEconomyProvider(ClientEconomyProtocol protocol, GlobalContext context) {
        this.protocol = protocol;
        this.context = context;
    }


    @Override
    public Set<Currency> getManagedCurrencies() {
        return context.getCurrencies();
    }


    @Override
    public void getBalances(Set<UUID> uuids, Set<Currency> currencies, Consumer<MultiBalanceResult> consumer) {
        Set<UUID> local = new HashSet<>(uuids);
        local.retainAll(accountsInCache);

        Set<UUID> notLocal = new HashSet<>(uuids);
        notLocal.removeAll(accountsInCache);

        if (!local.isEmpty()) {
            List<Balance> result = new ArrayList<>(uuids.size() * currencies.size());

            for (UUID uuid : uuids) {
                for (Currency currency : currencies) {
                    AccountIdentifier account = new AccountIdentifier(uuid, currency);

                    BigDecimal amount = cache.get(account);
                    if (amount == null) continue;

                    result.add(new Balance(account, amount));
                }

            }

            consumer.accept(new MultiBalanceResult(result, notLocal.isEmpty()));
        }

        if (!notLocal.isEmpty()) {
            long ref = nextRef.incrementAndGet();
            consumers.put(ref, new ConsumerHolder<>(consumer));

            if (!protocol.sendGetBalances(ref, uuids, currencies)) {
                //No result
                consumer.accept(null);
                consumers.remove(ref);
            }
        }
    }


    @Override
    public String getName() {
        //We are still "the" global economy provider, just a remote view of it
        return "global";
    }


    @SuppressWarnings("unchecked")
    public void hasAccountResult(long ref, Boolean hasAccount) {

        ConsumerHolder<Boolean> holder = (ConsumerHolder<Boolean>) consumers.remove(ref);
        if (holder == null) {
            //We timed out and would have passed null
            return;
        }

        holder.consumer.accept(hasAccount);

    }


    @SuppressWarnings("unchecked")
    public void getBalanceResult(long ref, Balance balance) {
        if (balance != null && accountsInCache.contains(balance.getAccount().getOwner())) {
            cache.put(balance.getAccount(), balance.getAmount());
        }

        ConsumerHolder<Balance> holder = (ConsumerHolder<Balance>) consumers.remove(ref);
        if (holder == null) {
            //We timed out or have already replied
            return;
        }

        holder.consumer.accept(balance);
    }


    @SuppressWarnings("unchecked")
    public void getBalancesResult(long ref, MultiBalanceResult balances) {
        if (balances != null) {
            balances.getBalances().forEach(this::updateCache);
        }

        ConsumerHolder<MultiBalanceResult> holder;

        if (balances == null || balances.isLastResult()) {
            holder = (ConsumerHolder<MultiBalanceResult>) consumers.remove(ref);
        } else {
            holder = (ConsumerHolder<MultiBalanceResult>) consumers.get(ref);
        }

        if (holder == null) {
            //We timed out or already replied
            return;
        }

        holder.consumer.accept(balances);
    }


    @SuppressWarnings("unchecked")
    public void balanceUpdateResult(long ref, BalanceUpdateResult result) {
        BalanceChange bc = result.getBalanceChange();
        if (bc != null) {
            updateCache(bc);
        }

        ConsumerHolder<BalanceUpdateResult> holder = (ConsumerHolder<BalanceUpdateResult>) consumers.remove(ref);

        if (holder == null) {
            //timed out
            return;
        }

        holder.consumer.accept(result);
    }


    private void updateCache(BalanceChange bc) {
        if (accountsInCache.contains(bc.getAccount().getOwner())) {
            cache.put(bc.getAccount(), bc.getNewBalance());
        }
    }


    public void balanceChange(BalanceChange change) {
        updateCache(change);
    }


    public void cacheBalances(Collection<UUID> accounts, MultiBalanceResult balances) {
        accountsInCache.addAll(accounts);

        for (Balance balance : balances.getBalances()) {
            updateCache(balance);
        }
    }


    private void updateCache(Balance balance) {
        if (accountsInCache.contains(balance.getAccount().getOwner())) {
            cache.put(balance.getAccount(), balance.getAmount());
        }
    }


    public void removeFromCache(UUID uuid) {
        accountsInCache.remove(uuid);
        cache.keySet().removeIf(accountAndCurrency -> accountAndCurrency.getOwner().equals(uuid));
    }


    @SuppressWarnings("unchecked")
    public void batchResult(long ref, BatchResult result) {
        //Update our cache
        for (UpdateResult updateResult : result.getResults()) {
            if (updateResult.getChange() != null) {
                updateCache(updateResult.getChange());
            }
        }

        ConsumerHolder<BatchResult> holder = (ConsumerHolder<BatchResult>) consumers.remove(ref);

        if (holder == null) {
            //Timed out earlier
            return;
        }

        holder.consumer.accept(result);
    }


    @Override
    public void hasAccount(AccountIdentifier account, Consumer<Boolean> consumer) {
        if (accountsInCache.contains(account.getOwner())) {
            consumer.accept(cache.containsKey(account));
        } else {
            long ref = nextRef.incrementAndGet();
            consumers.put(ref, new ConsumerHolder<>(consumer));

            if (!protocol.sendHasAccount(ref, account.getOwner(), account.getCurrency())) {
                //If no connection is available we return null straight away
                consumer.accept(null);
                consumers.remove(ref);
            }
        }
    }


    @Override
    public Optional<Boolean> hasAccount(AccountIdentifier account) {
        if (hasLocalAccount(account)) {
            return Optional.of(true);
        } else if (accountsInCache.contains(account.getOwner())) {
            return Optional.of(false);
        } else {
            return Optional.empty();
        }
    }


    @Override
    public boolean hasLocalAccount(AccountIdentifier account) {
        return cache.containsKey(account);
    }


    @Override
    public void getBalance(AccountIdentifier account, Consumer<Balance> consumer) {
        Optional<Balance> balance = getBalance(account);

        if (balance.isPresent()) {
            consumer.accept(balance.get());
        } else {
            long ref = nextRef.incrementAndGet();
            consumers.put(ref, new ConsumerHolder<>(consumer));

            if (!protocol.sendGetBalance(ref, account.getOwner(), account.getCurrency())) {
                //No result
                consumer.accept(null);
                consumers.remove(ref);
            }
        }
    }


    @Override
    public Optional<Balance> getBalance(AccountIdentifier account) {
        if (accountsInCache.contains(account.getOwner())) {
            BigDecimal amount = cache.getOrDefault(account, BigDecimal.ZERO);

            return Optional.of(new Balance(account, amount));
        } else {
            return Optional.empty();
        }
    }


    @Override
    public void executeBatch(Batch batch, String reason, Consumer<BatchResult> consumer) {
        long ref = nextRef.incrementAndGet();
        consumers.put(ref, new ConsumerHolder<>(consumer));

        if(!protocol.sendBatch(ref, batch, reason)) {
            consumers.remove(ref);
            consumer.accept(null);
        }
    }


    private static class ConsumerHolder<T> {

        private final Consumer<T> consumer;
        private final Instant time;


        public ConsumerHolder(Consumer<T> consumer) {
            this.consumer = consumer;
            this.time = Instant.now();
        }
    }


}
