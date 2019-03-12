package com.oceanprotocol.squid.core.sla;

import com.fasterxml.jackson.core.type.TypeReference;
import com.oceanprotocol.squid.external.KeeperService;
import com.oceanprotocol.squid.helpers.EthereumHelper;
import com.oceanprotocol.squid.manager.ManagerHelper;
import com.oceanprotocol.squid.models.DDO;
import com.oceanprotocol.squid.models.service.AccessService;
import com.oceanprotocol.squid.models.service.template.AccessTemplate;
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
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ServiceAgreementHandlerTest {

    static final Logger log= LogManager.getLogger(ServiceAgreementHandlerTest.class);

    private static ServiceAgreementHandler sla;

    private static final Config config = ConfigFactory.load();
    private static KeeperService keeper;

    private static final String TEMPLATE_ID= "";
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



    /*
    Agreementid   '0xc4a15b18fcf343a4b2cda85800a454f6153c1478db8f4b7bacba3ef21129cc26'
Assetid    '0xdaf95108d69844cbaafcd66fd480a244c7751fa9c659437cbc71b0f922b83220'
Consumer address ’0x068Ed00cF0441e4829D9784fCBe7b9e26D4BD8d0'
Publisher address ’0x00Bd138aBD70e2F00903268F3Db08f2D25677C9e'

lockRewardId     ''0x816926c38a73dae566c2b539a2cbac9ff36cda7573fc3b7f30b49a8e5bdfb21c''
	<class 'list'>  : ['address', 'uint256']
    	escrowRewardCondition.address '0xe22570D8ea2D8004023A928FbEb36f14738C62c9'
	price 10

	values_hash = ''0xcc78d90747006ec1da7a0976cd859d4a1328a5ece7c9ecb8eef1a73818346e3c''

accessSecretStoreId  ''0x55301caec9ece3cc2e9540f6711e6d8cf28842e4f0d84b7e4a7930a64ca7a18b''
	<class 'list'>: ['bytes32', 'address']
	assetid ’0x63e40addf15f446683b6ebef6966dc4e37d887b306f8438aa0c621aa39a72aa1'
	consumerAddress '0x068Ed00cF0441e4829D9784fCBe7b9e26D4BD8d0'

	value_hash = ''0xe0c0d92bebd0dbcf6dd6319e1dd3a2bb9a063e04f3ce02d512258ca7dd8af1bc''

escrow_cond_id.    ''0x1792957bac8bf96011ced782d28281bce5a17d8d07ba7f6d3c5474201087c62f''
	<class 'list'>: ['uint256', 'address', 'address', 'bytes32', 'bytes32']
	price 10
	publisher address '0x00Bd138aBD70e2F00903268F3Db08f2D25677C9e'
	consumer address '0x068Ed00cF0441e4829D9784fCBe7b9e26D4BD8d0'
	lockCondition 		'0x402d04764a4092c7d73d5ce8471b02f9c4c0e6d60d70dafdfe85b0de14d0f74f'
	releaseCondition 	'0x6d4a6b3e470ae82da1dc35451e832b97292d73eb2b9dc3c335924cc17eab41e1'

	value_hash= ''0x669d9121d992423cc00b28d77ed6a5a14dd7fe0703c1f1f5c06a89cdcb226514''


contract.LockRewardCondition.address="0x7BE62247c8ea441947e1e3350496401E6523dBc4"
contract.AccessSecretStoreCondition.address="0xb29Dd6383fb5786fea1172026CC181Ff5Cc553B9"
contract.EscrowReward.address="0xe22570D8ea2D8004023A928FbEb36f14738C62c9"


     */

    @Test
    public void generateConditionIds()  throws Exception {

        String agreementId ="0xc4a15b18fcf343a4b2cda85800a454f6153c1478db8f4b7bacba3ef21129cc26";
        String assetId = "0xdaf95108d69844cbaafcd66fd480a244c7751fa9c659437cbc71b0f922b83220";
        String consumerAddress = "0x068Ed00cF0441e4829D9784fCBe7b9e26D4BD8d0";
        String publisherAddress = "0x00Bd138aBD70e2F00903268F3Db08f2D25677C9e";
        String escrowRewardConditionAddress = "0xe22570D8ea2D8004023A928FbEb36f14738C62c9";
        String lockRewardConditionAddress = "0x7BE62247c8ea441947e1e3350496401E6523dBc4";
        String accessSecretStoreConditionAddress = "0xb29Dd6383fb5786fea1172026CC181Ff5Cc553B9";

        AccessService accessService= (AccessService) ddo.services.get(1);

        String lockRewardId = accessService.generateLockRewardId(agreementId, escrowRewardConditionAddress, lockRewardConditionAddress);
        assertEquals("0x816926c38a73dae566c2b539a2cbac9ff36cda7573fc3b7f30b49a8e5bdfb21c", lockRewardId);

        String accessSecretStoreId = accessService.generateAccessSecretStoreConditionId(agreementId, consumerAddress, accessSecretStoreConditionAddress);
        assertEquals("0x55301caec9ece3cc2e9540f6711e6d8cf28842e4f0d84b7e4a7930a64ca7a18b", accessSecretStoreId);

        String escrowRewardId = accessService.generateEscrowRewardConditionId(agreementId, consumerAddress, publisherAddress, escrowRewardConditionAddress, lockRewardId, accessSecretStoreId);
        assertEquals("0x1792957bac8bf96011ced782d28281bce5a17d8d07ba7f6d3c5474201087c62f", escrowRewardId);

    }


    @Test
    public void generateSASignature()  throws Exception {

        String agreementId ="0xc4a15b18fcf343a4b2cda85800a454f6153c1478db8f4b7bacba3ef21129cc26";
        String assetId = "0xdaf95108d69844cbaafcd66fd480a244c7751fa9c659437cbc71b0f922b83220";
        String consumerAddress = "0x068Ed00cF0441e4829D9784fCBe7b9e26D4BD8d0";
        String publisherAddress = "0x00Bd138aBD70e2F00903268F3Db08f2D25677C9e";
        String escrowRewardConditionAddress = "0xe22570D8ea2D8004023A928FbEb36f14738C62c9";
        String lockRewardConditionAddress = "0x7BE62247c8ea441947e1e3350496401E6523dBc4";
        String accessSecretStoreConditionAddress = "0xb29Dd6383fb5786fea1172026CC181Ff5Cc553B9";

        AccessService accessService= (AccessService) ddo.services.get(1);

        String hash= accessService.generateServiceAgreementHash(agreementId, consumerAddress, publisherAddress, lockRewardConditionAddress, accessSecretStoreConditionAddress, escrowRewardConditionAddress);
        assertEquals("0xa25575970920e439cc076f1489e0b820cb2fd91b7a8643165fd26d296fa69ee6", hash);

        // 'bytes32', 'bytes32[]', 'uint256[]', 'uint256[]', 'bytes32'
        // template_id, values_hash_list, timelocks, timeouts, agreement_id

        // '0x044852b2a670ade5407e78fb2863c51de9fcb96542a07186fe3aeda6bb8a116d',
        // <class 'tuple'>: ('0x55301caec9ece3cc2e9540f6711e6d8cf28842e4f0d84b7e4a7930a64ca7a18b', '0x816926c38a73dae566c2b539a2cbac9ff36cda7573fc3b7f30b49a8e5bdfb21c', '0x1792957bac8bf96011ced782d28281bce5a17d8d07ba7f6d3c5474201087c62f')
        // [0,0,0]
        // [0,0,0]
        // '0xc4a15b18fcf343a4b2cda85800a454f6153c1478db8f4b7bacba3ef21129cc26'

        // Signature -> '0xa25575970920e439cc076f1489e0b820cb2fd91b7a8643165fd26d296fa69ee6'
        //'0x2360b2ab58d64f31e2d4c4fbb0e28dfc4b49147ea2b8eafb5a9fb036f32b5a28'


        // 0x044852b2a670ade5407e78fb2863c51de9fcb96542a07186fe3aeda6bb8a116d
        // fae0c90859098a230249453571afa5ee53f7b4dd398b03ec63156c81affeed184d45a9e4f598faa64944688e5bfcc89231e85dbae1974bd0c5fd3278e8ac5b9e383dfc9a1961653de1db9817da4e490afb263ebb2979a67622a5e13e2fb73c23
        // 000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000
        // 453b32e5762a46aba5f23ade5c5e7039195fb3bdb98b4cd89d9ef574e0c0f605



    }

    @Ignore
    @Test
    public void generateServiceAgreementSignature() throws Exception {


        AccessService accessService= (AccessService) ddo.services.get(0);

        String hash= accessService.generateServiceAgreementHash(SERVICEAGREEMENT_ID, "consumerAddress", "publisherAddress", "lockRewardAddress",
                "accessSecretStoreADdress", "escrowRewardAddredd");
        String signature= accessService.generateServiceAgreementSignatureFromHash(keeper.getWeb3(), keeper.getAddress(), hash);

        final String hashTemplateId= Hash.sha3(TEMPLATE_ID);
        //final String hashConditionKeys= Hash.sha3(accessService.fetchConditionKeys());
        final String hashConditionValues= Hash.sha3(accessService.fetchConditionValues());
        final String hashTimeouts= Hash.sha3(accessService.fetchTimeout());
        final String hashServiceAgreementId= Hash.sha3(SERVICEAGREEMENT_ID);

        log.debug("Hash templateId: " + hashTemplateId);
        //log.debug("Hash conditionKeys: " + hashConditionKeys);
        log.debug("Hash conditionValues: " + hashConditionValues);
        log.debug("Hash Timeouts: " + hashTimeouts);
        log.debug("Hash ServiceAgreementId: " + hashServiceAgreementId);



        log.debug("\n-------\n");

        log.debug("Hash: " + hash);
        log.debug("Signature: " + signature);

        assertEquals("hashTemplateId doesn't match", "0x40105d5bc10105c17fd72b93a8f73369e2ee6eee4d4714b7bf7bf3c2f156e601", hashTemplateId);
        //assertEquals("hashConditionKeys Hash doesn't match", "0x5b0fbb997b36bcc10d1543e071c2a859fe21ad8a9f18af6bdeb366a584d091b3", hashConditionKeys);
        assertEquals("hashConditionValues doesn't match", "0xfbb8894170e025ff7aaf7c5278c16fa17f4ea3d1126623ebdac87bd91e70acc2", hashConditionValues);
        assertEquals("hashTimeouts doesn't match", "0x4a0dd5c0cd0686c8feff15f4ec2ff2b3b7009451ee56eb3d10d75d8a7da95c7f", hashTimeouts);
        assertEquals("hashServiceAgreementId doesn't match", "0x922c3379f6140ee422c40a900f23479d22737270ec1439ca87fcb321c6c0c692", hashServiceAgreementId);

        assertEquals(EXPECTED_HASH.length(), hash.length());
        assertEquals(EXPECTED_SIGNATURE.length(), signature.length());

        assertEquals("Error matching the HASH", EXPECTED_HASH, hash);
        assertEquals("Error matching the SIGNATURE", EXPECTED_SIGNATURE, signature);
    }

/*

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
*/
/*

    @Test
    public void getFullfillmentIndices() throws Exception {
        String templateFilePath = "src/main/resources/sla/access-sla-template.json";
        String jsonContent = new String(Files.readAllBytes(Paths.get(templateFilePath)));
        AccessTemplate accessTemplate= AccessTemplate.fromJSON(new TypeReference<AccessTemplate>() {}, jsonContent);

        List<BigInteger> indices = ServiceAgreementHandler.getFullfillmentIndices(accessTemplate.serviceAgreementTemplate.conditions);
        assertEquals(BigInteger.valueOf(2), indices.get(0));
        assertEquals(BigInteger.valueOf(3), indices.get(1));
    }
*/

/*
    @Test
    public void getDependenciesBits() throws Exception {
        String templateFilePath = "src/main/resources/sla/access-sla-template.json";
        String jsonContent = new String(Files.readAllBytes(Paths.get(templateFilePath)));
        AccessTemplate accessTemplate= AccessTemplate.fromJSON(new TypeReference<AccessTemplate>() {}, jsonContent);

//        List<BigInteger> depBits = ServiceAgreementHandler.getDependenciesBits(accessTemplate.conditions);
        List<BigInteger> depBits = ServiceAgreementHandler.getDependenciesBits();
        assertEquals(BigInteger.valueOf(0), depBits.get(0));
        assertEquals(BigInteger.valueOf(1), depBits.get(1));
        assertEquals(BigInteger.valueOf(4), depBits.get(2));
        assertEquals(BigInteger.valueOf(13), depBits.get(3));
    }
*/
}