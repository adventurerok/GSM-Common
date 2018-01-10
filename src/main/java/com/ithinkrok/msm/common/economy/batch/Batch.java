package com.ithinkrok.msm.common.economy.batch;

import com.ithinkrok.msm.common.economy.result.BalanceChange;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A batch of updates to be executed all at one atomically.
 */
public class Batch {

    private final List<Update> updates = new ArrayList<>();

    public Batch() {

    }

    public Batch(Update update) {
        updates.add(update);
    }

    public Batch(Collection<Update> updatesToAdd) {
        updates.addAll(updatesToAdd);
    }

    public void addUpdate(Update update) {
        updates.add(update);
    }

    public void addUpdates(Collection<Update> updatesToAdd) {
        updates.addAll(updatesToAdd);
    }

    public List<Update> getUpdates() {
        return Collections.unmodifiableList(updates);
    }

    public static Batch rollback(BatchResult result) {
        if(!result.wasSuccessful()) {
            throw new IllegalArgumentException("You should only rollback successful batch updates");
        }

        List<Update> rollbackUpdates = result.getResults().stream()
                .map(updateResult -> Update.rollback(updateResult.getChange()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new Batch(rollbackUpdates);
    }

    public static Batch rollback(Collection<BalanceChange> changes) {
        List<Update> rollbackUpdates = changes.stream()
                .map(Update::rollback)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new Batch(rollbackUpdates);
    }

}
