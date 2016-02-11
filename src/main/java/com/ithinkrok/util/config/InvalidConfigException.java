package com.ithinkrok.util.config;

import java.io.IOException;

/**
 * Created by paul on 11/02/16.
 */
public class InvalidConfigException extends IOException {

    public InvalidConfigException(String message) {
        super(message);
    }

    public InvalidConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidConfigException(Throwable cause) {
        super(cause);
    }
}
