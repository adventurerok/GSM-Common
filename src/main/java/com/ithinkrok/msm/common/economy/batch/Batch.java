package com.ithinkrok.msm.common.economy.batch;

import java.util.*;

/**
 * A batch of updates to be executed all at one atomically.
 */
public class Batch {

    private final List<Update> updates = new ArrayList<>();

    public void addUpdate(Update update) {
        updates.add(update);
    }

    public void addUpdates(Collection<Update> updatesToAdd) {
        updates.addAll(updatesToAdd);
    }

    public List<Update> getUpdates() {
        return Collections.unmodifiableList(updates);
    }

}
