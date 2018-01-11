package com.ithinkrok.msm.common.economy.batch;

import com.ithinkrok.msm.common.economy.AccountIdentifier;
import com.ithinkrok.msm.common.economy.Currency;
import com.ithinkrok.msm.common.economy.result.BalanceChange;
import com.ithinkrok.util.config.Config;
import com.ithinkrok.util.config.ConfigDeserializer;
import com.ithinkrok.util.config.ConfigSerializer;
import com.ithinkrok.util.config.MemoryConfig;

import java.math.BigDecimal;
import java.util.UUID;

public class Update {

    private final AccountIdentifier account;
    private final UpdateType updateType;
    private final BigDecimal amount;


    public Update(AccountIdentifier account, UpdateType updateType, BigDecimal amount) {
        this.account = account;
        this.updateType = updateType;
        this.amount = amount;
    }


    public AccountIdentifier getAccount() {
        return account;
    }


    public UpdateType getUpdateType() {
        return updateType;
    }


    public BigDecimal getAmount() {
        return amount;
    }


    /**
     * Creates an Update that would rollback the specified BalanceChange.
     *
     * @return the rollback update, or null if there is no rollback possible for the balance change.
     */
    public static Update rollback(BalanceChange balanceChange) {
        if(balanceChange.getChange().compareTo(BigDecimal.ZERO) > 0) {
            return new Update(balanceChange.getAccount(), UpdateType.WITHDRAW, balanceChange.getChange());

        } else if(balanceChange.getChange().compareTo(BigDecimal.ZERO) < 0) {
            return new Update(balanceChange.getAccount(), UpdateType.DEPOSIT, balanceChange.getChange().negate());

        } else {
            //nothing to do to rollback
            return null;
        }
    }

    public Config toConfig(ConfigSerializer<Currency> currencySerializer) {
        Config config = new MemoryConfig();

        config.set("account", account.getOwner().toString());
        config.set("currency", currencySerializer.serialize(account.getCurrency()));
        config.set("type", updateType.name());
        config.set("amount", amount);

        return config;
    }

    public static Update fromConfig(Config config, ConfigDeserializer<Currency> currencyDeserializer) {
        UUID account = UUID.fromString(config.getString("account"));
        Currency currency = currencyDeserializer.deserialize(config.getConfigOrNull("currency"));
        UpdateType updateType = UpdateType.valueOf(config.getString("type"));
        BigDecimal amount = config.getBigDecimal("amount");

        return new Update(new AccountIdentifier(account, currency), updateType, amount);
    }
}
