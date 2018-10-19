package com.oceanprotocol.api.squid.manager;

import com.oceanprotocol.api.squid.dto.KeeperDto;
import com.oceanprotocol.api.squid.dto.ProviderDto;
import com.oceanprotocol.keeper.contracts.OceanMarket;
import com.oceanprotocol.keeper.contracts.OceanRegistry;
import com.oceanprotocol.keeper.contracts.OceanToken;
import com.oceanprotocol.keeper.contracts.PLCRVoting;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.Contract;
import org.web3j.tx.gas.ContractGasProvider;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class BaseManager {

    private KeeperDto keeperDto;
    private ProviderDto providerDto;

    protected OceanToken tokenContract;
    protected PLCRVoting plcr;
    protected OceanRegistry oceanRegistry;
    protected OceanMarket oceanMarket;

    public BaseManager(KeeperDto keeperDto, ProviderDto providerDto) throws IOException, CipherException {
        this.keeperDto= keeperDto;
        this.providerDto= providerDto;
    }

    public KeeperDto getKeeperDto() {
        return keeperDto;
    }

    public BaseManager setKeeperDto(KeeperDto keeperDto) {
        this.keeperDto = keeperDto;
        return this;
    }

    public ProviderDto getProviderDto() {
        return providerDto;
    }

    public BaseManager setProviderDto(ProviderDto providerDto) {
        this.providerDto = providerDto;
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
    public BaseManager setTokenContract(OceanToken contract)    {
        this.tokenContract= contract;
        return this;
    }

    /**
     * It sets the PLCRVoting stub instance
     * @param contract PLCRVoting instance
     * @return BaseManager instance
     */
    public BaseManager setPLCRVotingContract(PLCRVoting contract)    {
        this.plcr= contract;
        return this;
    }

    /**
     * It sets the OceanRegistry stub instance
     * @param contract OceanRegistry instance
     * @return BaseManager instance
     */
    public BaseManager setOceanRegistryContract(OceanRegistry contract)    {
        this.oceanRegistry= contract;
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
        return "BaseManager{" +
                "keeperDto=" + keeperDto +
                ", providerDto=" + providerDto +
                '}';
    }
}
