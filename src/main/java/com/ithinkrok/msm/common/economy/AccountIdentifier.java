package com.ithinkrok.msm.common.economy;

import java.util.Objects;
import java.util.UUID;

/**
 * An immutable account class to be used as a key where often a balance is the value.
 */
public class AccountIdentifier {

    private final UUID owner;
    private final Currency currency;


    public AccountIdentifier(UUID owner, Currency currency) {
        Objects.requireNonNull(owner, "owner cannot be null");
        Objects.requireNonNull(currency, "currency cannot be null");

        this.owner = owner;
        this.currency = currency;
    }


    public UUID getOwner() {
        return owner;
    }


    public Currency getCurrency() {
        return currency;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountIdentifier account = (AccountIdentifier) o;
        return Objects.equals(owner, account.owner) &&
               Objects.equals(currency, account.currency);
    }


    @Override
    public int hashCode() {

        return Objects.hash(owner, currency);
    }
}
