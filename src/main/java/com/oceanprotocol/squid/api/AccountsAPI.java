package com.oceanprotocol.squid.api;

import com.oceanprotocol.squid.models.Account;
import com.oceanprotocol.squid.models.Balance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.List;

/**
 *
 */
// TODO SquidExceptions
public interface AccountsAPI {

    public List<Account> list();
    public Balance balance(Account account);
    public TransactionReceipt requestTokens(BigInteger amount);


}
