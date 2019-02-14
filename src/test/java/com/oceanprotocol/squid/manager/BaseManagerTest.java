package com.oceanprotocol.squid.manager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.oceanprotocol.squid.exceptions.DDOException;
import com.oceanprotocol.squid.external.AquariusService;
import com.oceanprotocol.squid.external.KeeperService;
import com.oceanprotocol.squid.models.DDO;
import com.oceanprotocol.squid.models.asset.AssetMetadata;
import com.oceanprotocol.squid.models.service.MetadataService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.web3j.crypto.CipherException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertNotNull;

public class BaseManagerTest {

    private static final Logger log = LogManager.getLogger(BaseManagerTest.class);


    public static class BaseManagerImplementation extends BaseManager {

        public BaseManagerImplementation(KeeperService keeperService, AquariusService aquariusService) throws IOException, CipherException {
            super(keeperService, aquariusService);
        }

    }

    private static final Config config = ConfigFactory.load();

    private static final String METADATA_JSON_SAMPLE = "src/test/resources/examples/metadata.json";
    private static String METADATA_JSON_CONTENT;

    private static AssetMetadata metadataBase;

    private static KeeperService keeperService;

    private static AquariusService aquarius;
    private static SecretStoreManager secretStore;

    private static BaseManagerImplementation baseManager;

    private static final String SERVICE_AGREEMENT_ADDRESS;
    static {
        SERVICE_AGREEMENT_ADDRESS = config.getString("contract.serviceExecutionAgreement.address");
    }


    @BeforeClass
    public static void setUp() throws Exception {

        log.debug("Setting Up...");

        keeperService = ManagerHelper.getKeeper(config, ManagerHelper.VmClient.parity, "");
        aquarius= ManagerHelper.getAquarius(config);
        secretStore= ManagerHelper.getSecretStoreController(config, ManagerHelper.VmClient.parity);

        baseManager = new BaseManagerImplementation(keeperService, aquarius);
        baseManager.setSecretStoreManager(secretStore);

        METADATA_JSON_CONTENT = new String(Files.readAllBytes(Paths.get(METADATA_JSON_SAMPLE)));
        metadataBase = DDO.fromJSON(new TypeReference<AssetMetadata>() {}, METADATA_JSON_CONTENT);

    }

    @Test
    public void buildDDO() throws DDOException {

        String metadataUrl= "http://172.15.0.15:5000/api/v1/aquarius/assets/ddo/{did}";
        MetadataService metadataService = new MetadataService(metadataBase, metadataUrl);

        DDO ddo = baseManager.buildDDO(metadataService, SERVICE_AGREEMENT_ADDRESS);
        assertNotNull(ddo.proof);
        assertNotNull(ddo.metadata.base.checksum);

    }


}
