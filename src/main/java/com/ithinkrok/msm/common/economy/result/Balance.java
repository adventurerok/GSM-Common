package com.ithinkrok.msm.common.economy.result;

import com.ithinkrok.msm.common.economy.AccountIdentifier;
import com.ithinkrok.msm.common.economy.Currency;
import com.ithinkrok.util.config.Config;
import com.ithinkrok.util.config.ConfigDeserializer;
import com.ithinkrok.util.config.ConfigSerializer;
import com.ithinkrok.util.config.MemoryConfig;

import java.math.BigDecimal;
import java.util.UUID;

public class Balance {

    private final AccountIdentifier account;
    private final BigDecimal amount;

    public Balance(AccountIdentifier account, BigDecimal amount) {
        this.account = account;
        this.amount = amount;
    }

    public AccountIdentifier getAccount() {
        return account;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public int getAmountAsInt() {
        return amount.intValue();
    }

    public Config toConfig(ConfigSerializer<Currency> currencySerializer) {
        Config config = new MemoryConfig();

        config.set("account", account.getOwner().toString());
        config.set("currency", currencySerializer.serialize(account.getCurrency()));
        config.set("amount", amount);

        return config;
    }

    public static Balance fromConfig(Config config, ConfigDeserializer<Currency> currencyDeserializer) {

        UUID account = UUID.fromString(config.getString("account"));
        Currency currency = currencyDeserializer.deserialize(config.getConfigOrNull("currency"));
        BigDecimal amount = config.getBigDecimal("amount");

        return new Balance(new AccountIdentifier(account, currency), amount);
    }
}
