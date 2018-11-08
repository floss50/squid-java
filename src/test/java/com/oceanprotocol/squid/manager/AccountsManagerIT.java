package com.oceanprotocol.squid.manager;

import com.oceanprotocol.keeper.contracts.OceanMarket;
import com.oceanprotocol.keeper.contracts.OceanRegistry;
import com.oceanprotocol.keeper.contracts.OceanToken;
import com.oceanprotocol.keeper.contracts.PLCRVoting;
import com.oceanprotocol.squid.dto.AquariusDto;
import com.oceanprotocol.squid.dto.KeeperDto;
import com.oceanprotocol.squid.models.Account;
import com.oceanprotocol.squid.models.Balance;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.web3j.crypto.CipherException;
import org.web3j.protocol.Web3j;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AccountsManagerIT {

    private static final Logger log = LogManager.getLogger(AccountsManagerIT.class);

    private static AccountsController manager;
    private static KeeperDto keeper;
    private static AquariusDto aquarius;

    private static OceanToken oceanToken;
    private static PLCRVoting plcr;
    private static OceanRegistry oceanRegistry;
    private static OceanMarket oceanMarket;

    private static final Config config = ConfigFactory.load();
    private static String TEST_ADDRESS;

    @BeforeClass
    public static void setUp() throws Exception {
        log.debug("Setting Up DTO's");

        keeper= ManagerHelper.getKeeper(config);
        aquarius= ManagerHelper.getAquarius(config);
        manager= AccountsController.getInstance(keeper, aquarius);

        // Deploying OceanToken Smart Contract
        oceanToken= ManagerHelper.deployOceanTokenContract(keeper);
        plcr= ManagerHelper.deployPLCRVotingContract(keeper, oceanToken.getContractAddress());
        oceanRegistry= ManagerHelper.deployOceanRegistryContract(keeper, oceanToken.getContractAddress(), plcr.getContractAddress());
        oceanMarket= ManagerHelper.deployOceanMarketContract(keeper, oceanToken.getContractAddress(), oceanRegistry.getContractAddress());

        manager.setTokenContract(oceanToken);
        manager.setPLCRVotingContract(plcr);
        manager.setOceanRegistryContract(oceanRegistry);
        manager.setOceanMarketContract(oceanMarket);

        TEST_ADDRESS= config.getString("account.address");
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
    public void getAccounts() throws IOException {
        List<Account> accounts= manager.getAccounts();
        assertTrue(accounts.size()>0);
    }

    @Test
    public void getAccountsBalance() throws IOException, CipherException {
        manager.requestTokens(BigInteger.valueOf(100));

        Balance balance= manager.getAccountBalance(TEST_ADDRESS);


        log.debug("Balance is " + balance.toString());
        //log.debug("Eth balance is " + Convert.fromWei(balance.getEth().toString(), Convert.Unit.ETHER).intValue());
        log.debug("Eth balance is " + balance.getEth().toString());

        assertEquals(1, balance.getEth().compareTo(BigInteger.ZERO));
        assertEquals(0, balance.getOcn().compareTo(BigInteger.valueOf(100)));

    }


}