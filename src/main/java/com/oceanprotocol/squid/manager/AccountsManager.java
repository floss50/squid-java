package com.oceanprotocol.squid.manager;

import com.oceanprotocol.squid.dto.AquariusDto;
import com.oceanprotocol.squid.dto.KeeperDto;
import com.oceanprotocol.squid.models.Account;
import com.oceanprotocol.squid.models.Balance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.web3j.crypto.CipherException;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthAccounts;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller class to manage the token functions
 */
public class AccountsManager extends BaseManager {

    static final Logger log= LogManager.getLogger(AccountsManager.class);

    private BigInteger ERROR_BALANCE= BigInteger.ZERO;

    private AccountsManager(KeeperDto keeperDto, AquariusDto aquariusDto)
            throws IOException, CipherException {
        super(keeperDto, aquariusDto);
    }

    /**
     * Given the KeeperDto and AquariusDto, returns a new instance of AccountsManager
     * using them as attributes
     * @param keeperDto Keeper Dto
     * @param aquariusDto Provider Dto
     * @return AccountsManager
     */
    public static AccountsManager getInstance(KeeperDto keeperDto, AquariusDto aquariusDto)
            throws IOException, CipherException {
        return new AccountsManager(keeperDto, aquariusDto);
    }



    /**
     * Returns the list of ethereum accounts registered in the Keeper node
     * If getBalance is true, get the ethereum and ocean balance of each account
     * @return List<Account> List of accounts
     */
    public List<Account> getAccounts(boolean getBalance) throws IOException {
        EthAccounts ethAccounts = getKeeperDto().getWeb3().ethAccounts().send();

        List<Account> accounts= new ArrayList<>();
        for (String account: ethAccounts.getAccounts())    {
            accounts.add(new Account(account));
        }

        return accounts;
    }

    /**
     * Returns the list of ethereum accounts registered in the Keeper node
     * @return List<Account> List of accounts without Balance information
     */
    public List<Account> getAccounts() throws IOException {
        return getAccounts(false);
    }

    /**
     * Given an account returns a Balance object with the Ethereum and Ocean balance
     * @param accountName account
     * @return Balance
     */
    public Balance getAccountBalance(String accountName)    {
        return new Balance(
                getEthAccountBalance(accountName),
                getOceanAccountBalance(accountName)
        );
    }

    /**
     * Given an account returns the Ethereum balance
     * @param accountName account
     * @return ethereum balance
     */
    public BigInteger getEthAccountBalance(String accountName)    {
        try {
            return getKeeperDto()
                    .getWeb3()
                    .ethGetBalance(accountName, DefaultBlockParameterName.LATEST).send()
                    .getBalance();
        } catch (Exception ex)  {
            log.debug("Unable to get account(" + accountName + ") Ocean balance: " + ex.getMessage());
        }
        return ERROR_BALANCE;
    }

    /**
     * Given an account returns the Ocean balance
     * Contract: OceanToken
     * Method: balanceOf
     * @param accountName account
     * @return ocean balance
     */
    public BigInteger getOceanAccountBalance(String accountName)    {
        try{
            return tokenContract.balanceOf(accountName).send();
        } catch (Exception ex)  {
            log.debug("Unable to get account(" + accountName + ") Ocean balance: " + ex.getMessage());
        }
        return ERROR_BALANCE;
    }


    /**
     * Requests Ocean Tokens from the OceanMarket Smart Contract
     * Contract: OceanMarket
     * Method: requestTokens
     * @param amount amount of tokens requestsd
     * @return TransactionReceipt
     */
    public TransactionReceipt requestTokens(BigInteger amount)    {
        try{
            return oceanMarket.requestTokens(amount).send();
        } catch (Exception ex)  {
            log.error("Unable request tokens " + amount.intValue()+"  " + ex.getMessage());
        }
        return null;
    }

}
