package com.ithinkrok.msm.common.economy.batch;

import com.ithinkrok.msm.common.economy.result.TransactionResult;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class BatchResult {

    private final boolean successful;
    private final List<UpdateResult> results;


    public BatchResult(List<UpdateResult> results) {
        this(results, allUpdatesSuccessful(results));
    }


    public BatchResult(List<UpdateResult> results, boolean successful) {
        Objects.requireNonNull(results, "results cannot be null");

        this.successful = successful;
        this.results = results;
    }


    public boolean wasSuccessful() {
        return successful;
    }


    /**
     *
     * @return List of update results. If this is empty, we failed due to an error out of our control (e.g. SQL error)
     */
    public List<UpdateResult> getResults() {
        return Collections.unmodifiableList(results);
    }


    private static boolean allUpdatesSuccessful(List<UpdateResult> updateResults) {
        return updateResults.stream().allMatch(result -> {
            return result.getResult() == TransactionResult.SUCCESS;
        });
    }


}
