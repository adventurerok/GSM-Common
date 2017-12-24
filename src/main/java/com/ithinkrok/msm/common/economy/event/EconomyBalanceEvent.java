package com.ithinkrok.msm.common.economy.event;

import com.ithinkrok.msm.common.economy.result.BalanceChange;

public interface EconomyBalanceEvent extends EconomyEvent {

    BalanceChange getBalanceChange();

}
