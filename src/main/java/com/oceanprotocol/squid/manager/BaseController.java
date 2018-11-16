package com.oceanprotocol.squid.manager;

import com.oceanprotocol.keeper.contracts.*;
import com.oceanprotocol.secretstore.core.EvmDto;
import com.oceanprotocol.secretstore.core.SecretStoreDto;
import com.oceanprotocol.squid.dto.AquariusDto;
import com.oceanprotocol.squid.dto.KeeperDto;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.Contract;
import org.web3j.tx.gas.ContractGasProvider;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class BaseController {

    private KeeperDto keeperDto;
    private AquariusDto aquariusDto;
    private SecretStoreDto secretStoreDto;
    private EvmDto evmDto;
    private SecretStoreController secretStoreController;

    protected OceanToken tokenContract;
    protected OceanMarket oceanMarket;
    protected DIDRegistry didRegistry;
    protected ServiceAgreement serviceAgreement;
    protected PaymentConditions paymentConditions;
    protected AccessConditions accessConditions;


    /**
     * Constructor
     * @param keeperDto KeeperDto
     * @param aquariusDto AquariusDto
     * @throws IOException
     * @throws CipherException
     */
    public BaseController(KeeperDto keeperDto, AquariusDto aquariusDto) throws IOException, CipherException {
        this.keeperDto= keeperDto;
        this.aquariusDto = aquariusDto;
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
    public BaseController setKeeperDto(KeeperDto keeperDto) {
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
    public BaseController setAquariusDto(AquariusDto aquariusDto) {
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
    public BaseController setSecretStoreDto(SecretStoreDto secretStoreDto) {
        this.secretStoreDto = secretStoreDto;
        return this;
    }

    /**
     * Get the SecretStoreController
     * @return SecretStoreDto
     */
    public SecretStoreController getSecretStoreController() {
        return secretStoreController;
    }

    /**
     * Set the SecretStoreController
     * @param secretStoreController SecretStoreDto
     * @return this
     */
    public BaseController setSecretStoreController(SecretStoreController secretStoreController) {
        this.secretStoreController = secretStoreController;
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
    public BaseController setEvmDto(EvmDto evmDto) {
        this.evmDto = evmDto;
        return this;
    }

    /**
     * Initialize the OceanToken object using the address given as parameter to point to the deployed contract
     * @param address OceanToken contract address
     * @return AccountsController instance
     * @throws IOException IOException
     * @throws CipherException CipherException
     */
    public BaseController setTokenContract(String address) throws IOException, CipherException {
        this.tokenContract= OceanToken.load(address,
                getKeeperDto().getWeb3(),
                getKeeperDto().getCredentials(),
                getKeeperDto().getContractGasProvider());
        return this;
    }

    /**
     * It sets the OceanToken stub instance
     * @param contract OceanToken instance
     * @return BaseController instance
     */
    public BaseController setTokenContract(OceanToken contract)    {
        this.tokenContract= contract;
        return this;
    }

    /**
     * It sets the OceanMarket stub instance
     * @param contract OceanMarket instance
     * @return BaseController instance
     */
    public BaseController setOceanMarketContract(OceanMarket contract)    {
        this.oceanMarket= contract;
        return this;
    }

    /**
     * Initialize the DIDRegistry object using the address given as parameter to point to the deployed contract
     * @param address DIDRegistry contract address
     * @return BaseController instance
     * @throws IOException IOException
     * @throws CipherException CipherException
     */
    public BaseController setDidRegistryContract(String address) throws IOException, CipherException {
        this.didRegistry= DIDRegistry.load(address,
                getKeeperDto().getWeb3(),
                getKeeperDto().getCredentials(),
                getKeeperDto().getContractGasProvider());
        return this;
    }

    /**
     * It sets the ServiceAgreement stub instance
     * @param contract ServiceAgreement instance
     * @return BaseController instance
     */
    public BaseController setServiceAgreementContract(ServiceAgreement contract)    {
        this.serviceAgreement= contract;
        return this;
    }

    /**
     * It sets the PaymentConditions stub instance
     * @param contract PaymentConditions instance
     * @return BaseController instance
     */
    public BaseController setPaymentConditionsContract(PaymentConditions contract)    {
        this.paymentConditions= contract;
        return this;
    }

    /**
     * It sets the AccessConditions stub instance
     * @param contract AccessConditions instance
     * @return BaseController instance
     */
    public BaseController setAccessConditionsContract(AccessConditions contract)    {
        this.accessConditions= contract;
        return this;
    }

    /**
     * It sets the DIDRegistry stub instance
     * @param contract DIDRegistry instance
     * @return BaseController instance
     */
    public BaseController setDidRegistryContract(DIDRegistry contract)    {
        this.didRegistry= contract;
        return this;
    }


    /**
     * Generic Contract Stub initialization method using reflection
     * @param address Contract address
     * @param classz Contract.class
     * @return contract instance instance
     * @throws IOException IOException
     * @throws CipherException CipherException
     */
    public Contract loadGenericContract(String address, Class classz)
            throws NoSuchMethodException, IOException, CipherException, InvocationTargetException, IllegalAccessException {

        Method method= classz.getMethod("load", String.class, Web3j.class, Credentials.class, ContractGasProvider.class);
        return (Contract) method.invoke(
                null,
                address,
                getKeeperDto().getWeb3(),
                getKeeperDto().getCredentials(),
                getKeeperDto().getContractGasProvider());

    }




    @Override
    public String toString() {
        return "BaseController{" +
                "keeperDto=" + keeperDto +
                ", aquariusDto=" + aquariusDto +
                '}';
    }
}
