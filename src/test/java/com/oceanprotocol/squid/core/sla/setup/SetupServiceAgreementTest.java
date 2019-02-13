package com.oceanprotocol.squid.core.sla.setup;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SetupServiceAgreementTest {

    private static SetupServiceAgreement setup;
    private static Config config;

    @BeforeClass
    public static void setupServiceAgreementTest() throws Exception {
        setup= new SetupServiceAgreement();
        config= ConfigFactory.load();
    }

    @Test
    public void getContractAddresses() throws Exception {
        List<String> expected= new ArrayList();
        expected.add(config.getString("contract.paymentConditions.address"));
        expected.add(config.getString("contract.accessConditions.address"));
        expected.add(config.getString("contract.paymentConditions.address"));
        expected.add(config.getString("contract.paymentConditions.address"));

        List<String> addresses= setup.getContractAddresses(setup.getAccessTemplate());

        assertEquals(expected.get(0).toLowerCase(), addresses.get(0).toLowerCase());
        assertEquals(expected.get(1).toLowerCase(), addresses.get(1).toLowerCase());
        assertEquals(expected.get(2).toLowerCase(), addresses.get(2).toLowerCase());
        assertEquals(expected.get(3).toLowerCase(), addresses.get(3).toLowerCase());
    }


}