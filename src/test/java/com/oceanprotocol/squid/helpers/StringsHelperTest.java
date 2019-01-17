package com.oceanprotocol.squid.helpers;

import org.junit.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;

public class StringsHelperTest {

    @Test
    public void wrapWithQuotesAndJoin() {
        List<String> listOfStrings = Arrays.asList("http://localhost:5000/1", "http://localhost:5000/2", "http://localhost:5000/3");
        String result= StringsHelper.wrapWithQuotesAndJoin(listOfStrings);

        assertEquals("\"http://localhost:5000/1\",\"http://localhost:5000/2\",\"http://localhost:5000/3\"",
                result);
    }
}