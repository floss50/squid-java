package com.oceanprotocol.squid.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.oceanprotocol.squid.core.sla.SlaManager;
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

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OceanApiIT {

    private static final Logger log = LogManager.getLogger(OceanApiIT.class);

    private static String METADATA_JSON_SAMPLE = "src/test/resources/examples/metadata.json";
    private static String METADATA_JSON_CONTENT;
    private static AssetMetadata metadataBase;
    private static ServiceEndpoints serviceEndpoints;
    private static final String SERVICE_DEFINITION_ID = "1";


    private static OceanAPI oceanAPI;

    @BeforeClass
    public static void setUp() throws Exception {

        METADATA_JSON_CONTENT =  new String(Files.readAllBytes(Paths.get(METADATA_JSON_SAMPLE)));
        metadataBase = DDO.fromJSON(new TypeReference<AssetMetadata>() {}, METADATA_JSON_CONTENT);

        String metadataUrl= "http://aquarius:5000/api/v1/aquarius/assets/ddo/{did}";
        String consumeUrl= "http://brizo:8030/api/v1/brizo/services/consume?consumerAddress=${consumerAddress}&serviceAgreementId=${serviceAgreementId}&url=${url}";
        String purchaseEndpoint= "http://brizo:8030/api/v1/brizo/services/access/initialize";

        serviceEndpoints= new ServiceEndpoints(consumeUrl, purchaseEndpoint, metadataUrl);

        Config config = ConfigFactory.load();
        oceanAPI = OceanAPI.getInstance(config);

    }


    @Test
    public void buildAPIFromConfig() throws Exception {

        Config config = ConfigFactory.load();

        OceanAPI oceanAPIFromConfig = OceanAPI.getInstance(config);
        assertEquals(config.getString("account.main.address"), oceanAPIFromConfig.getMainAccount().address);

    }

    @Test
    public void registerAsset() throws Exception {

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

        Flowable<OrderResult> response = oceanAPI.getAssetsAPI().order(did, SERVICE_DEFINITION_ID, oceanAPI.getMainAccount());

        OrderResult result = response.blockingFirst();
        assertNotNull(result.getServiceAgreementId());
        assertEquals(true, result.isAccessGranted());

    }
}
