package com.oceanprotocol.squid.manager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oceanprotocol.squid.dto.AquariusDto;
import com.oceanprotocol.squid.dto.KeeperDto;
import com.oceanprotocol.squid.models.DDO;
import com.oceanprotocol.squid.models.DID;
import com.oceanprotocol.squid.models.asset.AssetMetadata;
import com.oceanprotocol.squid.models.service.MetadataService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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

        TEST_ADDRESS = config.getString("account.ganache.address");
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

    @Test
    public void searchAssets() throws Exception {

        DDO ddo1= DDO.fromJSON(new TypeReference<DDO>() {}, DDO_JSON_CONTENT);
        DDO ddo2= DDO.fromJSON(new TypeReference<DDO>() {}, DDO_JSON_CONTENT);
        DDO ddo3= DDO.fromJSON(new TypeReference<DDO>() {}, DDO_JSON_CONTENT);
        DDO ddo4= DDO.fromJSON(new TypeReference<DDO>() {}, DDO_JSON_CONTENT);
        DDO ddo5= DDO.fromJSON(new TypeReference<DDO>() {}, DDO_JSON_CONTENT);

        ddo1.generateDID();
        ddo2.generateDID();
        ddo3.generateDID();
        ddo4.generateDID();
        ddo5.generateDID();

        String randomParam= UUID.randomUUID().toString().replaceAll("-","");
        log.debug("Using random param for search: " + randomParam);

//        ((MetadataService) ddo1.services.get(2)).metadata.base.type = randomParam;
//        ((MetadataService) ddo2.services.get(2)).metadata.base.type = randomParam;
//        ((MetadataService) ddo4.services.get(2)).metadata.base.name = "random name";
        ddo1.metadata.base.type= randomParam;
        ddo2.metadata.base.type= randomParam;
        ddo4.metadata.base.name = "random name";

        aquarius.createDDO(ddo1);
        aquarius.createDDO(ddo2);
        aquarius.createDDO(ddo3);
        aquarius.createDDO(ddo4);
        aquarius.createDDO(ddo5);

        List<DDO> result1= manager.searchAssets(randomParam, 10, 0);

        assertEquals(2, result1.size());
        assertEquals(randomParam,result1.get(0).metadata.base.type);

    }




}