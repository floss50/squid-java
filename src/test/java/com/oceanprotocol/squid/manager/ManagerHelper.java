package com.oceanprotocol.squid.manager;

import com.oceanprotocol.keeper.contracts.*;
import com.oceanprotocol.secretstore.core.EvmDto;
import com.oceanprotocol.secretstore.core.SecretStoreDto;
import com.oceanprotocol.squid.dto.AquariusDto;
import com.oceanprotocol.squid.dto.KeeperDto;
import com.oceanprotocol.squid.helpers.EncodingHelper;
import com.oceanprotocol.squid.helpers.EventsHelper;
import com.typesafe.config.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.crypto.CipherException;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthLog;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

public abstract class ManagerHelper {

    private static final Logger log = LogManager.getLogger(ManagerHelper.class);

    public enum VmClient { ganache, parity}

    public static KeeperDto getKeeper(Config config) throws IOException, CipherException {
        return getKeeper(config, VmClient.ganache);
    }

    public static KeeperDto getKeeper(Config config, VmClient client) throws IOException, CipherException {
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


    public static KeeperDto getKeeper(Config config, VmClient client, String nAddress) throws IOException, CipherException {
        KeeperDto keeper= KeeperDto.getInstance(
                config.getString("keeper.url"),
                config.getString("account." + client.toString() + ".address" + nAddress),
                config.getString("account." + client.toString() + ".password" + nAddress),
                config.getString("account." + client.toString() + ".file" + nAddress)
        );

        keeper.setGasLimit(BigInteger.valueOf(config.getLong("keeper.gasLimit")))
                .setGasPrice(BigInteger.valueOf(config.getLong("keeper.gasPrice")));

        return keeper;
    }

    public static AquariusDto getAquarius(Config config) {
        return AquariusDto.getInstance(config.getString("aquarius.url"));
    }

    public static SecretStoreDto getSecretStoreDto(Config config) {
        return SecretStoreDto.builder(config.getString("secretstore.url"));
    }

    public static EvmDto getEvmDto(Config config) {
        return getEvmDto(config, VmClient.ganache);
    }

    public static EvmDto getEvmDto(Config config, VmClient client) {
        return EvmDto.builder(
                config.getString("keeper.url"),
                config.getString("account." + client.toString() + ".address"),
                config.getString("account." + client.toString() + ".password")
        );
    }

    public static SecretStoreController getSecretStoreController(Config config, VmClient client) {
        return SecretStoreController.getInstance(getSecretStoreDto(config),getEvmDto(config, client));
    }


    public static OceanToken deployOceanTokenContract(KeeperDto keeper) throws Exception, IOException, CipherException {
        return OceanToken.deploy(
                keeper.getWeb3(),
                keeper.getCredentials(),
                keeper.getContractGasProvider()).send();
    }

    public static OceanToken loadOceanTokenContract(KeeperDto keeper, String address) throws Exception, IOException, CipherException {
        return OceanToken.load(
                address,
                keeper.getWeb3(),
                keeper.getCredentials(),
                keeper.getContractGasProvider());
    }

    public static OceanMarket deployOceanMarketContract(KeeperDto keeper, String tokenAddress)
            throws Exception {
        return OceanMarket.deploy(
                keeper.getWeb3(),
                keeper.getCredentials(),
                keeper.getContractGasProvider(),
                tokenAddress
                ).send();
    }

    public static OceanMarket loadOceanMarketContract(KeeperDto keeper, String address)
            throws Exception {
        return OceanMarket.load(
                address,
                keeper.getWeb3(),
                keeper.getCredentials(),
                keeper.getContractGasProvider()
        );
    }

    public static DIDRegistry deployDIDRegistryContract(KeeperDto keeper)
            throws Exception {

        return DIDRegistry.deploy(
                keeper.getWeb3(),
                keeper.getCredentials(),
                keeper.getContractGasProvider()
        ).send();
    }

    public static DIDRegistry loadDIDRegistryContract(KeeperDto keeper, String address)
            throws Exception {

        return DIDRegistry.load(
                address,
                keeper.getWeb3(),
                keeper.getCredentials(),
                keeper.getContractGasProvider()
        );
    }

    public static ServiceAgreement deployServiceAgreementContract(KeeperDto keeper) throws Exception, IOException, CipherException {
        return ServiceAgreement.deploy(
                keeper.getWeb3(),
                keeper.getCredentials(),
                keeper.getContractGasProvider()).send();
    }

    public static ServiceAgreement loadServiceAgreementContract(KeeperDto keeper, String address) throws Exception, IOException, CipherException {
        return ServiceAgreement.load(
                address,
                keeper.getWeb3(),
                keeper.getCredentials(),
                keeper.getContractGasProvider());
    }


    public static PaymentConditions deployPaymentConditionsContract(KeeperDto keeper, String saAddress, String tokenAddress) throws Exception, IOException, CipherException {
        return PaymentConditions.deploy(
                keeper.getWeb3(),
                keeper.getCredentials(),
                keeper.getContractGasProvider(),
                saAddress,
                tokenAddress
        ).send();
    }

    public static PaymentConditions loadPaymentConditionsContract(KeeperDto keeper, String address) throws Exception, IOException, CipherException {
        return PaymentConditions.load(
                address,
                keeper.getWeb3(),
                keeper.getCredentials(),
                keeper.getContractGasProvider()
        );
    }


    public static AccessConditions deployAccessConditionsContract(KeeperDto keeper, String saAddress) throws Exception, IOException, CipherException {
        return AccessConditions.deploy(
                keeper.getWeb3(),
                keeper.getCredentials(),
                keeper.getContractGasProvider(),
                saAddress
        ).send();
    }

    public static AccessConditions loadAccessConditionsContract(KeeperDto keeper, String address) throws Exception, IOException, CipherException {
        return AccessConditions.load(address,
                keeper.getWeb3(),
                keeper.getCredentials(),
                keeper.getContractGasProvider()
                );
    }


}
