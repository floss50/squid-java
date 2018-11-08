package com.oceanprotocol.squid.manager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oceanprotocol.squid.dto.AquariusDto;
import com.oceanprotocol.squid.dto.KeeperDto;
import com.oceanprotocol.squid.models.DDO;
import com.oceanprotocol.squid.models.DID;
import com.oceanprotocol.squid.models.asset.AssetMetadata;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class AssetsControllerIT {

    private static final Logger log = LogManager.getLogger(AssetsControllerIT.class);

    private static AssetsController manager;
    private static KeeperDto keeper;
    private static AquariusDto aquarius;

    private static final String METADATA_URL = "http://myaquarius.org/api/v1/provider/assets/metadata/{did}";
    private static final String DDO_JSON_SAMPLE = "src/test/resources/examples/ddo-example.json";
    private static String DDO_JSON_CONTENT;

    private static ObjectMapper objectMapper;

    private static AssetMetadata metadataBase;
    private static DDO ddoBase;
    private static JSONObject metadataJson;

    private static final Config config = ConfigFactory.load();
    private static String TEST_ADDRESS;


    @BeforeClass
    public static void setUp() throws Exception {
        log.debug("Setting Up DTO's");

        keeper = ManagerHelper.getKeeper(config);
        aquarius = ManagerHelper.getAquarius(config);
        manager = AssetsController.getInstance(keeper, aquarius);

        DDO_JSON_CONTENT = new String(Files.readAllBytes(Paths.get(DDO_JSON_SAMPLE)));
        ddoBase = DDO.fromJSON(new TypeReference<DDO>() {}, DDO_JSON_CONTENT);

        metadataBase = (AssetMetadata) ddoBase.metadata;
        JSONObject metadataJson = new JSONObject(metadataBase);

        objectMapper = new ObjectMapper();

        TEST_ADDRESS = config.getString("account.address");
    }


    @Test
    public void publishMetadata() throws Exception {

        DDO ddo = manager.publishMetadata(metadataBase, METADATA_URL);

        assertTrue(ddo.id.startsWith(DID.PREFIX));
        assertTrue(ddo.id.length() > 32);

        DDO ddoReturned = manager.getByDID(ddo.id);

        assertEquals(ddo.id, ddoReturned.id);
        assertEquals(ddo.metadata.base.name, ddoReturned.metadata.base.name);

        ddo.metadata.base.name = "new name";
        boolean updateStatus = manager.updateMetadata(ddo.id, ddo);
        DDO ddoUpdated = manager.getByDID(ddo.id);

        assertTrue(updateStatus);
        assertEquals("new name", ddoUpdated.metadata.base.name);

    }

    // TODO: Pending of Aquarius search method refactor
    @Test
    public void search() throws Exception {
        //manager.publishMetadata(metadataBase, METADATA_URL);
        //manager.publishMetadata(metadataBase, METADATA_URL);
        //manager.publishMetadata(metadataBase, METADATA_URL);


    }

}