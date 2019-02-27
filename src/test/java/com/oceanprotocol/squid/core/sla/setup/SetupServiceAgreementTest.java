package com.oceanprotocol.squid.core.sla.setup;

import com.fasterxml.jackson.core.type.TypeReference;
import com.oceanprotocol.squid.models.service.template.AccessTemplate;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SetupServiceAgreementTest {

    private static Config config;
    private static final String ACCESS_TEMPLATE_JSON = "src/main/resources/sla/access-sla-template.json";
    private static String ACCESS_TEMPLATE_JSON_CONTENT;
    private static AccessTemplate accessTemplate;

    @BeforeClass
    public static void setupServiceAgreementTest() throws Exception {
        config= ConfigFactory.load();
        ACCESS_TEMPLATE_JSON_CONTENT = new String(Files.readAllBytes(Paths.get(ACCESS_TEMPLATE_JSON)));
        accessTemplate= AccessTemplate.fromJSON(new TypeReference<AccessTemplate>() {}, ACCESS_TEMPLATE_JSON_CONTENT);
    }

    @Test
    public void getContractAddresses() throws Exception {
        List<String> expected= new ArrayList();
        expected.add(config.getString("contract.LockRewardCondition.address"));
        expected.add(config.getString("contract.AccessSecretStoreCondition.address"));
        expected.add(config.getString("contract.LockRewardCondition.address"));
        expected.add(config.getString("contract.LockRewardCondition.address"));

        List<String> addresses= SetupServiceAgreement.getContractAddresses(
                accessTemplate,
                config.getString("contract.LockRewardCondition.address"),
                config.getString("contract.AccessSecretStoreCondition.address"));

        assertEquals(expected.get(0).toLowerCase(), addresses.get(0).toLowerCase());
        assertEquals(expected.get(1).toLowerCase(), addresses.get(1).toLowerCase());
        assertEquals(expected.get(2).toLowerCase(), addresses.get(2).toLowerCase());
        assertEquals(expected.get(3).toLowerCase(), addresses.get(3).toLowerCase());
    }


}