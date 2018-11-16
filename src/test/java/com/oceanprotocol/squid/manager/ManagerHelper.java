package com.oceanprotocol.squid.manager;

import com.oceanprotocol.keeper.contracts.*;
import com.oceanprotocol.secretstore.core.EvmDto;
import com.oceanprotocol.secretstore.core.SecretStoreDto;
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

    protected enum VmClient { ganache, parity}

    protected static KeeperDto getKeeper(Config config) throws IOException, CipherException {
        return getKeeper(config, VmClient.ganache);
    }

    protected static KeeperDto getKeeper(Config config, VmClient client) throws IOException, CipherException {
        KeeperDto keeper= KeeperDto.getInstance(
                config.getString("keeper.url"),
                config.getString("account." + client.toString() + ".address"),
                config.getString("account." + client.toString() + ".password"),
                config.getString("account." + client.toString() + ".file")
        );

        keeper.setGasLimit(BigInteger.valueOf(config.getLong("keeper.gasLimit")))
              .setGasPrice(BigInteger.valueOf(config.getLong("keeper.gasPrice")));

        return keeper;
    }

    protected static AquariusDto getAquarius(Config config) {
        return AquariusDto.getInstance(config.getString("aquarius.url"));
    }

    protected static SecretStoreDto getSecretStoreDto(Config config) {
        return SecretStoreDto.builder(config.getString("secretstore.url"));
    }

    protected static EvmDto getEvmDto(Config config) {
        return getEvmDto(config, VmClient.ganache);
    }

    protected static EvmDto getEvmDto(Config config, VmClient client) {
        return EvmDto.builder(
                config.getString("keeper.url"),
                config.getString("account." + client.toString() + ".address"),
                config.getString("account." + client.toString() + ".password")
        );
    }

    protected static SecretStoreController getSecretStoreController(Config config, VmClient client) {
        return SecretStoreController.getInstance(getSecretStoreDto(config),getEvmDto(config, client));
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

    protected static ServiceAgreement deployServiceAgreementContract(KeeperDto keeper) throws Exception, IOException, CipherException {
        return ServiceAgreement.deploy(
                keeper.getWeb3(),
                keeper.getCredentials(),
                keeper.getContractGasProvider()).send();
    }


    protected static PaymentConditions deployPaymentConditionsContract(KeeperDto keeper, String saAddress, String tokenAddress) throws Exception, IOException, CipherException {
        return PaymentConditions.deploy(
                keeper.getWeb3(),
                keeper.getCredentials(),
                keeper.getContractGasProvider(),
                saAddress,
                tokenAddress
        ).send();
    }


    protected static AccessConditions deployAccessConditionsContract(KeeperDto keeper, String saAddress) throws Exception, IOException, CipherException {
        return AccessConditions.deploy(
                keeper.getWeb3(),
                keeper.getCredentials(),
                keeper.getContractGasProvider(),
                saAddress
        ).send();
    }

}
