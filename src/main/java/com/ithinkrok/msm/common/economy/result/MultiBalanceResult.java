package com.ithinkrok.msm.common.economy.result;

import java.util.Collection;

public class MultiBalanceResult {

    private final Collection<Balance> balances;
    private final boolean lastResult;

    public MultiBalanceResult(Collection<Balance> balances, boolean lastResult) {
        this.balances = balances;
        this.lastResult = lastResult;
    }

    public Collection<Balance> getBalances() {
        return balances;
    }

    public boolean isLastResult() {
        return lastResult;
    }
}
