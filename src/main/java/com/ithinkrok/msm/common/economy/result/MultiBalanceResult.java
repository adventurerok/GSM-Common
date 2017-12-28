package com.ithinkrok.msm.common.economy.result;

import com.ithinkrok.msm.common.economy.Currency;
import com.ithinkrok.util.config.Config;
import com.ithinkrok.util.config.ConfigDeserializer;
import com.ithinkrok.util.config.ConfigSerializer;
import com.ithinkrok.util.config.MemoryConfig;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class MultiBalanceResult {

    private final Collection<Balance> balances;
    private final boolean lastResult;

    public MultiBalanceResult(Collection<Balance> balances, boolean lastResult) {
        this.balances = balances;
        this.lastResult = lastResult;
    }

    public Collection<Balance> getBalances() {
        return balances;
    }

    public boolean isLastResult() {
        return lastResult;
    }

    public Config toConfig(ConfigSerializer<Currency> currencySerializer) {

        List<Config> balanceConfigs = balances.stream()
                .map(balance -> balance.toConfig(currencySerializer))
                .collect(Collectors.toList());

        Config result = new MemoryConfig();
        result.set("balances", balanceConfigs);

        result.set("last_result", lastResult);

        return result;
    }


    public static MultiBalanceResult fromConfig(Config config, ConfigDeserializer<Currency> currencyDeserializer) {
        List<Balance> balances = config.getConfigList("balances").stream()
                .map(balanceConfig -> Balance.fromConfig(balanceConfig, currencyDeserializer))
                .collect(Collectors.toList());

        boolean lastResult = config.getBoolean("last_result");

        return new MultiBalanceResult(balances, lastResult);
    }
}
