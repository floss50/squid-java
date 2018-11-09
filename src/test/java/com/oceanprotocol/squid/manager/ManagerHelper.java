package com.oceanprotocol.squid.manager;

import com.oceanprotocol.keeper.contracts.*;
import com.oceanprotocol.squid.dto.AquariusDto;
import com.oceanprotocol.squid.dto.KeeperDto;
import com.typesafe.config.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.web3j.crypto.CipherException;

import java.io.IOException;
import java.math.BigInteger;

public abstract class ManagerHelper {

    private static final Logger log = LogManager.getLogger(ManagerHelper.class);

    protected static KeeperDto getKeeper(Config config) throws IOException, CipherException {
        KeeperDto keeper= KeeperDto.getInstance(
                config.getString("keeper.url"),
                config.getString("account.address"),
                config.getString("account.password"),
                config.getString("account.file")
        );

        keeper.setGasLimit(BigInteger.valueOf(config.getLong("keeper.gasLimit")))
              .setGasPrice(BigInteger.valueOf(config.getLong("keeper.gasPrice")));

        return keeper;
    }

    protected static AquariusDto getAquarius(Config config) {
        return AquariusDto.getInstance(config.getString("aquarius.url"));
    }

    protected static OceanToken deployOceanTokenContract(KeeperDto keeper) throws Exception, IOException, CipherException {
        return OceanToken.deploy(
                keeper.getWeb3(),
                keeper.getCredentials(),
                keeper.getContractGasProvider()).send();
    }

    protected static OceanMarket deployOceanMarketContract(KeeperDto keeper, String tokenAddress)
            throws Exception {
        return OceanMarket.deploy(
                keeper.getWeb3(),
                keeper.getCredentials(),
                keeper.getContractGasProvider(),
                tokenAddress
                ).send();
    }

    protected static DIDRegistry deployDIDRegistryContract(KeeperDto keeper)
            throws Exception {

        return DIDRegistry.deploy(
                keeper.getWeb3(),
                keeper.getCredentials(),
                keeper.getContractGasProvider()
        ).send();
    }

}
