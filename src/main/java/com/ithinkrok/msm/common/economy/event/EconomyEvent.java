package com.ithinkrok.msm.common.economy.event;

import com.ithinkrok.msm.common.economy.Currency;
import com.ithinkrok.msm.common.economy.EconomyProvider;
import com.ithinkrok.util.event.CustomEvent;

public interface EconomyEvent extends CustomEvent {

    /**
     * @return The currency used in the event
     */
    Currency getCurrency();


    /**
     * @return The economy provider used in the event
     */
    EconomyProvider getProvider();


    /**
     * @return The reason given for this event, or null if there is none
     */
    String getReason();

}
