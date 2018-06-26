package com.ithinkrok.msm.client.economy;

import com.ithinkrok.msm.client.Client;
import com.ithinkrok.msm.client.ClientListener;
import com.ithinkrok.msm.client.economy.ClientEconomyProvider;
import com.ithinkrok.msm.client.economy.GlobalContext;
import com.ithinkrok.msm.common.Channel;
import com.ithinkrok.msm.common.economy.Currency;
import com.ithinkrok.msm.common.economy.CurrencyType;
import com.ithinkrok.msm.common.economy.Economy;
import com.ithinkrok.msm.common.economy.batch.Batch;
import com.ithinkrok.msm.common.economy.batch.BatchResult;
import com.ithinkrok.msm.common.economy.result.Balance;
import com.ithinkrok.msm.common.economy.result.BalanceChange;
import com.ithinkrok.msm.common.economy.result.MultiBalanceResult;
import com.ithinkrok.util.config.Config;
import com.ithinkrok.util.config.JsonConfigIO;
import com.ithinkrok.util.config.MemoryConfig;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ClientEconomyProtocol implements ClientListener {


    private final ClientEconomyProvider provider;

    private final GlobalContext context;

    private final Economy economy;

    private Channel channel;

    public ClientEconomyProtocol() {
        this.context = new GlobalContext();
        this.provider = new ClientEconomyProvider(this, context);
        this.economy = Economy.create(provider, context);
    }

    @Override
    public void connectionOpened(Client client, Channel channel) {
        this.channel = channel;
    }

    @Override
    public void connectionClosed(Client client) {
        this.channel = null;
        //We won't clear the cache because the values don't need to be correct, and during a short downtime
        //its likely they will be.
    }


    public void removeFromCache(UUID playerUUID) {
        provider.removeFromCache(playerUUID);
    }

    @Override
    public void packetRecieved(Client client, Channel channel, Config payload) {
        String mode = payload.getString("mode");

        System.out.println("Economy packet");
        System.out.println(JsonConfigIO.dumpConfig(payload));

        switch (mode) {
            case "EconomyInfo":
                handleEconomyInfo(payload);
                break;
            case "AccountBalances":
                handleAccountBalances(payload);
                break;
            case "HasAccountResult":
                handleHasAccountResult(payload);
                break;
            case "BatchResult":
                handleBatchResult(payload);
            case "BalanceResult":
                handleBalanceResult(payload);
                break;
            case "MultiBalanceResult":
                handleMultiBalanceResult(payload);
                break;
            case "BalanceChange":
                handleBalanceChange(payload);
                break;
        }
    }


    private void handleBatchResult(Config payload) {
        BatchResult result = BatchResult.fromConfig(payload.getConfigOrNull("result"), this::deserializeCurrency);

        long ref = payload.getLong("ref");

        provider.batchResult(ref, result);
    }


    private void handleEconomyInfo(Config payload) {
        List<Config> currencyConfigs = payload.getConfigList("currencies");

        context.update(currencyConfigs);
    }

    private void handleAccountBalances(Config payload) {
        Set<UUID> accounts = payload.getStringList("accounts").stream()
                .map(UUID::fromString)
                .collect(Collectors.toSet());

        Config balancesConfig = payload.getConfigOrNull("balances");
        MultiBalanceResult balances = MultiBalanceResult.fromConfig(balancesConfig, this::deserializeCurrency);

        provider.cacheBalances(accounts, balances);
    }

    private void handleHasAccountResult(Config payload) {
        boolean hasResult = payload.getBoolean("result");
        Boolean hasAccount = hasResult ? payload.getBoolean("has_account") : null;
        long ref = payload.getLong("ref");

        provider.hasAccountResult(ref, hasAccount);
    }


    private void handleBalanceResult(Config payload) {
        boolean hasResult = payload.getBoolean("result");
        Balance balance;

        if (hasResult) {
            Config balanceConfig = payload.getConfigOrNull("balance");
            balance = Balance.fromConfig(balanceConfig, this::deserializeCurrency);
        } else {
            balance = null;
        }

        long ref = payload.getLong("ref");

        provider.getBalanceResult(ref, balance);
    }

    private void handleMultiBalanceResult(Config payload) {
        boolean hasResult = payload.getBoolean("result");
        MultiBalanceResult balances;

        if (hasResult) {
            Config balancesConfig = payload.getConfigOrNull("balances");
            balances = MultiBalanceResult.fromConfig(balancesConfig, this::deserializeCurrency);
        } else {
            balances = null;
        }

        long ref = payload.getLong("ref");

        provider.getBalancesResult(ref, balances);
    }

    private void handleBalanceChange(Config payload) {
        Config changeConfig = payload.getConfigOrNull("change");
        BalanceChange change = BalanceChange.fromConfig(changeConfig, this::deserializeCurrency);

        provider.balanceChange(change);
    }

    public boolean sendHasAccount(long ref, UUID uuid, Currency currency) {
        if (channel == null) return false;

        Config payload = new MemoryConfig();

        payload.set("mode", "HasAccount");
        payload.set("account", uuid.toString());
        payload.set("currency", serializeCurrency(currency));
        payload.set("ref", ref);

        channel.write(payload);
        return true;
    }

    /**
     * Serializes a currency.
     * <p>
     * Not intended to transmit details of the currency, just its name and its type
     */
    private Config serializeCurrency(Currency currency) {
        Config config = new MemoryConfig();

        config.set("name", currency.getName());
        config.set("type", currency.getCurrencyType());

        return config;
    }

    public boolean sendGetBalance(long ref, UUID uuid, Currency currency) {
        if (channel == null) return false;

        Config payload = new MemoryConfig();

        payload.set("mode", "GetBalance");
        payload.set("account", uuid.toString());
        payload.set("currency", serializeCurrency(currency));
        payload.set("ref", ref);

        channel.write(payload);
        return true;
    }

    private Currency deserializeCurrency(Config config) {
        String name = config.getString("name");
        String type = config.getString("type");

        switch (type) {
            case CurrencyType.GLOBAL:
                return context.lookupCurrency(name);
            default:
                System.out.println("[GSM-Client] Unknown currency type: " + type);
                return null;
        }
    }

    public boolean sendGetBalances(long ref, Set<UUID> uuids, Set<Currency> currencies) {
        if (channel == null) {
            return false;
        }

        Config payload = new MemoryConfig();
        payload.set("mode", "GetBalances");

        List<String> accounts = uuids.stream()
                .map(UUID::toString)
                .collect(Collectors.toList());
        payload.set("accounts", accounts);

        List<Config> currencyConfigs = currencies.stream()
                .map(this::serializeCurrency)
                .collect(Collectors.toList());
        payload.set("currencies", currencyConfigs);

        payload.set("ref", ref);

        channel.write(payload);

        return true;
    }

    public Economy getEconomy() {
        return economy;
    }


    public boolean sendBatch(long ref, Batch batch, String reason) {
        if(channel == null) {
            return false;
        }

        Config payload = new MemoryConfig();
        payload.set("mode", "Batch");

        payload.set("batch", batch.toConfig(this::serializeCurrency));
        payload.set("reason", reason);
        payload.set("ref", ref);

        channel.write(payload);

        return true;
    }
}
