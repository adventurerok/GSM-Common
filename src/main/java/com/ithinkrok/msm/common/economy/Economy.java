package com.ithinkrok.msm.common.economy;

import com.ithinkrok.msm.common.economy.provider.EconomyProvider;

public interface Economy {


    EconomyProvider getProvider();

    EconomyContext getContext();


    static Economy create(EconomyProvider provider, EconomyContext context) {
        return new Economy() {
            @Override
            public EconomyProvider getProvider() {
                return provider;
            }

            @Override
            public EconomyContext getContext() {
                return context;
            }
        };
    }

}
