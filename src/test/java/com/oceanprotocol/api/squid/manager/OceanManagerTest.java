package com.oceanprotocol.api.squid.manager;

import com.oceanprotocol.api.squid.dto.KeeperDto;
import com.oceanprotocol.api.squid.dto.ProviderDto;
import com.oceanprotocol.api.squid.models.Account;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthAccounts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OceanManagerTest {

    private static final Logger log = LogManager.getLogger(OceanManagerTest.class);

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
    public void getAccounts() throws IOException {
        List<String> expectedAccounts= new ArrayList<>();
        expectedAccounts.add("0x123");
        expectedAccounts.add("0x456");
        expectedAccounts.add("0x789");

        KeeperDto _keeper= mock(KeeperDto.class);
        Web3j _web3j= mock(Web3j.class);

        Request<?, EthAccounts> _request= (Request<?, EthAccounts>) mock(Request.class);
        EthAccounts _response= mock(EthAccounts.class);

        when(_response.getAccounts()).thenReturn(expectedAccounts);
        when(_request.send()).thenReturn(_response);
        Mockito.doReturn(_request).when(_web3j).ethAccounts();
        when(_keeper.getWeb3()).thenReturn(_web3j);

        OceanManager fakeManager= OceanManager.getInstance(_keeper, provider);

        List<Account> accounts= fakeManager.getAccounts();

        assertTrue(accounts.size() == expectedAccounts.size());
        assertTrue(accounts.get(0).name.startsWith("0x"));
    }
}