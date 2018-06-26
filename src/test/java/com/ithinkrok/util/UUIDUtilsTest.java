package com.ithinkrok.util;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(DataProviderRunner.class)
public class UUIDUtilsTest {


    @Test
    @DataProvider(value = {"069a79f444e94726a5befca90e38aaf5,069a79f4-44e9-4726-a5be-fca90e38aaf5"})
    public void shouldCorrectlyAddDashes(String undashed, String dashed) {
        UUID uuid = UUIDUtils.fromStringWithoutDashes(undashed);

        assertEquals(uuid.toString().toLowerCase(), dashed);
    }


    @Test(expected = RuntimeException.class)
    @DataProvider(value = {",fancythat,thisisaverylongboiindeedheissolong"})
    public void shouldRejectNon32LengthStrings(String input) {
        UUIDUtils.fromStringWithoutDashes(input);
    }

}