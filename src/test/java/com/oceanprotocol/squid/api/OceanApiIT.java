package com.oceanprotocol.squid.api;

import com.oceanprotocol.squid.api.config.OceanConfig;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class OceanApiIT {

    private static final Logger log = LogManager.getLogger(OceanApiIT.class);

    @Test
    public void buildAPIFromConfig() throws Exception {

        Config config = ConfigFactory.load();

        OceanAPI oceanAPI = OceanAPI.getInstance(config);
        assertEquals(config.getString("account.main.address"), oceanAPI.getMainAccount().address);

    }

    @Test
    public void buildAPIFromProperties() throws Exception {

        // Default values for KEEPER_URL, KEEPER_GAS_LIMIT, KEEPER_GAS_PRICE, AQUARIUS_URL, SECRETSTORE_URL, CONSUME_BASE_PATH
        Properties properties = new Properties();
        properties.put(OceanConfig.MAIN_ACCOUNT_ADDRESS, "0x00bd138abd70e2f00903268f3db08f2d25677c9e");
        properties.put(OceanConfig.MAIN_ACCOUNT_PASSWORD,"node0");
        properties.put(OceanConfig.MAIN_ACCOUNT_CREDENTIALS_FILE,"src/test/resources/accounts/parity/00bd138abd70e2f00903268f3db08f2d25677c9e.json.testaccount");
        properties.put(OceanConfig.DID_REGISTRY_ADDRESS, "0x01daE123504DDf108E0C65a42190516E5c5dfc07");
        properties.put(OceanConfig.SERVICE_AGREEMENT_ADDRESS, "0x21668cE2116Dbc48AC116F31678CfaaeF911F7aA");
        properties.put(OceanConfig.PAYMENT_CONDITIONS_ADDRESS, "0x38A531cc85A58adCb01D6a249E33c27CE277a2D1");
        properties.put(OceanConfig.ACCESS_CONDITIONS_ADDRESS, "0x605FAF898Fc7c2Aa847Ba0D558b5251c0F128Fd7");
        properties.put(OceanConfig.TOKEN_ADDRESS, "0xxxx");
        properties.put(OceanConfig.OCEAN_MARKET_ADDRESS, "0xxxxx");

        OceanAPI oceanAPI = OceanAPI.getInstance(properties);
        assertEquals(properties.getProperty("account.main.address"), oceanAPI.getMainAccount().address);

    }
}
