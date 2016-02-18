package com.ithinkrok.util.event;

/**
 * Created by paul on 18/02/16.
 */
public class EventException extends RuntimeException {

    public EventException(String message, Throwable cause) {
        super(message, cause);
    }

    public EventException(Throwable cause) {
        super(cause);
    }
}
