package com.oceanprotocol.squid.core.sla;

import com.fasterxml.jackson.core.type.TypeReference;
import com.oceanprotocol.squid.external.KeeperService;
import com.oceanprotocol.squid.helpers.EthereumHelper;
import com.oceanprotocol.squid.manager.ManagerHelper;
import com.oceanprotocol.squid.models.DDO;
import com.oceanprotocol.squid.models.service.AccessService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import static org.junit.Assert.assertEquals;

public class ServiceAgreementHandlerTest {

    static final Logger log= LogManager.getLogger(ServiceAgreementHandlerTest.class);

    private static ServiceAgreementHandler sla;

    private static final Config config = ConfigFactory.load();
    private static KeeperService keeper;

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
        sla= new ServiceAgreementHandler();
        keeper = ManagerHelper.getKeeper(config, ManagerHelper.VmClient.parity);

        jsonContent = new String(Files.readAllBytes(Paths.get(DDO_JSON_SAMPLE)));
        ddo= DDO.fromJSON(new TypeReference<DDO>() {}, jsonContent);

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
    public void fetchConditionKeyTest() throws UnsupportedEncodingException {

        String FUNCTION_LOCKPAYMENT_DEF= "lockPayment(bytes32,bytes32,uint256)";
        String FUNCTION_GRANTACCESS_DEF= "grantAccess(bytes32,bytes32,bytes32)";

        String targetGrantAccessConditionKey = "0x600b855012216922339cafd208590e02fdd8c8b8bbfd761d951976801a2b2b05";
        String targetLockPaymentConditionKey = "0x1699b99d88626025f8b13de3b666cccec63eaf744d664d901a95b62c36d2b531";

        String templateId = "0x044852b2a670ade5407e78fb2863c51de9fcb96542a07186fe3aeda6bb8a116d";
        String address =  Keys.toChecksumAddress("0x00bd138abd70e2f00903268f3db08f2d25677c9e");

        String grantAccessFingerprint = EthereumHelper.getFunctionSelector(FUNCTION_GRANTACCESS_DEF);
        String lockPaymentFingerprint = EthereumHelper.getFunctionSelector(FUNCTION_LOCKPAYMENT_DEF);

        String grantAccessConditionKey = ServiceAgreementHandler.fetchConditionKey(templateId, address, grantAccessFingerprint);
        assertEquals(grantAccessConditionKey, targetGrantAccessConditionKey);

        String lockPaymentConditionKey = ServiceAgreementHandler.fetchConditionKey(templateId, address, lockPaymentFingerprint);
        assertEquals(lockPaymentConditionKey, targetLockPaymentConditionKey);

    }

}