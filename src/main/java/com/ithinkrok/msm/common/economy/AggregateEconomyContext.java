package com.ithinkrok.msm.common.economy;

import java.util.*;

public class AggregateEconomyContext extends AbstractEconomyContext {

    private EconomyContext parent;

    private final List<EconomyContext> lookups = new ArrayList<>();

    public AggregateEconomyContext(String contextType, Collection<? extends EconomyContext> lookups) {
        super(contextType);

        this.lookups.addAll(lookups);
    }


    @Override
    protected Currency lookupLocalCurrency(String name) {
        for (EconomyContext lookup : lookups) {
            Currency currency = lookup.lookupCurrency(name);
            if(currency != null) {
                return currency;
            }
        }

        return null;
    }

}
