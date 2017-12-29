package com.ithinkrok.msm.common.economy;

public abstract class AbstractEconomyContext implements EconomyContext {

    private EconomyContext parent;

    private final String contextType;

    public AbstractEconomyContext(String contextType) {
        this.contextType = contextType;
    }

    @Override
    public String getContextType() {
        return contextType;
    }

    protected abstract Currency lookupLocalCurrency(String name);

    @Override
    public Currency lookupCurrency(String name) {
        Currency currency = lookupLocalCurrency(name);
        if(currency != null) {
            return currency;
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
