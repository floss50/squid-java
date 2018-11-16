package com.oceanprotocol.squid.core.sla;

import com.oceanprotocol.squid.helpers.EthereumHelper;
import com.oceanprotocol.squid.models.service.Condition;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class AccessSLATest {

    private static AccessSLA sla;


    @BeforeClass
    public static void setUp() throws Exception {
        sla= new AccessSLA();
    }

    @Test
    public void initializeConditions() throws Exception {

        Map<String, Object> params= new HashMap<>();
        params.put("parameter.did", "0ebed8226ada17fde24b6bf2b95d27f8f05fcce09139ff5cec31f6d81a7cd2ea");
        params.put("parameter.price", 123);
        params.put("contract.paymentConditions.address", "0x12345678901234567890123456789012");
        params.put("contract.accessConditions.address", "0xaaa45678901234567890123456789012");

        List<Condition> conditions= sla.initializeConditions(params);

        assertEquals(4, conditions.size());
        assertEquals("lockPayment", conditions.get(0).name);
        assertEquals("grantAccess", conditions.get(1).name);
        assertEquals("releasePayment", conditions.get(2).name);
        assertEquals("refundPayment", conditions.get(3).name);

        assertEquals("0x12345678901234567890123456789012",
                conditions.get(0).conditionKey.contractAddress);
        assertEquals("0ebed8226ada17fde24b6bf2b95d27f8f05fcce09139ff5cec31f6d81a7cd2ea",
                conditions.get(0).parameters.get(0).value);
        assertEquals(123, conditions.get(0).parameters.get(1).value);

        assertTrue(conditions.get(0).conditionKey.fingerprint.length() == 10);
        assertTrue(conditions.get(1).conditionKey.fingerprint.length() == 10);
        assertTrue(conditions.get(2).conditionKey.fingerprint.length() == 10);
        assertTrue(conditions.get(3).conditionKey.fingerprint.length() == 10);
    }
}