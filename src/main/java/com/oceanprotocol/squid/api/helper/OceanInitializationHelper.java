package com.oceanprotocol.squid.api.helper;

import com.oceanprotocol.keeper.contracts.*;
import com.oceanprotocol.secretstore.core.EvmDto;
import com.oceanprotocol.secretstore.core.SecretStoreDto;
import com.oceanprotocol.squid.api.config.OceanConfig;
import com.oceanprotocol.squid.dto.AquariusDto;
import com.oceanprotocol.squid.dto.KeeperDto;
import com.oceanprotocol.squid.manager.AccountsManager;
import com.oceanprotocol.squid.manager.AssetsManager;
import com.oceanprotocol.squid.manager.OceanManager;
import com.oceanprotocol.squid.manager.SecretStoreManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.web3j.crypto.CipherException;

import java.io.IOException;

public class OceanInitializationHelper {

    private static final Logger log = LogManager.getLogger(OceanInitializationHelper.class);
    private OceanConfig oceanConfig;

    public OceanInitializationHelper(OceanConfig oceanConfig) {
        this.oceanConfig = oceanConfig;
    }

    public KeeperDto getKeeper() throws IOException, CipherException {

        KeeperDto keeper= KeeperDto.getInstance(
                oceanConfig.getKeeperUrl(),
                oceanConfig.getMainAccountAddress(),
                oceanConfig.getMainAccountPassword(),
                oceanConfig.getMainAccountCredentialsFile()
        );

        keeper.setGasLimit(oceanConfig.getKeeperGasLimit())
                .setGasPrice(oceanConfig.getKeeperGasPrice());

        return keeper;
    }

    public AquariusDto getAquarius() {
        return AquariusDto.getInstance(oceanConfig.getAquariusUrl());
    }

    public SecretStoreDto getSecretStoreDto() {
        return SecretStoreDto.builder(oceanConfig.getSecretStoreUrl());
    }

    public EvmDto getEvmDto() {
        return EvmDto.builder(
                oceanConfig.getKeeperUrl(),
                oceanConfig.getMainAccountAddress(),
                oceanConfig.getMainAccountPassword()
        );
    }

    public SecretStoreManager getSecretStoreManager(SecretStoreDto secretStoreDto, EvmDto evmDto) {
        return SecretStoreManager.getInstance(secretStoreDto, evmDto);
    }

    public OceanManager getOceanManager(KeeperDto keeperDto, AquariusDto aquariusDto) throws IOException, CipherException {
        return OceanManager.getInstance(keeperDto, aquariusDto);
    }

    public AccountsManager getAccountsManager(KeeperDto keeperDto, AquariusDto aquariusDto) throws IOException, CipherException {
        return AccountsManager.getInstance(keeperDto, aquariusDto);
    }

    public AssetsManager getAssetsManager(KeeperDto keeperDto, AquariusDto aquariusDto) throws IOException, CipherException {
        return AssetsManager.getInstance(keeperDto, aquariusDto);
    }

    public OceanToken loadOceanTokenContract(KeeperDto keeper) throws Exception, IOException, CipherException {
        return OceanToken.load(
                oceanConfig.getTokenAddress(),
                keeper.getWeb3(),
                keeper.getCredentials(),
                keeper.getContractGasProvider());
    }

    public OceanMarket loadOceanMarketContract(KeeperDto keeper)
            throws Exception {
        return OceanMarket.load(
                oceanConfig.getOceanMarketAddress(),
                keeper.getWeb3(),
                keeper.getCredentials(),
                keeper.getContractGasProvider()
        );
    }

    public DIDRegistry loadDIDRegistryContract(KeeperDto keeper)
            throws Exception {

        return DIDRegistry.load(
                oceanConfig.getDidRegistryAddress(),
                keeper.getWeb3(),
                keeper.getCredentials(),
                keeper.getContractGasProvider()
        );
    }

    public ServiceAgreement loadServiceAgreementContract(KeeperDto keeper) throws Exception, IOException, CipherException {
        return ServiceAgreement.load(
                oceanConfig.getServiceAgreementAddress(),
                keeper.getWeb3(),
                keeper.getCredentials(),
                keeper.getContractGasProvider());
    }

    public PaymentConditions loadPaymentConditionsContract(KeeperDto keeper) throws Exception, IOException, CipherException {
        return PaymentConditions.load(
                oceanConfig.getPaymentConditionsAddress(),
                keeper.getWeb3(),
                keeper.getCredentials(),
                keeper.getContractGasProvider()
        );
    }

    public AccessConditions loadAccessConditionsContract(KeeperDto keeper) throws Exception, IOException, CipherException {
        return AccessConditions.load(
                oceanConfig.getAccessConditionsAddress(),
                keeper.getWeb3(),
                keeper.getCredentials(),
                keeper.getContractGasProvider()
        );
    }



}
