package com.oceanprotocol.api.squid.manager;

import com.oceanprotocol.api.squid.dto.KeeperDto;
import com.oceanprotocol.api.squid.dto.ProviderDto;
import com.oceanprotocol.keeper.contracts.OceanMarket;
import com.oceanprotocol.keeper.contracts.OceanRegistry;
import com.oceanprotocol.keeper.contracts.OceanToken;
import com.oceanprotocol.keeper.contracts.PLCRVoting;
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

    protected static ProviderDto getProvider(Config config) {
        return ProviderDto.getInstance(config.getString("provider.url"));
    }

    protected static OceanToken deployOceanTokenContract(KeeperDto keeper) throws Exception, IOException, CipherException {
        return OceanToken.deploy(
                keeper.getWeb3(),
                keeper.getCredentials(),
                keeper.getContractGasProvider()).send();
    }

    protected static PLCRVoting deployPLCRVotingContract(KeeperDto keeper, String tokenAddress)
            throws Exception {

        return PLCRVoting.deploy(
                keeper.getWeb3(),
                keeper.getCredentials(),
                keeper.getContractGasProvider(),
                tokenAddress
        ).send();
    }

    protected static OceanRegistry deployOceanRegistryContract(KeeperDto keeper, String tokenAddress, String plcrAddress)
            throws Exception {

        return OceanRegistry.deploy(
                keeper.getWeb3(),
                keeper.getCredentials(),
                keeper.getContractGasProvider(),
                tokenAddress,
                plcrAddress
                ).send();
    }


    protected static OceanMarket deployOceanMarketContract(KeeperDto keeper, String tokenAddress, String registryAddress)
            throws Exception {
        return OceanMarket.deploy(
                keeper.getWeb3(),
                keeper.getCredentials(),
                keeper.getContractGasProvider(),
                tokenAddress,
                registryAddress
                ).send();
    }
}
