package com.ithinkrok.msm.common.economy;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public interface EconomyContext {

    /**
     * @return The types of currency that may reside in this context
     */
    default Collection<String> getCurrencyTypes() {
        if(getParent() != null) {
            Set<String> result = new HashSet<>(getParent().getCurrencyTypes());
            result.add(getContextType());
            return result;
        } else {
            return Collections.singleton(getContextType());
        }
    }


    /**
     * @return The currency type specific to this context (and not its parents)
     */
    String getContextType();


    /**
     * Looks up a currency in this context, or the parent contexts.
     *
     * @param name The name of the currency to lookup
     * @return The {@link Currency}, if one is found, or else null
     */
    Currency lookupCurrency(String name);


    /**
     * Gets the parent currency context to this one,
     * or null if this is the highest currency context existing (the global context usually)
     *
     * @return The parent context to this one
     */
    EconomyContext getParent();


    void setParent(EconomyContext parent);

}
