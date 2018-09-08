package com.ithinkrok.msm.common.economy.batch;

import com.ithinkrok.msm.common.economy.Currency;
import com.ithinkrok.msm.common.economy.result.TransactionResult;
import com.ithinkrok.util.config.Config;
import com.ithinkrok.util.config.ConfigDeserializer;
import com.ithinkrok.util.config.ConfigSerializer;
import com.ithinkrok.util.config.MemoryConfig;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BatchResult {

    public static BatchResult FAILURE = new BatchResult(Collections.emptyList(), false);

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


    private static boolean allUpdatesSuccessful(List<UpdateResult> updateResults) {
        return updateResults.stream().allMatch(result -> {
            return result.getResult() == TransactionResult.SUCCESS;
        });
    }


    public boolean wasSuccessful() {
        return successful;
    }


    /**
     * @return List of update results. If this is empty, we failed due to an error out of our control (e.g. SQL error)
     */
    public List<UpdateResult> getResults() {
        return Collections.unmodifiableList(results);
    }


    public Config toConfig(ConfigSerializer<Currency> currencySerializer) {
        Config config = new MemoryConfig();

        config.set("successful", successful);

        config.set("results", results.stream()
                .map(updateResult -> updateResult.toConfig(currencySerializer))
                .collect(Collectors.toList()));

        return config;
    }

    public static BatchResult fromConfig(Config config, ConfigDeserializer<Currency> currencyDeserializer) {
        boolean successful = config.getBoolean("successful");

        List<UpdateResult> results = config.getConfigList("results").stream()
                .map(resultConfig -> UpdateResult.fromConfig(resultConfig, currencyDeserializer))
                .collect(Collectors.toList());

        return new BatchResult(results, successful);
    }
}
