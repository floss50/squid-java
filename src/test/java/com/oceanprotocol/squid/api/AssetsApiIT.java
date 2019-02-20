package com.oceanprotocol.squid.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.oceanprotocol.squid.models.DDO;
import com.oceanprotocol.squid.models.DID;
import com.oceanprotocol.squid.models.asset.AssetMetadata;
import com.oceanprotocol.squid.models.asset.OrderResult;
import com.oceanprotocol.squid.models.service.Service;
import com.oceanprotocol.squid.models.service.ServiceEndpoints;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AssetsApiIT {

    private static final Logger log = LogManager.getLogger(AssetsApiIT.class);

    private static String METADATA_JSON_SAMPLE = "src/test/resources/examples/metadata.json";
    private static String METADATA_JSON_CONTENT;
    private static AssetMetadata metadataBase;
    private static ServiceEndpoints serviceEndpoints;


    private static OceanAPI oceanAPI;

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

    }

    @Test
    public void create() throws Exception {

        DDO ddo = oceanAPI.getAssetsAPI().create(metadataBase, serviceEndpoints);

        DID did= new DID(ddo.id);
        DDO resolvedDDO= oceanAPI.getAssetsAPI().resolve(did);
        assertEquals(ddo.id, resolvedDDO.id);
        assertTrue( resolvedDDO.services.size() == 2);

    }


    @Test
    public void order() throws Exception {

        DDO ddo= oceanAPI.getAssetsAPI().create(metadataBase, serviceEndpoints);
        DID did= new DID(ddo.id);

        Flowable<OrderResult> response = oceanAPI.getAssetsAPI().order(did, Service.DEFAULT_ACCESS_SERVICE_ID);

        OrderResult result = response.blockingFirst();
        assertNotNull(result.getServiceAgreementId());
        assertEquals(true, result.isAccessGranted());

    }

    @Test
    public void consume() throws Exception {

        DDO ddo= oceanAPI.getAssetsAPI().create(metadataBase, serviceEndpoints);
        DID did= new DID(ddo.id);

        log.debug("DDO registered!");

        Flowable<OrderResult> response = oceanAPI.getAssetsAPI().order(did,  Service.DEFAULT_ACCESS_SERVICE_ID);

        OrderResult orderResult = response.blockingFirst();
        assertNotNull(orderResult.getServiceAgreementId());
        assertEquals(true, orderResult.isAccessGranted());
        log.debug("Granted Access Received for the service Agreement " + orderResult.getServiceAgreementId());

        boolean result = oceanAPI.getAssetsAPI().consume(orderResult.getServiceAgreementId(), did, Service.DEFAULT_ACCESS_SERVICE_ID, "/tmp");
        assertEquals(true, result);

    }

    @Test
    public void search() throws Exception {

        oceanAPI.getAssetsAPI().create(metadataBase, serviceEndpoints);
        log.debug("DDO registered!");

        String searchText = "Weather";

        List<DDO> results = oceanAPI.getAssetsAPI().search(searchText);
        assertNotNull(results);

    }

    @Test
    public void query() throws Exception {

        oceanAPI.getAssetsAPI().create(metadataBase, serviceEndpoints);
        log.debug("DDO registered!");

        Map<String, Object> params = new HashMap<>();
        params.put("license", "CC-BY");

        List<DDO> results = oceanAPI.getAssetsAPI().query(params);
        assertNotNull(results);

    }
}
