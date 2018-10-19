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
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
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

        provider= ManagerHelper.getProvider(config);
    }


}