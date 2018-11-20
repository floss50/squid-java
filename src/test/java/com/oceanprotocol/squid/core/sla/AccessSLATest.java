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

        String templateId= "044852b2a670ade5407e78fb2863c51000000000000000000000000000000002";
        String address= "0x00bd138abd70e2f00903268f3db08f2d25677c9e";

        Map<String, Object> params= new HashMap<>();
        params.put("parameter.did", "0ebed8226ada17fde24b6bf2b95d27f8f05fcce09139ff5cec31f6d81a7cd2ea");
        params.put("parameter.price", 123);

        List<Condition> conditions= sla.initializeConditions(templateId, address, params);

        assertEquals(4, conditions.size());
        assertEquals("lockPayment", conditions.get(0).name);
        assertEquals("grantAccess", conditions.get(1).name);
        assertEquals("releasePayment", conditions.get(2).name);
        assertEquals("refundPayment", conditions.get(3).name);

        assertEquals("1393ee702c2777f980d84f674b8de0bbb84def6e04b9f12cc32fafc6fb977cf9",
                conditions.get(0).conditionKey);
        assertEquals("0ebed8226ada17fde24b6bf2b95d27f8f05fcce09139ff5cec31f6d81a7cd2ea",
                conditions.get(0).parameters.get(0).value);
        assertEquals(123, conditions.get(0).parameters.get(1).value);

    }
}