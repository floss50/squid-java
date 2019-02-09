package com.oceanprotocol.squid.core.sla.setup;

import com.oceanprotocol.squid.helpers.EncodingHelper;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SetupServiceAgreementTest {

    private static SetupServiceAgreement setup;

    @BeforeClass
    public static void setupServiceAgreementTest() throws Exception {
        setup= new SetupServiceAgreement();
    }

    @Test
    public void getContractAddresses() throws Exception {
        List<String> expected= new ArrayList();
        expected.add("0xed0a0123eaeb4e32335d64cab75ed0022938cd59");
        expected.add("0x9e6d17b661a535f0d7126673e0f5d62a561bddf4");
        expected.add("0xed0a0123eaeb4e32335d64cab75ed0022938cd59");
        expected.add("0xed0a0123eaeb4e32335d64cab75ed0022938cd59");

        List<String> addresses= setup.getContractAddresses(setup.getAccessTemplate());

        assertEquals(expected.get(0), addresses.get(0).toLowerCase());
        assertEquals(expected.get(1), addresses.get(1).toLowerCase());
        assertEquals(expected.get(2), addresses.get(2).toLowerCase());
        assertEquals(expected.get(3), addresses.get(3).toLowerCase());
    }


}