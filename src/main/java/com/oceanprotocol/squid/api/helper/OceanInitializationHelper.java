package com.oceanprotocol.squid.api.helper;

import com.oceanprotocol.keeper.contracts.*;
import com.oceanprotocol.secretstore.core.EvmDto;
import com.oceanprotocol.secretstore.core.SecretStoreDto;
import com.oceanprotocol.squid.api.config.OceanConfig;
import com.oceanprotocol.squid.external.AquariusService;
import com.oceanprotocol.squid.external.KeeperService;
import com.oceanprotocol.squid.manager.AccountsManager;
import com.oceanprotocol.squid.manager.AssetsManager;
import com.oceanprotocol.squid.manager.OceanManager;
import com.oceanprotocol.squid.manager.SecretStoreManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.web3j.crypto.CipherException;

import java.io.IOException;

/**
 * Helper to initialize all the managers, services and contracts needed for the API
 */
public class OceanInitializationHelper {

    private static final Logger log = LogManager.getLogger(OceanInitializationHelper.class);
    private OceanConfig oceanConfig;

    /**
     * Constructor
     * @param oceanConfig
     */
    public OceanInitializationHelper(OceanConfig oceanConfig) {
        this.oceanConfig = oceanConfig;
    }

    /**
     * Initialize an instance of KeeperService
     * @return an initialized KeeperService object
     * @throws IOException
     * @throws CipherException
     */
    public KeeperService getKeeper() throws IOException, CipherException {

        KeeperService keeper= KeeperService.getInstance(
                oceanConfig.getKeeperUrl(),
                oceanConfig.getMainAccountAddress(),
                oceanConfig.getMainAccountPassword(),
                oceanConfig.getMainAccountCredentialsFile()
        );

        keeper.setGasLimit(oceanConfig.getKeeperGasLimit())
                .setGasPrice(oceanConfig.getKeeperGasPrice());

        return keeper;
    }

    /**
     * Initialize an instance of AquariusService
     * @return an initialized AquariusService object
     */
    public AquariusService getAquarius() {
        return AquariusService.getInstance(oceanConfig.getAquariusUrl());
    }

    /**
     * Initialize an instance of SecretStoreDto
     * @return an initializedSecretStoreDto object
     */
    public SecretStoreDto getSecretStoreDto() {
        return SecretStoreDto.builder(oceanConfig.getSecretStoreUrl());
    }

    /**
     *  Initialize an instance of EvmDto
     * @return an initialized EvmDto object
     */
    public EvmDto getEvmDto() {
        return EvmDto.builder(
                oceanConfig.getKeeperUrl(),
                oceanConfig.getMainAccountAddress(),
                oceanConfig.getMainAccountPassword()
        );
    }

    /**
     *  Initialize an instance of SecretStoreManager
     * @param secretStoreDto
     * @param evmDto
     * @return  an initialized SecretStoreManager object
     */
    public SecretStoreManager getSecretStoreManager(SecretStoreDto secretStoreDto, EvmDto evmDto) {
        return SecretStoreManager.getInstance(secretStoreDto, evmDto);
    }

    /**
     * Initialize an instance of OceanManager
     * @param keeperService
     * @param aquariusService
     * @return an initialized OceanManager object
     * @throws IOException
     * @throws CipherException
     */
    public OceanManager getOceanManager(KeeperService keeperService, AquariusService aquariusService) throws IOException, CipherException {
        return OceanManager.getInstance(keeperService, aquariusService);
    }

    /**
     * Initialize an instance of AccountsManager
     * @param keeperService
     * @param aquariusService
     * @return an initialized AccountsManager object
     * @throws IOException
     * @throws CipherException
     */
    public AccountsManager getAccountsManager(KeeperService keeperService, AquariusService aquariusService) throws IOException, CipherException {
        return AccountsManager.getInstance(keeperService, aquariusService);
    }

    /**
     * Initialize an instance of AssetsManager
     * @param keeperService
     * @param aquariusService
     * @return an initialized AssetsManager object
     * @throws IOException
     * @throws CipherException
     */
    public AssetsManager getAssetsManager(KeeperService keeperService, AquariusService aquariusService) throws IOException, CipherException {
        return AssetsManager.getInstance(keeperService, aquariusService);
    }

    /**
     * Loads the OceanToken contract from Keeper
     * @param keeper
     * @return an instance of OceanToken contract deployed in keeper
     * @throws IOException
     * @throws CipherException
     */
    public OceanToken loadOceanTokenContract(KeeperService keeper) throws IOException, CipherException {
        return OceanToken.load(
                oceanConfig.getTokenAddress(),
                keeper.getWeb3(),
                keeper.getCredentials(),
                keeper.getContractGasProvider());
    }


    /**
     * Loads the OceanMarket contract from Keeper
     * @param keeper
     * @return an instance of OceanMarket contract deployed in keeper
     * @throws IOException
     * @throws CipherException
     */
    public OceanMarket loadOceanMarketContract(KeeperService keeper) throws IOException, CipherException {
        return OceanMarket.load(
                oceanConfig.getOceanMarketAddress(),
                keeper.getWeb3(),
                keeper.getCredentials(),
                keeper.getContractGasProvider()
        );
    }

    /**
     *  Loads the DIDRegistry contract from Keeper
     * @param keeper
     * @return an instance of DIDRegistry contract deployed in keeper
     * @throws IOException
     * @throws CipherException
     */
    public DIDRegistry loadDIDRegistryContract(KeeperService keeper) throws IOException, CipherException {

        return DIDRegistry.load(
                oceanConfig.getDidRegistryAddress(),
                keeper.getWeb3(),
                keeper.getCredentials(),
                keeper.getContractGasProvider()
        );
    }

    /**
     * Loads the ServiceAgreement contract from Keeper
     * @param keeper
     * @return an instance of ServiceAgreement contract deployed in keeper
     * @throws IOException
     * @throws CipherException
     */
    public ServiceAgreement loadServiceAgreementContract(KeeperService keeper) throws IOException, CipherException {
        return ServiceAgreement.load(
                oceanConfig.getServiceAgreementAddress(),
                keeper.getWeb3(),
                keeper.getCredentials(),
                keeper.getContractGasProvider());
    }

    /**
     * Loads the PaymentConditions contract from Keeper
     * @param keeper
     * @return an instance of PaymentConditions contract deployed in keeper
     * @throws IOException
     * @throws CipherException
     */
    public PaymentConditions loadPaymentConditionsContract(KeeperService keeper) throws  IOException, CipherException {
        return PaymentConditions.load(
                oceanConfig.getPaymentConditionsAddress(),
                keeper.getWeb3(),
                keeper.getCredentials(),
                keeper.getContractGasProvider()
        );
    }

    /**
     *  Loads the AccessConditions contract from Keeper
     * @param keeper
     * @return an instance of AccessConditions contract deployed in keeper
     * @throws IOException
     * @throws CipherException
     */
    public AccessConditions loadAccessConditionsContract(KeeperService keeper) throws IOException, CipherException {
        return AccessConditions.load(
                oceanConfig.getAccessConditionsAddress(),
                keeper.getWeb3(),
                keeper.getCredentials(),
                keeper.getContractGasProvider()
        );
    }



}
