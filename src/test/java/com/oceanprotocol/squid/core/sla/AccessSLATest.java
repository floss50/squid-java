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
import org.junit.Test;

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
    private static final String SERVICEAGREEMENT_ID= "9899898095860594860852938293058340583405834056000000000000000002";

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
        assertEquals("044852b2a670ade5407e78fb2863c51de9fcb96542a07186fe3aeda6bb8a116d2165e057ca19e807eaa52b6d5f82024021d1c1fbf92d3c53d2eb8a1a4de42d3f5c0b248ab89b89638a6ef7020afbe7390c90c1debebfb93f06577a221e455655c7b899951bb944225768dcc8173572e641b4b62aad4d1f42f59132c6f4eb9a6274901f13c534f069cb9523bacb4f617f4724a2910eae6a82f6fcec7adf28ac4c307863623336636637386438376634636534613738346631376332613461363934663139663366626630356238313461633662306237313937313633383838383635000000000000000000000000000000000000000000000000000000000000000a307863623336636637386438376634636534613738346631376332613461363934663139663366626630356238313461633662306237313937313633383838383635307863623336636637386438376634636534613738346631376332613461363934663139663366626630356238313461633662306237313937313633383838383635307863623336636637386438376634636534613738346631376332613461363934663139663366626630356238313461633662306237313937313633383838383635000000000000000000000000000000000000000000000000000000000000000a307863623336636637386438376634636534613738346631376332613461363934663139663366626630356238313461633662306237313937313633383838383635000000000000000000000000000000000000000000000000000000000000000a0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000a000000000000000000000000000000000000000000000000000000000000000a000000000000000000000000000000000000000000000000000000000000000a9899898095860594860852938293058340583405834056000000000000000002",
                agreementSignature);

    }

    @Test
    public void generateServiceAgreementSignature() throws Exception {

        AccessService accessService= (AccessService) ddo.services.get(0);

        String signature= accessService.generateServiceAgreementSignature(keeper.getWeb3(), keeper.getAddress(), SERVICEAGREEMENT_ID);

        assertTrue(signature.length() == 132);
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