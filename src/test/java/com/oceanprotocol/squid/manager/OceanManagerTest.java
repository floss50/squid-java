package com.oceanprotocol.squid.manager;

import com.oceanprotocol.squid.dto.AquariusDto;
import com.oceanprotocol.squid.dto.KeeperDto;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;

import static org.mockito.Mockito.mock;

public class OceanManagerTest {

    private static final Logger log = LogManager.getLogger(OceanManagerTest.class);

    private static OceanController manager;
    private static KeeperDto keeper;
    private static AquariusDto aquarius;
    private static final Config config = ConfigFactory.load();

    @BeforeClass
    public static void setUp() throws Exception {
        log.debug("Setting Up DTO's");

        aquarius= ManagerHelper.getAquarius(config);
    }


}