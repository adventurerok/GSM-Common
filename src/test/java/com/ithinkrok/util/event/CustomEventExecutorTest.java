package com.ithinkrok.util.event;

import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by paul on 20/01/16.
 */
@RunWith(DataProviderRunner.class)
public class CustomEventExecutorTest {

    @Mock CustomEvent event;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void shouldExecuteAllEventsInTheCorrectOrder() {
        OrderListener listener = new OrderListener();

        CustomEventExecutor.executeEvent(event, listener);

        assertThat(listener.doneFirst && listener.doneLow && listener.doneHigh && listener.doneLast).isTrue();
    }

    private static class OrderListener implements CustomListener {

        private boolean doneFirst = false;
        private boolean doneLow = false;
        private boolean doneHigh = false;
        private boolean doneLast = false;

        @CustomEventHandler(priority = CustomEventHandler.INTERNAL_FIRST)
        public void internalFirstFirst(CustomEvent event) {
            assertThat(doneFirst || doneLow || doneHigh || doneLast).isFalse();

            doneFirst = true;
        }

        @CustomEventHandler(priority = CustomEventHandler.LOW)
        public void lowSecond(CustomEvent event) {
            assertThat(doneFirst && !(doneLow || doneHigh || doneLast)).isTrue();

            doneLow = true;
        }

        @CustomEventHandler(priority = CustomEventHandler.HIGH)
        public void highThird(CustomEvent event) {
            assertThat((doneFirst && doneLow) && !(doneHigh || doneLast)).isTrue();

            doneHigh = true;
        }

        @CustomEventHandler(priority = CustomEventHandler.HIGH)
        public void internalLastLast(CustomEvent event) {
            assertThat((doneFirst && doneLow && doneHigh) && !doneLast).isTrue();

            doneLast = true;
        }
    }
}