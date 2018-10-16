package com.oceanprotocol.api.squid.dto;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;

public class KeeperDto {

    protected static final Logger log = LogManager.getLogger(KeeperDto.class);

    private Web3j web3 = null;
    private String keeperUrl;
    private String address;
    private String password;

    private BigInteger gasPrice;
    private BigInteger gasLimit;

    private final BigInteger DEFAULT_GAS_PRICE= BigInteger.valueOf(1500l);
    private final BigInteger DEFAULT_GAS_LIMIT= BigInteger.valueOf(250000l);


    /**
     * Initializes the KeeperDto object given a Keeper url, user and password
     * @param url Parity Keeper url (ie. http://localhost:8545)
     * @param address User ethereum address
     * @param password User password
     * @return KeeperDto
     */
    public static KeeperDto getInstance(String url, String address, String password) {
        return new KeeperDto(url, address, password);
    }

    public static KeeperDto getInstance(Web3jService web3jService) {
        return new KeeperDto(web3jService);
    }

    private KeeperDto(Web3jService web3jService)  {
        this.web3= Web3j.build(web3jService);
    }

    private KeeperDto(String url, String address, String password)    {
        log.debug("Initializing KeeperDto: " + url);
        this.address= address;
        this.password= password;
        this.keeperUrl= url;
        this.gasPrice= DEFAULT_GAS_PRICE;
        this.gasLimit= DEFAULT_GAS_LIMIT;
        this.web3 = Web3j.build(new HttpService(this.keeperUrl));
    }

    /**
     * Get the Web3j instance
     * @return web3j
     */
    public Web3j getWeb3()  {
        return web3;
    }

    public KeeperDto setGasPrice(BigInteger gasPrice) {
        this.gasPrice = gasPrice;
        return this;
    }

    public KeeperDto setGasLimit(BigInteger gasLimit) {
        this.gasLimit = gasLimit;
        return this;
    }
}
