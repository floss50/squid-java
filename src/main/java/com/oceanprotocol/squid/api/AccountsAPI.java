package com.oceanprotocol.squid.api;

import com.oceanprotocol.squid.exceptions.EthereumException;
import com.oceanprotocol.squid.models.Account;
import com.oceanprotocol.squid.models.Balance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.List;

/**
 *
 */
public interface AccountsAPI {

    public List<Account> list() throws EthereumException;
    public Balance balance(Account account) throws EthereumException;
    public TransactionReceipt requestTokens(BigInteger amount) throws EthereumException;

}
