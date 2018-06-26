package com.ithinkrok.msm.client.economy;

import com.ithinkrok.msm.common.economy.Currency;
import com.ithinkrok.msm.common.economy.EconomyContext;
import com.ithinkrok.msm.common.economy.CurrencyType;
import com.ithinkrok.util.config.Config;

import java.util.*;

public class GlobalContext implements EconomyContext {

    private Map<String, GlobalCurrency> currencyMap = Collections.emptyMap();

    public GlobalContext() {

    }

    public void update(Collection<Config> currencyConfigs) {
        Map<String, GlobalCurrency> map = new HashMap<>();

        for (Config config : currencyConfigs) {
            String name = config.getString("name");

            GlobalCurrency currency;

            //ensure we keep the same objects in circulation
            if(currencyMap.containsKey(name)) {
                currency = currencyMap.get(name);
                currency.reloadFromConfig(config);
            } else {
                currency = new GlobalCurrency(this, name, config);
            }


            map.put(name, currency);
        }

        this.currencyMap = map;
    }

    @Override
    public Collection<String> getCurrencyTypes() {
        return Collections.singleton(CurrencyType.GLOBAL);
    }

    @Override
    public String getContextType() {
        return CurrencyType.GLOBAL;
    }

    @Override
    public Currency lookupCurrency(String name) {
        return currencyMap.get(name);
    }

    @Override
    public EconomyContext getParent() {
        return null;
    }

    public Set<Currency> getCurrencies() {
        return new HashSet<>(currencyMap.values());
    }

    @Override
    public void setParent(EconomyContext parent) {
        throw new UnsupportedOperationException("There can be no parent to a global context");
    }
}
