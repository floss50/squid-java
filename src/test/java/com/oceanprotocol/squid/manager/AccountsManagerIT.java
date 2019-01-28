package com.oceanprotocol.squid.manager;

import com.oceanprotocol.keeper.contracts.OceanMarket;
import com.oceanprotocol.keeper.contracts.OceanToken;
import com.oceanprotocol.squid.external.AquariusService;
import com.oceanprotocol.squid.external.KeeperService;
import com.oceanprotocol.squid.exceptions.EthereumException;
import com.oceanprotocol.squid.models.Account;
import com.oceanprotocol.squid.models.Balance;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.web3j.protocol.admin.Admin;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AccountsManagerIT {

    private static final Logger log = LogManager.getLogger(AccountsManagerIT.class);

    private static AccountsManager manager;
    private static KeeperService keeper;
    private static AquariusService aquarius;

    private static OceanToken oceanToken;
    private static OceanMarket oceanMarket;

    private static final Config config = ConfigFactory.load();
    private static String TEST_ADDRESS;

    @BeforeClass
    public static void setUp() throws Exception {
        log.debug("Setting Up DTO's");

        keeper= ManagerHelper.getKeeper(config, ManagerHelper.VmClient.parity);
        aquarius= ManagerHelper.getAquarius(config);
        manager= AccountsManager.getInstance(keeper, aquarius);

        // Deploying OceanToken Smart Contract
        oceanToken= ManagerHelper.deployOceanTokenContract(keeper);
        oceanMarket= ManagerHelper.deployOceanMarketContract(keeper, oceanToken.getContractAddress());

        manager.setTokenContract(oceanToken);
        manager.setOceanMarketContract(oceanMarket);

        TEST_ADDRESS= config.getString("account.parity.address");
    }

    @Test
    public void getInstance() {
        // Checking if web3j driver included in KeeperService implements the Web3j interface
        assertTrue(
                manager.getKeeperService().getWeb3().getClass().getInterfaces()[0].isAssignableFrom(Admin.class));
        assertTrue(
                manager.getAquariusService().getClass().isAssignableFrom(AquariusService.class));
    }

    @Test
    public void getAccounts() throws IOException, EthereumException {
        List<Account> accounts= manager.getAccounts();
        assertTrue(accounts.size()>0);
    }

    @Test
    public void getAccountsBalance() throws EthereumException {
        manager.requestTokens(BigInteger.valueOf(100));

        Balance balance= manager.getAccountBalance(TEST_ADDRESS);

        log.debug("Balance is " + balance.toString());
        log.debug("Eth balance is " + balance.getEth().toString());

        assertEquals(1, balance.getEth().compareTo(BigInteger.ZERO));
        assertEquals(0, balance.getOcn().compareTo(BigInteger.valueOf(100)));

    }


}