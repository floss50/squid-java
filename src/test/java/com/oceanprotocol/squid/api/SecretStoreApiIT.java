package com.oceanprotocol.squid.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.oceanprotocol.squid.models.DDO;
import com.oceanprotocol.squid.models.DID;
import com.oceanprotocol.squid.models.asset.AssetMetadata;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SecretStoreApiIT {

    private static final Logger log = LogManager.getLogger(SecretStoreApiIT.class);

    private static String METADATA_JSON_SAMPLE = "src/test/resources/examples/metadata.json";
    private static String METADATA_JSON_CONTENT;
    private static AssetMetadata metadataBase;


    private static OceanAPI oceanAPI;

    @BeforeClass
    public static void setUp() throws Exception {

        METADATA_JSON_CONTENT =  new String(Files.readAllBytes(Paths.get(METADATA_JSON_SAMPLE)));
        metadataBase = DDO.fromJSON(new TypeReference<AssetMetadata>() {}, METADATA_JSON_CONTENT);

        Config config = ConfigFactory.load();
        oceanAPI = OceanAPI.getInstance(config);

        assertNotNull(oceanAPI.getSecretStoreAPI());
        assertNotNull(oceanAPI.getMainAccount());

    }

    @Test
    public void encrypt() throws Exception{

        String filesJson = metadataBase.toJson(metadataBase.base.files);
        String did = DID.builder().getHash();

        String encryptedDocument = oceanAPI.getSecretStoreAPI().encrypt(did, filesJson, 0);
        String decryptedDocument = oceanAPI.getSecretStoreAPI().decrypt(did, encryptedDocument);

        assertEquals(filesJson, decryptedDocument);

    }
}
