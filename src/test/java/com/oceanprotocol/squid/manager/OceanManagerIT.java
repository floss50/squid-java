package com.oceanprotocol.squid.manager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.oceanprotocol.keeper.contracts.AccessConditions;
import com.oceanprotocol.keeper.contracts.DIDRegistry;
import com.oceanprotocol.keeper.contracts.PaymentConditions;
import com.oceanprotocol.keeper.contracts.ServiceAgreement;
import com.oceanprotocol.squid.dto.AquariusDto;
import com.oceanprotocol.squid.dto.KeeperDto;
import com.oceanprotocol.squid.models.Account;
import com.oceanprotocol.squid.models.DDO;
import com.oceanprotocol.squid.models.DID;
import com.oceanprotocol.squid.models.asset.AssetMetadata;
import com.oceanprotocol.squid.models.asset.OrderResult;
import com.oceanprotocol.squid.models.service.ServiceEndpoints;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.reactivex.Flowable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.web3j.protocol.admin.Admin;
import java.nio.file.Files;
import java.nio.file.Paths;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OceanManagerIT {

    private static final Logger log = LogManager.getLogger(OceanManagerIT.class);

    private static final String DDO_JSON_SAMPLE = "src/test/resources/examples/ddo-example.json";
    private static String DDO_JSON_CONTENT;
    private static final String METADATA_JSON_SAMPLE = "src/test/resources/examples/metadata.json";
    private static String METADATA_JSON_CONTENT;

    private static DDO ddoBase;
    private static AssetMetadata metadataBase;

    private static OceanManager managerPublisher;
    private static OceanManager managerConsumer;

    private static KeeperDto keeperPublisher;
    private static KeeperDto keeperConsumer;

    private static AquariusDto aquarius;
    private static SecretStoreManager secretStore;

    private static DIDRegistry didRegistry;
    private static ServiceAgreement saContract;
    private static PaymentConditions paymentConditions;
    private static AccessConditions accessConditions;

    private static final String SERVICE_DEFINITION_ID = "1";

    private static final Config config = ConfigFactory.load();

    private static final String PURCHASE_ADDRESS;
    static {
        PURCHASE_ADDRESS = config.getString("account.parity.address");
    }

    private static final String PURCHASE_PASSWORD;
    static {
        PURCHASE_PASSWORD = config.getString("account.parity.password");
    }

    private static final String DID_REGISTRY_CONTRACT;
    static {
        DID_REGISTRY_CONTRACT = config.getString("contract.didRegistry.address");
    }

    private static final String SERVICE_AGREEMENT_CONTRACT;
    static {
        SERVICE_AGREEMENT_CONTRACT = config.getString("contract.serviceAgreement.address");
    }

    private static final String PAYMENT_CONDITIONS_CONTRACT;
    static {
        PAYMENT_CONDITIONS_CONTRACT = config.getString("contract.paymentConditions.address");
    }

    private static final String ACCESS_CONDITIONS_CONTRACT;
    static {
        ACCESS_CONDITIONS_CONTRACT = config.getString("contract.accessConditions.address");
    }

    private static final String CONSUME_BASE_PATH;
    static {
        CONSUME_BASE_PATH = config.getString("consume.basePath");
    }

    @BeforeClass
    public static void setUp() throws Exception {
        log.debug("Setting Up DTO's");

        keeperPublisher = ManagerHelper.getKeeper(config, ManagerHelper.VmClient.parity, "");
        keeperConsumer = ManagerHelper.getKeeper(config, ManagerHelper.VmClient.parity, "2");

        aquarius= ManagerHelper.getAquarius(config);
        secretStore= ManagerHelper.getSecretStoreController(config, ManagerHelper.VmClient.parity);

        didRegistry= ManagerHelper.loadDIDRegistryContract(keeperPublisher, DID_REGISTRY_CONTRACT);
        saContract= ManagerHelper.loadServiceAgreementContract(keeperPublisher, SERVICE_AGREEMENT_CONTRACT);
        accessConditions= ManagerHelper.loadAccessConditionsContract(keeperPublisher, ACCESS_CONDITIONS_CONTRACT);
        paymentConditions= ManagerHelper.loadPaymentConditionsContract(keeperPublisher, PAYMENT_CONDITIONS_CONTRACT);

        // Initializing the OceanManager for the Publisher
        managerPublisher = OceanManager.getInstance(keeperPublisher, aquarius);
        managerPublisher.setSecretStoreManager(secretStore)
                .setDidRegistryContract(didRegistry)
                .setServiceAgreementContract(saContract)
                .setPaymentConditionsContract(paymentConditions)
                .setAccessConditionsContract(accessConditions);

        // Initializing the OceanManager for the Consumer
        managerConsumer = OceanManager.getInstance(keeperConsumer, aquarius);
        managerConsumer.setSecretStoreManager(secretStore)
                .setDidRegistryContract(didRegistry)
                .setServiceAgreementContract(saContract)
                .setPaymentConditionsContract(paymentConditions)
                .setAccessConditionsContract(accessConditions);

        // Pre-parsing of json's and models
        DDO_JSON_CONTENT = new String(Files.readAllBytes(Paths.get(DDO_JSON_SAMPLE)));
        ddoBase = DDO.fromJSON(new TypeReference<DDO>() {}, DDO_JSON_CONTENT);

        METADATA_JSON_CONTENT = new String(Files.readAllBytes(Paths.get(METADATA_JSON_SAMPLE)));
        metadataBase = DDO.fromJSON(new TypeReference<AssetMetadata>() {}, METADATA_JSON_CONTENT);

    }

    @Test
    public void getInstance() {
        // Checking if web3j driver included in KeeperDto implements the Web3j interface
        assertTrue(
                managerPublisher.getKeeperDto().getWeb3().getClass().getInterfaces()[0].isAssignableFrom(Admin.class));
        assertTrue(
                managerPublisher.getAquariusDto().getClass().isAssignableFrom(AquariusDto.class));
    }

    @Test
    public void searchOrders() {

    }

    private DDO newRegisteredAsset() throws Exception {

        metadataBase = DDO.fromJSON(new TypeReference<AssetMetadata>() {}, METADATA_JSON_CONTENT);

        String publicKey= config.getString("account.parity.address");
        String metadataUrl= "http://172.15.0.15:5000/api/v1/aquarius/assets/ddo/{did}";
        String consumeUrl= "http://localhost:8030/api/v1/brizo/services/consume?consumerAddress=${consumerAddress}&serviceAgreementId=${serviceAgreementId}&url=${url}";
        String purchaseEndpoint= "http://localhost:8030/api/v1/brizo/services/access/initialize";

        String serviceAgreementAddress = saContract.getContractAddress();

        ServiceEndpoints serviceEndpoints= new ServiceEndpoints(consumeUrl, purchaseEndpoint, metadataUrl);

        return managerPublisher.registerAsset(metadataBase,
                serviceAgreementAddress,
                serviceEndpoints,
                0);

    }

    @Test
    public void registerAsset() throws Exception {

        String publicKey= config.getString("account.parity.address");
        String metadataUrl= "http://172.15.0.15:5000/api/v1/aquarius/assets/ddo/{did}";
        String consumeUrl= "http://localhost:8030/api/v1/brizo/services/consume?consumerAddress=${consumerAddress}&serviceAgreementId=${serviceAgreementId}&url=${url}";
        String purchaseEndpoint= "http://localhost:8030/api/v1/brizo/services/access/initialize";

        String serviceAgreementAddress = saContract.getContractAddress();

        ServiceEndpoints serviceEndpoints= new ServiceEndpoints(consumeUrl, purchaseEndpoint, metadataUrl);

        DDO ddo= managerPublisher.registerAsset(metadataBase,
                serviceAgreementAddress,
                serviceEndpoints,
                0);

        DID did= new DID(ddo.id);
        DDO resolvedDDO= managerPublisher.resolveDID(did);

        assertEquals(ddo.id, resolvedDDO.id);
        assertEquals(metadataUrl, resolvedDDO.services.get(0).serviceEndpoint);
        assertTrue( resolvedDDO.services.size() == 2);

    }

    @Test
    public void resolveDID() throws Exception {

        DID did= DID.builder();
        String oldUrl= "http://mymetadata.io/api";
        String newUrl= "http://172.15.0.15:5000/api/v1/aquarius/assets/ddo/{did}";

        ddoBase.id = did.toString();

        ddoBase.services.get(0).serviceEndpoint = newUrl;
        aquarius.createDDO(ddoBase);

        boolean didRegistered= managerPublisher.registerDID(did, oldUrl);
        assertTrue(didRegistered);

        log.debug("Registering " + did.toString());
        managerPublisher.registerDID(did, newUrl);

        DDO ddo= managerPublisher.resolveDID(did);
        assertEquals(did.getDid(), ddo.id);
        assertEquals(newUrl, ddo.services.get(0).serviceEndpoint);
    }


    @Test
    public void purchaseAsset() throws Exception {

        DDO ddo= newRegisteredAsset();
        DID did= new DID(ddo.id);

        Flowable<OrderResult> response =
                managerConsumer.purchaseAsset(did, SERVICE_DEFINITION_ID, new Account(PURCHASE_ADDRESS, PURCHASE_PASSWORD));

        // blocking for testing purpose
        OrderResult result = response.blockingFirst();
        assertNotNull(result.getServiceAgreementId());
        assertEquals(true, result.isAccessGranted());
    }

    @Test
    public void consumerAsset() throws Exception {

        DDO ddo= newRegisteredAsset();
        DID did= new DID(ddo.id);

        log.debug("DDO registered!");

        Flowable<OrderResult> response =
                managerConsumer.purchaseAsset(did, SERVICE_DEFINITION_ID, new Account(PURCHASE_ADDRESS, PURCHASE_PASSWORD));

        // blocking for testing purpose
        log.debug("Waiting for granted Access............");
        OrderResult orderResult = response.blockingFirst();
        assertEquals(true, orderResult.isAccessGranted());

        log.debug("Granted Access Received for the service Agreement " + orderResult.getServiceAgreementId());
        boolean result = managerConsumer.consume("0x" + orderResult.getServiceAgreementId(), did, SERVICE_DEFINITION_ID, PURCHASE_ADDRESS,  CONSUME_BASE_PATH);
        assertEquals(true, result);

    }


    @Test
    public void getOrder() {
    }

}