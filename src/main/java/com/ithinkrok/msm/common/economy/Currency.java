package com.ithinkrok.msm.common.economy;

import java.math.BigDecimal;

public interface Currency {

    String getCurrencyType();

    String format(BigDecimal amount);

    int getDecimalPlaces();

    String getName();

    /**
     * @return The plural formatted name of the currency
     */
    String getFormattedName();

    String getSingularFormattedName();

    String getSymbol();

    /**
     * @return The context for this currency. A (name,context) make up a unique currency.
     */
    CurrencyContext getCurrencyContext();

    /**
     * @return If it is valid to use this currency at this point.
     *         Once this becomes false it should never become true again.
     */
    boolean isValid();
}
