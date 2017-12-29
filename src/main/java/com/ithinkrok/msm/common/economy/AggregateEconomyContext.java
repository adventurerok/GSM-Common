package com.ithinkrok.msm.common.economy;

import java.util.*;

public class AggregateEconomyContext implements EconomyContext {

    private EconomyContext parent;

    private final String contextType;

    private final List<EconomyContext> lookups = new ArrayList<>();

    public AggregateEconomyContext(String contextType, Collection<? extends EconomyContext> lookups) {
        this.contextType = contextType;

        this.lookups.addAll(lookups);
    }

    @Override
    public Collection<String> getCurrencyTypes() {
        if(parent != null) {
            Set<String> result = new HashSet<>(parent.getCurrencyTypes());
            result.add(contextType);
            return result;
        } else {
            return Collections.singleton(contextType);
        }
    }

    @Override
    public String getContextType() {
        return contextType;
    }

    @Override
    public Currency lookupCurrency(String name) {
        for (EconomyContext lookup : lookups) {
            Currency currency = lookup.lookupCurrency(name);
            if(currency != null) {
                return currency;
            }
        }

        if(parent != null) {
            return parent.lookupCurrency(name);
        } else {
            return null;
        }
    }

    @Override
    public EconomyContext getParent() {
        return parent;
    }

    @Override
    public void setParent(EconomyContext parent) {
        this.parent = parent;
    }
}
