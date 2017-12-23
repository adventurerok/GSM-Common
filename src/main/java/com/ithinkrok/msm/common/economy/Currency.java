package com.ithinkrok.msm.common.economy;

import java.math.BigDecimal;

public interface Currency {

    CurrencyType getCurrencyType();

    String format(BigDecimal amount);

    int getDecimalPlaces();

    String getName();

    String getFormattedName();

    String getPluralFormattedName();

    String getSymbol();
}
