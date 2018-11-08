package com.oceanprotocol.squid.manager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.oceanprotocol.keeper.contracts.DIDRegistry;
import com.oceanprotocol.squid.dto.KeeperDto;
import com.oceanprotocol.squid.dto.AquariusDto;
import com.oceanprotocol.squid.helpers.EncodingHelper;
import com.oceanprotocol.squid.models.DDO;
import com.oceanprotocol.squid.models.DID;
import com.oceanprotocol.squid.models.asset.Asset;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OceanManagerIT {

    private static final Logger log = LogManager.getLogger(OceanManagerIT.class);

    private static final String DDO_JSON_SAMPLE = "src/test/resources/examples/ddo-example.json";
    private static String DDO_JSON_CONTENT;
    private static DDO ddoBase;

    private static OceanController manager;
    private static KeeperDto keeper;
    private static AquariusDto aquarius;
    private static DIDRegistry didRegistry;

    private static final Config config = ConfigFactory.load();

    @BeforeClass
    public static void setUp() throws Exception {
        log.debug("Setting Up DTO's");

        keeper= ManagerHelper.getKeeper(config);
        aquarius= ManagerHelper.getAquarius(config);
        manager= OceanController.getInstance(keeper, aquarius);

        didRegistry= ManagerHelper.deployDIDRegistryContract(keeper);
        manager.setDidRegistryContract(didRegistry);

        DDO_JSON_CONTENT = new String(Files.readAllBytes(Paths.get(DDO_JSON_SAMPLE)));
        ddoBase = DDO.fromJSON(new TypeReference<DDO>() {}, DDO_JSON_CONTENT);
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
    public void searchAssets() {
    }

    @Test
    public void searchOrders() {
    }

    @Test
    public void register() {
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
    public void resolveDDO() {
    }

    @Test
    public void getOrder() {
    }
}