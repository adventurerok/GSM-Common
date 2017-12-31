package com.ithinkrok.msm.common.economy;

import com.ithinkrok.util.config.Config;
import com.ithinkrok.util.config.MemoryConfig;

import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class AbstractCurrency implements Currency {

    private final String name;
    private String singularFormattedName;
    private String pluralFormattedName;
    private String symbol;
    private String singularFormat;
    private String pluralFormat;
    private int decimalPlaces;

    public AbstractCurrency(String name, String singularFormattedName, String pluralFormattedName, String symbol,
                            String singularFormat, String pluralFormat, int decimalPlaces) {
        this.name = name;
        this.singularFormattedName = singularFormattedName;
        this.pluralFormattedName = pluralFormattedName;
        this.symbol = symbol;
        this.singularFormat = singularFormat;
        this.pluralFormat = pluralFormat;
        this.decimalPlaces = decimalPlaces;
    }

    public AbstractCurrency(String name, Config config) {
        this.name = name;

        reloadFromConfig(config);
    }

    protected void reloadFromConfig(Config config) {
        this.singularFormattedName = config.getString("singular");
        this.pluralFormattedName = config.getString("plural");
        this.symbol = config.getString("symbol", "$");
        this.decimalPlaces = config.getInt("decimals", 0);

        this.pluralFormat = config.getString("format", config.getString("plural_format"));
        this.singularFormat = config.getString("singular_format", this.pluralFormat);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFormattedName() {
        return pluralFormattedName;
    }

    @Override
    public String getSingularFormattedName() {
        return singularFormattedName;
    }

    @Override
    public int getDecimalPlaces() {
        return decimalPlaces;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public String format(BigDecimal amount) {
        if(amount == null) {
            throw new NullPointerException("amount cannot be null");
        }

        amount = amount.setScale(decimalPlaces, RoundingMode.FLOOR);

        if(amount.compareTo(BigDecimal.ONE) == 0) {
            return String.format(singularFormat, amount);
        } else {
            return String.format(pluralFormat, amount);
        }
    }

    public String getSingularFormat() {
        return singularFormat;
    }

    public String getPluralFormat() {
        return pluralFormat;
    }

    public Config toConfig() {
        Config result = new MemoryConfig();

        result.set("singular", singularFormattedName);
        result.set("plural", pluralFormattedName);
        result.set("symbol", symbol);
        result.set("singular_format", singularFormat);
        result.set("plural_format", pluralFormat);
        result.set("decimals", decimalPlaces);

        return result;
    }
}
