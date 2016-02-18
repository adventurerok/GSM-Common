package com.ithinkrok.util.event;

/**
 * Created by paul on 18/02/16.
 */
public interface Cancellable {

    boolean isCancelled();
    void setCancelled(boolean cancel);
}
