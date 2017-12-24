package com.ithinkrok.msm.common.economy.event;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * To be transmitted as well as two {@link EconomyBalanceEvent} when a transfer is completed.
 */
public interface EconomyTransferEvent extends EconomyEvent {


    UUID getSendingAccount();


    UUID getRecievingAccount();


    BigDecimal getAmount();

}
