package com.oceanprotocol.squid.api;

import com.oceanprotocol.squid.models.Account;
import com.oceanprotocol.squid.models.Balance;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AccountsApiIT {

    private static final Logger log = LogManager.getLogger(AccountsApiIT.class);

    private static OceanAPI oceanAPI;

    @BeforeClass
    public static void setUp() throws Exception {


        Config config = ConfigFactory.load();
        oceanAPI = OceanAPI.getInstance(config);

        assertNotNull(oceanAPI.getAccountsAPI());
        assertNotNull(oceanAPI.getMainAccount());

    }

    @Test
    public void list() throws Exception {

        List<Account> accounts = oceanAPI.getAccountsAPI().list();
        assertNotNull(accounts);
        assertTrue(!accounts.isEmpty());
    }

    @Test
    public void balance() throws Exception {

        Balance balance = oceanAPI.getAccountsAPI().balance(oceanAPI.getMainAccount());
        assertNotNull(balance);
        assertTrue(balance.getEth().compareTo(BigInteger.ZERO)>0);
        assertTrue(balance.getOcn().intValue()>0);
    }

    @Test
    public void requestTokens() throws Exception {

        BigInteger tokens = BigInteger.valueOf(1);

        Balance balanceBefore = oceanAPI.getAccountsAPI().balance(oceanAPI.getMainAccount());
        TransactionReceipt receipt = oceanAPI.getAccountsAPI().requestTokens(tokens);

        assertTrue(receipt.isStatusOK());

        Balance balanceAfter = oceanAPI.getAccountsAPI().balance(oceanAPI.getMainAccount());
        assertEquals(balanceBefore.getOcn().add(BigInteger.valueOf(1)), balanceAfter.getOcn());
    }

}
