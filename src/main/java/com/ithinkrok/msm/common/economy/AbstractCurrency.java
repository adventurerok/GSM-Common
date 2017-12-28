package com.ithinkrok.msm.common.economy;

import com.ithinkrok.util.config.Config;

import java.math.BigDecimal;

public abstract class AbstractCurrency implements Currency {

    private final String name;
    private final String singularFormattedName;
    private final String pluralFormattedName;
    private final String symbol;
    private final String singularFormat;
    private final String pluralFormat;
    private final int decimalPlaces;

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
}
