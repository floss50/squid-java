package com.oceanprotocol.squid.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.oceanprotocol.squid.api.config.OceanConfig;
import com.oceanprotocol.squid.models.Account;
import com.oceanprotocol.squid.models.DDO;
import com.oceanprotocol.squid.models.DID;
import com.oceanprotocol.squid.models.asset.AssetMetadata;
import com.oceanprotocol.squid.models.asset.OrderResult;
import com.oceanprotocol.squid.models.service.ServiceEndpoints;
import com.oceanprotocol.squid.core.sla.setup.SetupServiceAgreement;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.reactivex.Flowable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AssetsApiIT {

    private static final Logger log = LogManager.getLogger(AssetsApiIT.class);

    private static String METADATA_JSON_SAMPLE = "src/test/resources/examples/metadata.json";
    private static String METADATA_JSON_CONTENT;
    private static AssetMetadata metadataBase;
    private static ServiceEndpoints serviceEndpoints;
    private static final String SERVICE_DEFINITION_ID = "1";
    private static SetupServiceAgreement setupServiceAgreement;


    private static OceanAPI oceanAPI;
    private static OceanAPI oceanAPIConsumer;

    @BeforeClass
    public static void setUp() throws Exception {

        METADATA_JSON_CONTENT =  new String(Files.readAllBytes(Paths.get(METADATA_JSON_SAMPLE)));
        metadataBase = DDO.fromJSON(new TypeReference<AssetMetadata>() {}, METADATA_JSON_CONTENT);

        String metadataUrl= "http://172.15.0.15:5000/api/v1/aquarius/assets/ddo/{did}";
        String consumeUrl= "http://localhost:8030/api/v1/brizo/services/consume?consumerAddress=${consumerAddress}&serviceAgreementId=${serviceAgreementId}&url=${url}";
        String purchaseEndpoint= "http://localhost:8030/api/v1/brizo/services/access/initialize";

        serviceEndpoints= new ServiceEndpoints(consumeUrl, purchaseEndpoint, metadataUrl);

        Config config = ConfigFactory.load();
        oceanAPI = OceanAPI.getInstance(config);

        assertNotNull(oceanAPI.getAssetsAPI());
        assertNotNull(oceanAPI.getMainAccount());
        setupServiceAgreement = new SetupServiceAgreement();
        setupServiceAgreement.registerTemplate();

        Properties properties = new Properties();
        properties.put(OceanConfig.KEEPER_URL, config.getString("keeper.url"));
        properties.put(OceanConfig.KEEPER_GAS_LIMIT, config.getString("keeper.gasLimit"));
        properties.put(OceanConfig.KEEPER_GAS_PRICE, config.getString("keeper.gasPrice"));
        properties.put(OceanConfig.AQUARIUS_URL, config.getString("aquarius.url"));
        properties.put(OceanConfig.SECRETSTORE_URL, config.getString("secretstore.url"));
        properties.put(OceanConfig.CONSUME_BASE_PATH, config.getString("consume.basePath"));
        properties.put(OceanConfig.MAIN_ACCOUNT_ADDRESS, config.getString("account.parity.address2"));
        properties.put(OceanConfig.MAIN_ACCOUNT_PASSWORD,  config.getString("account.parity.password2"));
        properties.put(OceanConfig.MAIN_ACCOUNT_CREDENTIALS_FILE, config.getString("account.parity.file2"));
        properties.put(OceanConfig.DID_REGISTRY_ADDRESS, config.getString("contract.didRegistry.address"));
        properties.put(OceanConfig.SERVICE_EXECUTION_AGREEMENT_ADDRESS, config.getString("contract.serviceExecutionAgreement.address"));
        properties.put(OceanConfig.PAYMENT_CONDITIONS_ADDRESS,config.getString("contract.paymentConditions.address"));
        properties.put(OceanConfig.ACCESS_CONDITIONS_ADDRESS, config.getString("contract.accessConditions.address"));
        properties.put(OceanConfig.TOKEN_ADDRESS, config.getString("contract.token.address"));
        properties.put(OceanConfig.DISPENSER_ADDRESS, config.getString("contract.dispenser.address"));

        oceanAPIConsumer = OceanAPI.getInstance(properties);

    }

    @Test
    public void create() throws Exception {

        DDO ddo = oceanAPI.getAssetsAPI().create(metadataBase, oceanAPI.getMainAccount(), serviceEndpoints);

        DID did= new DID(ddo.id);
        DDO resolvedDDO= oceanAPI.getAssetsAPI().resolve(did);
        assertEquals(ddo.id, resolvedDDO.id);
        assertTrue( resolvedDDO.services.size() == 2);

    }

    @Test
    public void order() throws Exception {

        DDO ddo= oceanAPI.getAssetsAPI().create(metadataBase, oceanAPI.getMainAccount(), serviceEndpoints);
        DID did= new DID(ddo.id);

        Flowable<OrderResult> response = oceanAPIConsumer.getAssetsAPI().order(did, SERVICE_DEFINITION_ID, oceanAPIConsumer.getMainAccount());

        OrderResult result = response.blockingFirst();
        assertNotNull(result.getServiceAgreementId());
        assertEquals(true, result.isAccessGranted());

    }

    @Test
    public void consume() throws Exception {

        DDO ddo= oceanAPI.getAssetsAPI().create(metadataBase, oceanAPI.getMainAccount(), serviceEndpoints);
        DID did= new DID(ddo.id);

        log.debug("DDO registered!");

        Flowable<OrderResult> response = oceanAPIConsumer.getAssetsAPI().order(did, SERVICE_DEFINITION_ID, oceanAPIConsumer.getMainAccount());

        OrderResult orderResult = response.blockingFirst();
        assertNotNull(orderResult.getServiceAgreementId());
        assertEquals(true, orderResult.isAccessGranted());
        log.debug("Granted Access Received for the service Agreement " + orderResult.getServiceAgreementId());

        boolean result = oceanAPIConsumer.getAssetsAPI().consume(orderResult.getServiceAgreementId(), did, SERVICE_DEFINITION_ID, oceanAPIConsumer.getMainAccount(), "/tmp");
        assertEquals(true, result);

    }

    @Test
    public void search() throws Exception {

        oceanAPI.getAssetsAPI().create(metadataBase, oceanAPI.getMainAccount(), serviceEndpoints);
        log.debug("DDO registered!");

        String searchText = "Weather";

        List<DDO> results = oceanAPI.getAssetsAPI().search(searchText);
        assertNotNull(results);

    }

    @Test
    public void query() throws Exception {

        oceanAPI.getAssetsAPI().create(metadataBase, oceanAPI.getMainAccount(), serviceEndpoints);
        log.debug("DDO registered!");

        Map<String, Object> params = new HashMap<>();
        params.put("license", "CC-BY");

        List<DDO> results = oceanAPI.getAssetsAPI().query(params);
        assertNotNull(results);

    }
}
