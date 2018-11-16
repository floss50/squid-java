package com.oceanprotocol.squid.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oceanprotocol.squid.helpers.CryptoHelper;
import com.oceanprotocol.squid.helpers.EncodingHelper;
import com.oceanprotocol.squid.models.asset.AssetMetadata;
import com.oceanprotocol.squid.models.service.AccessService;
import com.oceanprotocol.squid.models.service.MetadataService;
import com.oceanprotocol.squid.models.service.Service;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PublicKey;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class DDOTest {

    static final Logger log = LogManager.getLogger(DDOTest.class);

    // DDO example downloaded from w3c site
    private static final String DDO_JSON_SAMPLE = "src/test/resources/examples/ddo-example.json";
    private static String DDO_JSON_CONTENT;

    private static ObjectMapper objectMapper;

    private static final Config config = ConfigFactory.load();


    @BeforeClass
    public static void setUp() throws Exception {
        DDO_JSON_CONTENT = new String(Files.readAllBytes(Paths.get(DDO_JSON_SAMPLE)));
        objectMapper = new ObjectMapper();
    }

    @Test
    public void generateDID() throws Exception {
        DDO ddo = DDO.fromJSON(new TypeReference<DDO>() {
        }, DDO_JSON_CONTENT);
        String account = config.getString("account.ganache.address");

        assertTrue(ddo.id.startsWith(DID.PREFIX));
        assertEquals(64, ddo.getDid().getHash().length());
    }

    @Test
    public void generateRandomDID() throws Exception {
        DID did= DID.builder();
        //String token= EncodingHelper.encodeToHex(did.getHash());

        assertEquals(64, did.getHash().length());
    }

    @Test
    public void jsonToModel() throws Exception {

        DDO ddo = DDO.fromJSON(new TypeReference<DDO>() {
        }, DDO_JSON_CONTENT);

        assertEquals("https://w3id.org/future-method/v1", ddo.context);
        assertEquals("did:op:3809174ce71dd460faf4941140323ebafdc062f062d3932fe0195c78719a8716", ddo.id.toString());
        assertEquals(3, ddo.publicKeys.size());
        assertTrue(ddo.publicKeys.get(0).id.startsWith("did:op:b6e2eb5eff1a093ced9826315d5a4ef6c5b5c8bd3c49890ee284231d7e1d0aaa"));

        assertEquals(2, ddo.authentication.size());
        assertTrue(ddo.authentication.get(0).publicKey.startsWith("did:op:0ebed8226ada17fde24b6bf2b95d27f8f05fcce09139ff5cec31f6d81a7cd2ea"));

        assertEquals(3, ddo.services.size());
        assertTrue(ddo.services.get(2).serviceEndpoint.startsWith("http"));

        AssetMetadata metadata = (AssetMetadata) ddo.metadata;

        assertEquals("UK Weather information 2011", metadata.base.name);
        assertEquals(2, metadata.base.links.size());
        assertEquals(123, metadata.curation.numVotes);
    }

    @Test
    public void modelToJson() throws Exception {
        String did = "did:op:12345";
        DDO ddo = new DDO();

        DDO.PublicKey pk = new DDO.PublicKey();
        pk.id = did;
        pk.type = "RsaVerificationKey2018";
        pk.owner = did + "owner";

        ddo.publicKeys.add(pk);
        ddo.publicKeys.add(pk);

        DDO.Authentication auth = new DDO.Authentication();
        auth.type = "AuthType";
        auth.publicKey = "AuthPK";

        ddo.authentication.add(auth);
        ddo.authentication.add(auth);
        ddo.authentication.add(auth);

        AssetMetadata metadata = new AssetMetadata();
        AssetMetadata.Base base = new AssetMetadata.Base();
        base.name = "test name";
        base.contentUrls = new ArrayList<String>() {{
            add("http://service.net");
        }};

        metadata.base = base;

        MetadataService metadataService = new MetadataService(metadata, "http://disney.com", "0");

        AccessService accessService = new AccessService("http://ocean.com", "1");

        ddo.services.add(metadataService);
        ddo.services.add(accessService);


        String modelJson = ddo.toJson();
        log.debug(modelJson);

        JSONObject json = new JSONObject(modelJson);
        assertEquals(2, (json.getJSONArray("publicKey").length()));
        assertEquals(did, ((JSONObject) (json.getJSONArray("publicKey").get(0))).getString("id"));

        assertEquals(3, (json.getJSONArray("authentication").length()));
        assertEquals("AuthType", ((JSONObject) (json.getJSONArray("authentication").get(1))).getString("type"));

        assertEquals(2, (json.getJSONArray("service").length()));
        assertEquals("test name", ((JSONObject) (json.getJSONArray("service").get(0))).getJSONObject("metadata").getJSONObject("base").getString("name"));
        assertEquals("http://service.net", ((JSONObject) (json.getJSONArray("service").get(0))).getJSONObject("metadata").getJSONObject("base").getJSONArray("contentUrls").get(0));

    }


}