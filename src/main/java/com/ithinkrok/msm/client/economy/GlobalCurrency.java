package com.ithinkrok.msm.client.economy;

import com.ithinkrok.msm.common.economy.AbstractCurrency;
import com.ithinkrok.msm.common.economy.EconomyContext;
import com.ithinkrok.msm.common.economy.CurrencyType;
import com.ithinkrok.util.config.Config;

public class GlobalCurrency extends AbstractCurrency {

    private final GlobalContext context;

    public GlobalCurrency(GlobalContext context, String name, Config config) {
        super(name, config);
        this.context = context;
    }

    @Override
    public void reloadFromConfig(Config config) {
        super.reloadFromConfig(config);
    }

    @Override
    public String getCurrencyType() {
        return CurrencyType.GLOBAL;
    }

    @Override
    public EconomyContext getContext() {
        return context;
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
