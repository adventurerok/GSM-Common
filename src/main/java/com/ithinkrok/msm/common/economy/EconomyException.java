package com.ithinkrok.msm.common.economy;

public class EconomyException extends RuntimeException {

    public EconomyException(String message) {
        super(message);
    }

    public EconomyException(String message, Throwable cause) {
        super(message, cause);
    }
}
