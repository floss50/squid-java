package com.oceanprotocol.squid.api;

import com.oceanprotocol.squid.exceptions.EthereumOceanException;
import com.oceanprotocol.squid.models.Account;
import com.oceanprotocol.squid.models.Balance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.List;

/**
 *
 */
public interface AccountsAPI {

    public List<Account> list() throws EthereumOceanException;
    public Balance balance(Account account) throws EthereumOceanException;
    public TransactionReceipt requestTokens(BigInteger amount) throws EthereumOceanException;

}
