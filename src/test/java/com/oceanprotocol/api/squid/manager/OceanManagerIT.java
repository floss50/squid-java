package com.oceanprotocol.api.squid.manager;

import com.oceanprotocol.api.squid.dto.KeeperDto;
import com.oceanprotocol.api.squid.dto.ProviderDto;
import com.oceanprotocol.api.squid.models.Account;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.web3j.protocol.Web3j;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class OceanManagerIT {

    private static final Logger log = LogManager.getLogger(OceanManagerIT.class);

    private static OceanManager manager;
    private static KeeperDto keeper;
    private static ProviderDto provider;
    private static final Config config = ConfigFactory.load();

    @BeforeClass
    public static void setUp() throws Exception {
        log.debug("Setting Up DTO's");

        keeper= ManagerHelper.getKeeper(config);
        provider= ManagerHelper.getProvider(config);
        manager= OceanManager.getInstance(keeper, provider);
    }

    @Test
    public void getInstance() {
        // Checking if web3j driver included in KeeperDto implements the Web3j interface
        assertTrue(
                manager.getKeeperDto().getWeb3().getClass().getInterfaces()[0].isAssignableFrom(Web3j.class));
        assertTrue(
                manager.getProviderDto().getClass().isAssignableFrom(ProviderDto.class));
    }

    @Test
    public void getAccounts() throws IOException {
        List<Account> accounts= manager.getAccounts();
        assertTrue(accounts.size()>0);
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
    public void generateDID() {
    }

    @Test
    public void resolveDDO() {
    }

    @Test
    public void getOrder() {
    }
}