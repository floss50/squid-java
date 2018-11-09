package com.oceanprotocol.squid.manager;

import com.oceanprotocol.keeper.contracts.*;
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

    protected OceanToken tokenContract;
    protected OceanMarket oceanMarket;
    protected DIDRegistry didRegistry;


    public BaseController(KeeperDto keeperDto, AquariusDto aquariusDto) throws IOException, CipherException {
        this.keeperDto= keeperDto;
        this.aquariusDto = aquariusDto;
    }

    public KeeperDto getKeeperDto() {
        return keeperDto;
    }

    public BaseController setKeeperDto(KeeperDto keeperDto) {
        this.keeperDto = keeperDto;
        return this;
    }

    public AquariusDto getAquariusDto() {
        return aquariusDto;
    }

    public BaseController setAquariusDto(AquariusDto aquariusDto) {
        this.aquariusDto = aquariusDto;
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
