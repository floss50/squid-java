package com.oceanprotocol.squid.manager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.oceanprotocol.keeper.contracts.AccessConditions;
import com.oceanprotocol.keeper.contracts.DIDRegistry;
import com.oceanprotocol.keeper.contracts.PaymentConditions;
import com.oceanprotocol.keeper.contracts.ServiceAgreement;
import com.oceanprotocol.secretstore.core.EvmDto;
import com.oceanprotocol.secretstore.core.SecretStoreDto;
import com.oceanprotocol.squid.dto.AquariusDto;
import com.oceanprotocol.squid.dto.KeeperDto;
import com.oceanprotocol.squid.models.AbstractModel;
import com.oceanprotocol.squid.models.DDO;
import com.oceanprotocol.squid.models.DID;
import com.oceanprotocol.squid.models.asset.AssetMetadata;
import com.oceanprotocol.squid.models.service.Endpoints;
import com.oceanprotocol.squid.models.service.MetadataService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.web3j.protocol.Web3j;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OceanManagerIT {

    private static final Logger log = LogManager.getLogger(OceanManagerIT.class);

    private static final String DDO_JSON_SAMPLE = "src/test/resources/examples/ddo-example.json";
    private static String DDO_JSON_CONTENT;
    private static final String METADATA_JSON_SAMPLE = "src/test/resources/examples/metadata.json";
    private static String METADATA_JSON_CONTENT;

    private static DDO ddoBase;
    private static AssetMetadata metadataBase;

    private static OceanController manager;
    private static KeeperDto keeper;
    private static AquariusDto aquarius;
    private static SecretStoreController secretStore;

    private static DIDRegistry didRegistry;
    private static ServiceAgreement saContract;
    private static PaymentConditions paymentConditions;
    private static AccessConditions accessConditions;

    private static final Config config = ConfigFactory.load();

    @BeforeClass
    public static void setUp() throws Exception {
        log.debug("Setting Up DTO's");


        // Initializing DTO's
        keeper= ManagerHelper.getKeeper(config, ManagerHelper.VmClient.parity);
        aquarius= ManagerHelper.getAquarius(config);
        secretStore= ManagerHelper.getSecretStoreController(config, ManagerHelper.VmClient.parity);
        didRegistry= ManagerHelper.deployDIDRegistryContract(keeper);
        saContract= ManagerHelper.deployServiceAgreementContract(keeper);
        accessConditions= ManagerHelper.deployAccessConditionsContract(keeper, saContract.getContractAddress());
        paymentConditions= ManagerHelper.deployPaymentConditionsContract(keeper, saContract.getContractAddress(), accessConditions.getContractAddress());

        // Initializing the OceanController
        manager= OceanController.getInstance(keeper, aquarius);
        manager.setSecretStoreController(secretStore)
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
                manager.getKeeperDto().getWeb3().getClass().getInterfaces()[0].isAssignableFrom(Web3j.class));
        assertTrue(
                manager.getAquariusDto().getClass().isAssignableFrom(AquariusDto.class));
    }


    @Test
    public void searchOrders() {

    }

    @Test
    public void registerAsset() throws Exception {
        String publicKey= config.getString("account.ganache.address");
        String metadataUrl= "http://localhost:5000/api/v1/aquarius/assets/ddo/{did}";
        String consumeUrl= "http://mybrizo.org/api/v1/brizo/services/consume?pubKey=${pubKey}&serviceId={serviceId}&url={url}";
        String purchaseEndpoint= "http://mybrizo.org/api/v1/brizo/services/access/initialize";


        Endpoints serviceEndpoints= new Endpoints(consumeUrl, purchaseEndpoint, metadataUrl);

        DDO ddo= manager.registerAsset(metadataBase, publicKey, serviceEndpoints, 0);
        DDO resolvedDDO= manager.resolveDID(new DID(ddo.id));

        assertEquals(ddo.id, resolvedDDO.id);
        assertEquals(metadataUrl, resolvedDDO.services.get(0).serviceEndpoint);
        assertTrue( resolvedDDO.services.size() == 2);
    }



    @Test
    public void resolveDID() throws Exception {

        DID did= DID.builder();
        String oldUrl= "http://mymetadata.io/api";
        String newUrl= "http://localhost:5000/api/v1/aquarius/assets/ddo/{did}";

        ddoBase.id = did.toString();

        ddoBase.services.get(0).serviceEndpoint = newUrl;
        aquarius.createDDO(ddoBase);

        boolean didRegistered= manager.registerDID(did, oldUrl);
        assertTrue(didRegistered);

        log.debug("Registering " + did.toString());
        manager.registerDID(did, newUrl);

        DDO ddo= manager.resolveDID(did);
        assertEquals(did.getDid(), ddo.id);
        assertEquals(newUrl, ddo.services.get(0).serviceEndpoint);
    }


    @Test
    public void getOrder() {
    }
}