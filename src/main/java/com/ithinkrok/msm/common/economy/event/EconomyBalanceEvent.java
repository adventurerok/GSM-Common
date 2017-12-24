package com.ithinkrok.msm.common.economy.event;

import java.math.BigDecimal;
import java.util.UUID;

public interface EconomyBalanceEvent extends EconomyEvent {

    /**
     *
     * @return The UUID of the account that was changed
     */
    UUID getAccount();


    BigDecimal getNewBalance();


    BigDecimal getBalanceChange();


    default BigDecimal getOldBalance() {
        return getNewBalance().subtract(getBalanceChange());
    }

}
