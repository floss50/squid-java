package com.oceanprotocol.squid.core.sla;

import com.fasterxml.jackson.core.type.TypeReference;
import com.oceanprotocol.keeper.contracts.ServiceAgreement;
import com.oceanprotocol.squid.dto.KeeperDto;
import com.oceanprotocol.squid.helpers.EncodingHelper;
import com.oceanprotocol.squid.manager.ManagerHelper;
import com.oceanprotocol.squid.models.DDO;
import com.oceanprotocol.squid.models.service.AccessService;
import com.oceanprotocol.squid.models.service.Condition;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.reactivex.Flowable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.web3j.crypto.Hash;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AccessSLATest {

    static final Logger log= LogManager.getLogger(AccessSLATest.class);

    private static AccessSLA sla;

    private static final Config config = ConfigFactory.load();
    private static KeeperDto keeper;

    private static final String TEMPLATE_ID= "0x044852b2a670ade5407e78fb2863c51de9fcb96542a07186fe3aeda6bb8a116d";
    private static final String ADDRESS= "0x00bd138abd70e2f00903268f3db08f2d25677c9e";
    private static final String DID= "0ebed8226ada17fde24b6bf2b95d27f8f05fcce09139ff5cec31f6d81a7cd2ea";
    private static final String SERVICEAGREEMENT_ID= "0xf136d6fadecb48fdb2fc1fb420f5a5d1c32d22d9424e47ab9461556e058fefaa";

    private static final String EXPECTED_HASH= "0x66652d0f8f8ec464e67aa6981c17fa1b1644e57d9cfd39b6f1b58ad1b71d61bb";
    private static final String EXPECTED_SIGNATURE= "0x28fbc30b05fe7caf6d8082778ef3aabd17ceeb31d1bba2908354f999da55bb1878d7e2f7242f591d112fe7e93f9a98ef7d7a85af76e54c53f0b8ee5ced96e4271b";

    private static final String DDO_JSON_SAMPLE = "src/test/resources/examples/ddo-generated-example-2.json";

    private static String jsonContent;
    private static DDO ddo;

    @BeforeClass
    public static void setUp() throws Exception {
        sla= new AccessSLA();
        keeper = ManagerHelper.getKeeper(config, ManagerHelper.VmClient.parity);

        jsonContent = new String(Files.readAllBytes(Paths.get(DDO_JSON_SAMPLE)));
        ddo= DDO.fromJSON(new TypeReference<DDO>() {}, jsonContent);

    }

    @Test
    public void initializeConditions() throws Exception {


        Map<String, Object> params= new HashMap<>();
        params.put("parameter.did", DID);
        params.put("parameter.price", 123);

        List<Condition> conditions= sla.initializeConditions(TEMPLATE_ID, ADDRESS, params);

        assertEquals(4, conditions.size());
        assertEquals("lockPayment", conditions.get(0).name);
        assertEquals("grantAccess", conditions.get(1).name);
        assertEquals("releasePayment", conditions.get(2).name);
        assertEquals("refundPayment", conditions.get(3).name);

        assertEquals("d70d1f943c0f7de4f3e5e6fd4f917e1b31c423b94b26d6b1987d09926626d090",
                conditions.get(0).conditionKey);
        assertEquals("0ebed8226ada17fde24b6bf2b95d27f8f05fcce09139ff5cec31f6d81a7cd2ea",
                conditions.get(0).parameters.get(0).value);
        assertEquals(123, conditions.get(0).parameters.get(1).value);

    }


    @Test
    public void initializeDDOConditions() throws Exception {
        String did= "did:op:cb36cf78d87f4ce4a784f17c2a4a694f19f3fbf05b814ac6b0b7197163888865";
        int price= 10;

        Map<String, Object> params= new HashMap<>();
        params.put("parameter.did", did);
        params.put("parameter.price", price);

        List<Condition> conditions= sla.initializeConditions(TEMPLATE_ID, ADDRESS, params);

        AccessService accessService= (AccessService) ddo.services.get(0);
        assertTrue(
                accessService.conditions.get(0).conditionKey.equals("0x2165e057ca19e807eaa52b6d5f82024021d1c1fbf92d3c53d2eb8a1a4de42d3f"));

        String agreementSignature= accessService.generateServiceAgreementHash(SERVICEAGREEMENT_ID);
        assertEquals("0x66652d0f8f8ec464e67aa6981c17fa1b1644e57d9cfd39b6f1b58ad1b71d61bb",
                agreementSignature);

    }

    @Ignore
    @Test
    public void generateServiceAgreementSignature() throws Exception {

        AccessService accessService= (AccessService) ddo.services.get(0);

        String hash= accessService.generateServiceAgreementHash(SERVICEAGREEMENT_ID);
        String signature= accessService.generateServiceAgreementSignatureFromHash(keeper.getWeb3(), keeper.getAddress(), hash);

        final String hashTemplateId= Hash.sha3(TEMPLATE_ID);
        final String hashConditionKeys= Hash.sha3(accessService.fetchConditionKeys());
        final String hashConditionValues= Hash.sha3(accessService.fetchConditionValues());
        final String hashTimeouts= Hash.sha3(accessService.fetchTimeout());
        final String hashServiceAgreementId= Hash.sha3(SERVICEAGREEMENT_ID);

        log.debug("Hash templateId: " + hashTemplateId);
        log.debug("Hash conditionKeys: " + hashConditionKeys);
        log.debug("Hash conditionValues: " + hashConditionValues);
        log.debug("Hash Timeouts: " + hashTimeouts);
        log.debug("Hash ServiceAgreementId: " + hashServiceAgreementId);



        log.debug("\n-------\n");

        log.debug("Hash: " + hash);
        log.debug("Signature: " + signature);

        assertEquals("hashTemplateId doesn't match", "0x40105d5bc10105c17fd72b93a8f73369e2ee6eee4d4714b7bf7bf3c2f156e601", hashTemplateId);
        assertEquals("hashConditionKeys Hash doesn't match", "0x5b0fbb997b36bcc10d1543e071c2a859fe21ad8a9f18af6bdeb366a584d091b3", hashConditionKeys);
        assertEquals("hashConditionValues doesn't match", "0xfbb8894170e025ff7aaf7c5278c16fa17f4ea3d1126623ebdac87bd91e70acc2", hashConditionValues);
        assertEquals("hashTimeouts doesn't match", "0x4a0dd5c0cd0686c8feff15f4ec2ff2b3b7009451ee56eb3d10d75d8a7da95c7f", hashTimeouts);
        assertEquals("hashServiceAgreementId doesn't match", "0x922c3379f6140ee422c40a900f23479d22737270ec1439ca87fcb321c6c0c692", hashServiceAgreementId);

        assertEquals(EXPECTED_HASH.length(), hash.length());
        assertEquals(EXPECTED_SIGNATURE.length(), signature.length());

        assertEquals("Error matching the HASH", EXPECTED_HASH, hash);
        assertEquals("Error matching the SIGNATURE", EXPECTED_SIGNATURE, signature);
    }

    @Test
    public void lockPaymentObservable() throws Exception {
//        AccessService accessService= (AccessService) ddo.services.get(0);
//        String signature= accessService.generateServiceAgreementSignature(keeper.getWeb3(), keeper.getAddress(), SERVICEAGREEMENT_ID);


        ServiceAgreement.ExecuteAgreementEventResponse response= new ServiceAgreement.ExecuteAgreementEventResponse();
        response.did= EncodingHelper.hexStringToBytes(DID);
        response.serviceAgreementId= EncodingHelper.hexStringToBytes(SERVICEAGREEMENT_ID);
        response.templateId= EncodingHelper.hexStringToBytes(TEMPLATE_ID);

        List<ServiceAgreement.ExecuteAgreementEventResponse> responses= new ArrayList<>();
        responses.add(response);

        Flowable<ServiceAgreement.ExecuteAgreementEventResponse> flowable = Flowable.just(response);

        SlaManager manager= new SlaManager();
        manager.registerExecuteAgreementFlowable(flowable);

    }


}