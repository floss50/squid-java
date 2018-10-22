package com.oceanprotocol.api.squid.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oceanprotocol.api.squid.models.asset.AssetMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class DDOTest {

    static final Logger log= LogManager.getLogger(DDOTest.class);

    // DDO example downloaded from w3c site
    private static final String DDO_JSON_SAMPLE= "src/test/resources/examples/ddo-example.json";
    private static String DDO_JSON_CONTENT;

    private static ObjectMapper objectMapper;

    @BeforeClass
    public static void setUp() throws Exception {
        DDO_JSON_CONTENT= new String(Files.readAllBytes(Paths.get(DDO_JSON_SAMPLE)));
        objectMapper= new ObjectMapper();
    }

    @Test
    public void jsonToModel() throws Exception {

        DDO ddo= DDO.fromJSON(new TypeReference<DDO>() {}, DDO_JSON_CONTENT);

        assertEquals("did:op:123456789abcdefghi", ddo.id.toString());
        assertEquals(3, ddo.publicKeys.size());
        assertTrue(ddo.publicKeys.get(0).id.startsWith("did:op:123456789abcdefghi"));

        assertEquals(2, ddo.authentication.size());
        assertTrue(ddo.authentication.get(0).publicKey.startsWith("did:op:123456789abcdefghi"));

        assertEquals(9, ddo.services.size());
        assertTrue(ddo.services.get(3).serviceEndpoint.startsWith("http"));

        AssetMetadata metadata= (AssetMetadata) ddo.metadata;

        assertEquals("UK Weather information 2011", metadata.base.name);
        assertEquals(123, metadata.curation.numVotes);
    }

    @Test
    public void modelToJson() throws Exception {
        String did= "did:op:12345";
        DDO ddo= new DDO();

        DDO.PublicKey pk= new DDO.PublicKey();
        pk.id = did;
        pk.type= "RsaVerificationKey2018";
        pk.owner= did + "owner";

        ddo.publicKeys.add(pk);
        ddo.publicKeys.add(pk);

        DDO.Authentication auth= new DDO.Authentication();
        auth.type= "AuthType";
        auth.publicKey= "AuthPK";

        ddo.authentication.add(auth);
        ddo.authentication.add(auth);
        ddo.authentication.add(auth);

        DDO.Service service= new DDO.Service();
        service.id= "service1";
        service.serviceEndpoint= "http://disney.com";

        ddo.services.add(service);
        DDO.Service service2= new DDO.Service();
        service2.id= "service2";
        service2.serviceEndpoint= "http://disney.com";
        service2.type= "Metadata";
        ddo.services.add(service2);

        AssetMetadata metadata= new AssetMetadata();
        AssetMetadata.Base base= new AssetMetadata.Base();
        base.name= "test name";
        base.contentUrls=  new ArrayList<String>() {{
            add("http://service.net");
        }};

        metadata.base= base;

        ddo.metadata= metadata;

        String modelJson= ddo.toJson();
        log.debug(modelJson);

        JSONObject json= new JSONObject(modelJson);
        assertEquals(2, ( json.getJSONArray("publicKey").length()));
        assertEquals(did, ((JSONObject) (json.getJSONArray("publicKey").get(0))).getString("id"));

        assertEquals(3, ( json.getJSONArray("authentication").length()));
        assertEquals("AuthType", ((JSONObject) (json.getJSONArray("authentication").get(1))).getString("type"));

        assertEquals(2, ( json.getJSONArray("service").length()));
        assertEquals("test name", ((JSONObject) (json.getJSONArray("service").get(1))).getJSONObject("metadata").getJSONObject("base").getString("name"));
        assertEquals("http://service.net", ((JSONObject) (json.getJSONArray("service").get(1))).getJSONObject("metadata").getJSONObject("base").getJSONArray("contentUrls").get(0));

    }
}