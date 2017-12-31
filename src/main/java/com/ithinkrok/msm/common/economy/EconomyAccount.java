package com.ithinkrok.msm.common.economy;

import com.ithinkrok.msm.common.economy.provider.EconomyProvider;

import java.util.UUID;

public class EconomyAccount implements Account {


    private Economy economy;

    private final UUID uuid;

    public EconomyAccount(Economy economy, UUID uuid) {
        if(economy == null) {
            throw new NullPointerException("Economy cannot be null");
        }

        if(uuid == null) {
            throw new NullPointerException("UUID cannot be null");
        }

        this.economy = economy;
        this.uuid = uuid;
    }

    @Override
    public EconomyContext getContext() {
        return economy.getContext();
    }

    @Override
    public EconomyProvider getProvider() {
        return economy.getProvider();
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    public void setEconomy(Economy economy) {
        this.economy = economy;
    }
}
