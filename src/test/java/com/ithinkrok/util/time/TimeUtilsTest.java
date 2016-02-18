package com.ithinkrok.util.time;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Instant;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by paul on 18/02/16.
 */
@RunWith(DataProviderRunner.class)
public class TimeUtilsTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWithNullString() {
        TimeUtils.addTimeToInstant(null, Instant.now());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWithNullInstant() {
        TimeUtils.addTimeToInstant("3h2m30s", null);
    }

    @Test
    @DataProvider(
            {"2007-12-03T10:15:30.00Z,2007-12-03T10:15:31.00Z,1s", "2067-01-01T00:00:00.00Z,2067-01-08T00:00:00.00Z,1w",
                    "2067-01-01T00:00:00.00Z,2067-01-08T00:00:01.00Z,1w1s",
                    "2067-01-01T00:00:00.00Z,2067-01-08T20:10:05.00Z,1w20h10m5s"})
    public void shouldAddCorrectTime(String beforeInstant, String afterInstant, String parse) {
        Instant before = Instant.parse(beforeInstant);
        Instant after = Instant.parse(afterInstant);

        Instant result = TimeUtils.addTimeToInstant(parse, before);

        assertThat(result).isEqualTo(after);
    }
}