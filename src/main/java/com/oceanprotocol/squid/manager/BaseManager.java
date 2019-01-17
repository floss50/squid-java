package com.oceanprotocol.squid.manager;

import com.oceanprotocol.keeper.contracts.*;
import com.oceanprotocol.secretstore.core.EvmDto;
import com.oceanprotocol.secretstore.core.SecretStoreDto;
import com.oceanprotocol.squid.dto.AquariusDto;
import com.oceanprotocol.squid.dto.KeeperDto;
import org.web3j.crypto.CipherException;
import java.io.IOException;

public abstract class BaseManager {

    private KeeperDto keeperDto;
    private AquariusDto aquariusDto;
    private SecretStoreDto secretStoreDto;
    private EvmDto evmDto;
    private SecretStoreManager secretStoreManager;
    protected OceanToken tokenContract;
    protected OceanMarket oceanMarket;
    protected DIDRegistry didRegistry;
    protected ServiceAgreement serviceAgreement;
    protected PaymentConditions paymentConditions;
    protected AccessConditions accessConditions;
    protected ContractAddresses contractAddresses  = new ContractAddresses();

    public static class ContractAddresses {

        private String paymentConditionsAddress;
        private String accessConditionsAddres;

        public ContractAddresses(){}

        public String getPaymentConditionsAddress() {
            return paymentConditionsAddress;
        }

        public void setPaymentConditionsAddress(String paymentConditionsAddress) {
            this.paymentConditionsAddress = paymentConditionsAddress;
        }

        public String getAccessConditionsAddres() {
            return accessConditionsAddres;
        }

        public void setAccessConditionsAddres(String accessConditionsAddres) {
            this.accessConditionsAddres = accessConditionsAddres;
        }
    }

    /**
     * Constructor
     * @param keeperDto KeeperDto
     * @param aquariusDto AquariusDto
     * @throws IOException
     * @throws CipherException
     */
    public BaseManager(KeeperDto keeperDto, AquariusDto aquariusDto) throws IOException, CipherException {
        this.keeperDto= keeperDto;
        this.aquariusDto = aquariusDto;
    }


    public ContractAddresses getContractAddresses() {
        return contractAddresses;
    }

    /**
     * Get the KeeperDto
     * @return KeeperDto
     */
    public KeeperDto getKeeperDto() {
        return keeperDto;
    }

    /**
     * Set the KeeperDto
     * @param keeperDto KeeperDto
     * @return this
     */
    public BaseManager setKeeperDto(KeeperDto keeperDto) {
        this.keeperDto = keeperDto;
        return this;
    }

    /**
     * Get the AquariusDto
     * @return AquariusDto
     */
    public AquariusDto getAquariusDto() {
        return aquariusDto;
    }

    /**
     * Set the AquariusDto
     * @param aquariusDto AquariusDto
     * @return this
     */
    public BaseManager setAquariusDto(AquariusDto aquariusDto) {
        this.aquariusDto = aquariusDto;
        return this;
    }

    /**
     * Get the SecretStoreDto
     * @return SecretStoreDto
     */
    public SecretStoreDto getSecretStoreDto() {
        return secretStoreDto;
    }

    /**
     * Set the SecretStoreDto
     * @param secretStoreDto SecretStoreDto
     * @return this
     */
    public BaseManager setSecretStoreDto(SecretStoreDto secretStoreDto) {
        this.secretStoreDto = secretStoreDto;
        return this;
    }

    /**
     * Get the SecretStoreManager
     * @return SecretStoreDto
     */
    public SecretStoreManager getSecretStoreManager() {
        return secretStoreManager;
    }

    /**
     * Set the SecretStoreManager
     * @param secretStoreManager SecretStoreDto
     * @return this
     */
    public BaseManager setSecretStoreManager(SecretStoreManager secretStoreManager) {
        this.secretStoreManager = secretStoreManager;
        return this;
    }

    /**
     * Get the EvmDto
     * @return EvmDto
     */
    public EvmDto getEvmDto() {
        return evmDto;
    }

    /**
     * Set the EvmDto necessary to stablish the encryption/decryption flow necessary by Secret Store
     * @param evmDto EvmDto
     * @return this
     */
    public BaseManager setEvmDto(EvmDto evmDto) {
        this.evmDto = evmDto;
        return this;
    }

    /**
     * Initialize the OceanToken object using the address given as parameter to point to the deployed contract
     * @param address OceanToken contract address
     * @return AccountsManager instance
     * @throws IOException IOException
     * @throws CipherException CipherException
     */
    public BaseManager setTokenContract(String address) throws IOException, CipherException {
        this.tokenContract= OceanToken.load(address,
                getKeeperDto().getWeb3(),
                getKeeperDto().getCredentials(),
                getKeeperDto().getContractGasProvider());
        return this;
    }

    /**
     * It sets the OceanToken stub instance
     * @param contract OceanToken instance
     * @return BaseManager instance
     */
    public BaseManager setTokenContract(OceanToken contract)   {
        this.tokenContract= contract;
        return this;
    }

    /**
     * Initialize the Token Contract given a contract address
     * @param address OceanToken contract address
     * @return BaseManager instance
     */
    public BaseManager initializeTokenContract(String address) throws Exception {
        this.tokenContract= OceanToken.load(address,
                getKeeperDto().getWeb3(),
                getKeeperDto().getCredentials(),
                getKeeperDto().getContractGasProvider()
                );
        return this;
    }

    /**
     * It sets the OceanMarket stub instance
     * @param contract OceanMarket instance
     * @return BaseManager instance
     */
    public BaseManager setOceanMarketContract(OceanMarket contract)    {
        this.oceanMarket= contract;
        return this;
    }


    /**
     * Initialize the OceanMarket Contract given a contract address
     * @param address OceanMarket contract address
     * @return BaseManager instance
     */
    public BaseManager initializeOceanMarketContract(String address) throws Exception {
        this.oceanMarket= OceanMarket.load(address,
                getKeeperDto().getWeb3(),
                getKeeperDto().getCredentials(),
                getKeeperDto().getContractGasProvider()
        );
        return this;
    }

    /**
     * Initialize the DIDRegistry object using the address given as parameter to point to the deployed contract
     * @param address DIDRegistry contract address
     * @return BaseManager instance
     * @throws IOException IOException
     * @throws CipherException CipherException
     */
    public BaseManager setDidRegistryContract(String address) throws IOException, CipherException {
        this.didRegistry= DIDRegistry.load(address,
                getKeeperDto().getWeb3(),
                getKeeperDto().getCredentials(),
                getKeeperDto().getContractGasProvider());
        return this;
    }

    /**
     * It sets the ServiceAgreement stub instance
     * @param contract ServiceAgreement instance
     * @return BaseManager instance
     */
    public BaseManager setServiceAgreementContract(ServiceAgreement contract)    {
        this.serviceAgreement= contract;
        return this;
    }

    /**
     * It sets the PaymentConditions stub instance
     * @param contract PaymentConditions instance
     * @return BaseManager instance
     */
    public BaseManager setPaymentConditionsContract(PaymentConditions contract)    {
        this.paymentConditions= contract;
        this.contractAddresses.setPaymentConditionsAddress(this.paymentConditions.getContractAddress());
        return this;
    }

    /**
     * It sets the AccessConditions stub instance
     * @param contract AccessConditions instance
     * @return BaseManager instance
     */
    public BaseManager setAccessConditionsContract(AccessConditions contract)    {
        this.accessConditions= contract;
        this.contractAddresses.setAccessConditionsAddres(this.accessConditions.getContractAddress());
        return this;
    }

    /**
     * It sets the DIDRegistry stub instance
     * @param contract DIDRegistry instance
     * @return BaseManager instance
     */
    public BaseManager setDidRegistryContract(DIDRegistry contract)    {
        this.didRegistry= contract;
        return this;
    }

    @Override
    public String toString() {
        return "BaseManager{" +
                "keeperDto=" + keeperDto +
                ", aquariusDto=" + aquariusDto +
                '}';
    }
}
