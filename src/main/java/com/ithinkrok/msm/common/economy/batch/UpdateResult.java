package com.ithinkrok.msm.common.economy.batch;

import com.ithinkrok.msm.common.economy.Currency;
import com.ithinkrok.msm.common.economy.result.BalanceChange;
import com.ithinkrok.msm.common.economy.result.TransactionResult;
import com.ithinkrok.util.config.Config;
import com.ithinkrok.util.config.ConfigDeserializer;
import com.ithinkrok.util.config.ConfigSerializer;
import com.ithinkrok.util.config.MemoryConfig;

import java.util.Objects;

public class UpdateResult {

    private final Update update;
    private final TransactionResult result;
    private final BalanceChange change;


    public UpdateResult(Update update, TransactionResult result, BalanceChange change) {
        Objects.requireNonNull(update, "update cannot be null");
        Objects.requireNonNull(result, "result cannot be null");
        //change can be null if we failed to lookup balance

        this.update = update;
        this.result = result;
        this.change = change;
    }


    public Update getUpdate() {
        return update;
    }


    public TransactionResult getResult() {
        return result;
    }


    public BalanceChange getChange() {
        return change;
    }


    public Config toConfig(ConfigSerializer<Currency> currencySerializer) {
        Config config = new MemoryConfig()
                .set("update", update.toConfig(currencySerializer))
                .set("result", result.name());

        if (change != null) {
            config.set("change", change.toConfig(currencySerializer));
        }

        return config;
    }

    public static UpdateResult fromConfig(Config config, ConfigDeserializer<Currency> currencyDeserializer) {
        Update update = Update.fromConfig(config.getConfigOrNull("update"), currencyDeserializer);
        TransactionResult result = TransactionResult.valueOf(config.getString("result"));
        BalanceChange change = null;

        if(config.contains("change")) {
            change = BalanceChange.fromConfig(config.getConfigOrNull("change"), currencyDeserializer);
        }

        return new UpdateResult(update, result, change);
    }
}
